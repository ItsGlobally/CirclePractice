package top.circlenetwork.circlePractice.handlers;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import top.circlenetwork.circlePractice.data.*;
import top.circlenetwork.circlePractice.utils.Msg;
import top.circlenetwork.circlePractice.utils.TeamColorUtil;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class GameHandler implements Global {

    public static Game startGame(HashMap<UUID, PracticePlayer> red, HashMap<UUID, PracticePlayer> blue, Kit kit) {
        GameArena arena = GameArena.createGameArena(kit);
        if (arena == null) return null;
        Game ng = new Game(kit, arena, red, blue);
        for (PracticePlayer practicePlayer : ng.getRed().values()) {
            Player player = practicePlayer.getPlayer();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getInventory().setArmorContents(TeamColorUtil.colorTeamItems(kit.getArmor(), true));
            player.getInventory().setContents(TeamColorUtil.colorTeamItems(kit.getInventory(), true));
            player.teleport(arena.redSpawn());
        }
        for (PracticePlayer practicePlayer : ng.getBlue().values()) {
            Player player = practicePlayer.getPlayer();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getInventory().setArmorContents(TeamColorUtil.colorTeamItems(kit.getArmor(), false));
            player.getInventory().setContents(TeamColorUtil.colorTeamItems(kit.getInventory(), false));
            player.teleport(arena.blueSpawn());
        }


        return ng;
    }

    // instance

    private final Game game;

    private String teamPrefixedName(PracticePlayer practicePlayer) {
        if (isRed(practicePlayer)) {
            return "&c" + practicePlayer.getName();
        } else {
            return "&9" + practicePlayer.getName();
        }
    }

    private boolean isRed(PracticePlayer practicePlayer) {
        return game.getRed().containsKey(practicePlayer.getUuid());
    }
    public void death(PracticePlayer victim, PracticePlayer killer, boolean walkedOff) {
        if (victim == null) return;
        Player victimPlayer = victim.getPlayer();
        Player killerPlayer;
        String killMessage;
        if (killer == null) {
            if (walkedOff) {
                killMessage = teamPrefixedName(victim) + "&r walked off." + (game.getRespawnable().getOrDefault(victim.getUuid(), false) ? "" : " &bFINAL KILL!");
            } else {
                killMessage = teamPrefixedName(victim) + "&r died for no reason." + (game.getRespawnable().getOrDefault(victim.getUuid(), false) ? "" : " &bFINAL KILL!");
            }
        } else {
            if (walkedOff) {
                killMessage = teamPrefixedName(victim) + "&r walked off because of " + teamPrefixedName(killer) + "&r's help." + (game.getRespawnable().getOrDefault(victim.getUuid(), false) ? "" : " &bFINAL KILL!");
            } else {
                killMessage = teamPrefixedName(victim) + "&r was killed by " + teamPrefixedName(killer) + "&r." + (game.getRespawnable().getOrDefault(victim.getUuid(), false) ? "" : " &bFINAL KILL!");
            }
        }
        if (!game.getRespawnable().getOrDefault(victim.getUuid(), false)) {
            if (isRed(victim)) {
                if (game.getRed().size() - 1 == 0) end(false);
                game.getRed().remove(victim.getUuid());
            } else {
                if (game.getBlue().size() - 1 == 0) end(true);
                game.getBlue().remove(victim.getUuid());
            }
        } else {
            victimPlayer.teleport(isRed(victim) ? game.getGameArena().redSpawn() : game.getGameArena().blueSpawn());
            for (PracticePlayer practicePlayer : game.getRed().values()) {
                Player player = practicePlayer.getPlayer();
                player.hidePlayer(victimPlayer);
            }
            for (PracticePlayer practicePlayer : game.getBlue().values()) {
                Player player = practicePlayer.getPlayer();
                player.hidePlayer(victimPlayer);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (PracticePlayer practicePlayer : game.getRed().values()) {
                        Player player = practicePlayer.getPlayer();
                        player.showPlayer(victimPlayer);
                    }
                    for (PracticePlayer practicePlayer : game.getBlue().values()) {
                        Player player = practicePlayer.getPlayer();
                        player.showPlayer(victimPlayer);
                    }
                    victimPlayer.setHealth(20);
                    victimPlayer.setFoodLevel(20);
                    victimPlayer.setSaturation(20);
                    victimPlayer.setFireTicks(0);
                    victimPlayer.setFlying(false);
                    victimPlayer.setAllowFlight(false);
                    victimPlayer.getInventory().setArmorContents(TeamColorUtil.colorTeamItems(game.getKit().getArmor(), false));
                    victimPlayer.getInventory().setContents(TeamColorUtil.colorTeamItems(game.getKit().getInventory(), false));
                    victimPlayer.teleport(isRed(victim) ? game.getGameArena().redSpawn() : game.getGameArena().blueSpawn());
                }
            }.runTaskLater(plugin, 20L * game.getKit().getInt(Kit.KitOption.RESPAWNTIME));
        }
        game.broadcast(killMessage);
    }

    public void end(boolean redWon) {
        StringBuilder sb = new StringBuilder("&m                              \n");
        sb.append("&aWinner: &r");
        if (redWon) {
            for (PracticePlayer practicePlayer1 : game.getAllRed().values()) {
                sb.append(practicePlayer1.getPlayer().getName()).append(", ");
            }
            if (!game.getAllRed().isEmpty()) sb.setLength(sb.length() - 2);
            sb.append("&cLoser: &r");
            for (PracticePlayer practicePlayer1 : game.getAllBlue().values()) {
                sb.append(practicePlayer1.getPlayer().getName()).append(", ");
            }
            if (!game.getAllBlue().isEmpty()) sb.setLength(sb.length() - 2);
        } else {
            for (PracticePlayer practicePlayer1 : game.getAllBlue().values()) {
                sb.append(practicePlayer1.getPlayer().getName()).append(", ");
            }
            if (!game.getAllBlue().isEmpty()) sb.setLength(sb.length() - 2);
            sb.append("&cLoser: &r");
            for (PracticePlayer practicePlayer1 : game.getAllRed().values()) {
                sb.append(practicePlayer1.getPlayer().getName()).append(", ");
            }
            if (!game.getAllRed().isEmpty()) sb.setLength(sb.length() - 2);
        }

        for (PracticePlayer practicePlayer : game.getRed().values()) {
            teleportSpawn(practicePlayer, sb.toString());
        }
        for (PracticePlayer practicePlayer : game.getBlue().values()) {
            teleportSpawn(practicePlayer, sb.toString());
        }
    }

    private void teleportSpawn(PracticePlayer practicePlayer, String msg) {
        Player player = practicePlayer.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFireTicks(0);
        player.setFlying(true);
        player.setAllowFlight(true);
        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(new ItemStack[36]);
        player.teleport(PreDefinedData.spawnLocation);
        Msg.send(player, msg);
    }

}
