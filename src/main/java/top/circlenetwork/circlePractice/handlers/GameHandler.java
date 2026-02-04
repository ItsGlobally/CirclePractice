package top.circlenetwork.circlePractice.handlers;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import top.circlenetwork.circlePractice.data.*;
import top.circlenetwork.circlePractice.utils.Msg;
import top.circlenetwork.circlePractice.utils.NoteBlockUtil;
import top.circlenetwork.circlePractice.utils.TeamColorUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class GameHandler implements Global {

    private final Game game;

    // instance

    public static Game startGame(HashMap<UUID, PracticePlayer> red, HashMap<UUID, PracticePlayer> blue, Kit kit) {
        GameArena arena = GameArena.createGameArena(kit);
        if (arena == null) {
            Msg.warn("沒有被選中的arena");
            return null;
        }
        Game ng = new Game(kit, arena, red, blue, red, blue);
        for (PracticePlayer practicePlayer : ng.getRed().values()) {
            Player player = practicePlayer.getPlayer();
            ng.getCanGetDamaged().put(player.getUniqueId(), false);
            ng.getRespawning().put(player.getUniqueId(), false);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getInventory().setArmorContents(TeamColorUtil.colorTeamItems(kit.getArmor(), true));
            player.getInventory().setContents(TeamColorUtil.colorTeamItems(kit.getInventory(), true));
            player.teleport(arena.redSpawn());
            practicePlayer.setState(PracticePlayer.SpawnState.NOTSPAWN);
            practicePlayer.setQueuedKit(null);
            practicePlayer.setCurrentGame(ng);
            if (kit.getBoolean(Kit.KitOption.BED)) ng.getRespawnable().put(player.getUniqueId(), true);
            ng.getBlocks().put(player.getUniqueId(), new ArrayList<>());
        }
        for (PracticePlayer practicePlayer : ng.getBlue().values()) {
            Player player = practicePlayer.getPlayer();
            ng.getCanGetDamaged().put(player.getUniqueId(), false);
            ng.getRespawning().put(player.getUniqueId(), false);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getInventory().setArmorContents(TeamColorUtil.colorTeamItems(kit.getArmor(), false));
            player.getInventory().setContents(TeamColorUtil.colorTeamItems(kit.getInventory(), false));
            player.teleport(arena.blueSpawn());
            practicePlayer.setState(PracticePlayer.SpawnState.NOTSPAWN);
            practicePlayer.setQueuedKit(null);
            practicePlayer.setCurrentGame(ng);
            if (kit.getBoolean(Kit.KitOption.BED)) ng.getRespawnable().put(player.getUniqueId(), true);
            ng.getBlocks().put(player.getUniqueId(), new ArrayList<>());
        }


        final int[] startTime = {5};

        new BukkitRunnable() {

            @Override
            public void run() {
                if (ng.isEnded()) cancel();

                if (startTime[0] <= 0) {
                    ng.setStarted(true);
                    for (PracticePlayer practicePlayer : ng.getRed().values()) {
                        Player player = practicePlayer.getPlayer();
                        ng.getCanGetDamaged().put(player.getUniqueId(), true);
                        Msg.send(player, "&a遊戲開始!");
                    }
                    for (PracticePlayer practicePlayer : ng.getBlue().values()) {
                        Player player = practicePlayer.getPlayer();
                        ng.getCanGetDamaged().put(player.getUniqueId(), true);
                        Msg.send(player, "&a遊戲開始!");
                    }
                    cancel();
                    return;
                }
                for (PracticePlayer practicePlayer : ng.getRed().values()) {
                    Player player = practicePlayer.getPlayer();
                    if (startTime[0] == 3) NoteBlockUtil.glass(player, 20);
                    if (startTime[0] == 2) NoteBlockUtil.glass(player, 18);
                    if (startTime[0] == 1) NoteBlockUtil.glass(player, 15);
                    Msg.send(player, "&e遊戲在" + startTime[0] + "內開始...");
                }
                for (PracticePlayer practicePlayer : ng.getBlue().values()) {
                    Player player = practicePlayer.getPlayer();
                    if (startTime[0] == 3) NoteBlockUtil.glass(player, 20);
                    if (startTime[0] == 2) NoteBlockUtil.glass(player, 18);
                    if (startTime[0] == 1) NoteBlockUtil.glass(player, 15);
                    Msg.send(player, "&e遊戲在" + startTime[0] + "秒後開始...");
                }

                startTime[0]--;
            }
        }.runTaskTimer(plugin, 0L, 20L);


        return ng;
    }

    private String teamPrefixedName(PracticePlayer practicePlayer) {
        if (isRed(practicePlayer)) {
            return "&c" + practicePlayer.getName();
        } else {
            return "&9" + practicePlayer.getName();
        }
    }

    private Location findSpawnpoint(Location l) {

        if (l.getBlock().getType() != Material.AIR) {
            return l.clone().add(0, 1, 0);
        }
        l.clone().add(0, 2, 0).getBlock().setType(Material.AIR);
        l.clone().add(0, 3, 0).getBlock().setType(Material.AIR);
        return l;
    }

    public boolean isNear(Location loc1, Location loc2, int radius) {
        return Math.abs(loc1.getBlockX() - loc2.getBlockX()) <= radius &&
                Math.abs(loc1.getBlockY() - loc2.getBlockY()) <= radius &&
                Math.abs(loc1.getBlockZ() - loc2.getBlockZ()) <= radius;
    }
    public boolean isBedNear(Location bedBase, Location loc) {
        Location head = bedBase.clone();
        Location footX = bedBase.clone().add(1, 0, 0);
        Location footZ = bedBase.clone().add(0, 0, 1);

        return isNear(loc, head, 2)
                || isNear(loc, footX, 2)
                || isNear(loc, footZ, 2);
    }

    public boolean isNearAnyBed(Location loc) {
        if (isBedNear(game.getGameArena().redBed(), loc)) return true;
        return isBedNear(game.getGameArena().blueBed(), loc);
    }


    public boolean getIsEnemyBed(PracticePlayer pp, Location loc) {
        Location enemyBed;

        if (game.getRed().containsKey(pp.getUuid())) {
            enemyBed = game.getGameArena().blueBed();
        } else if (game.getBlue().containsKey(pp.getUuid())) {
            enemyBed = game.getGameArena().redBed();
        } else {
            return false;
        }

        return isBedNear(enemyBed, loc);
    }

    public boolean getIsOwnBed(PracticePlayer pp, Location loc) {
        Location ownBed;

        if (game.getRed().containsKey(pp.getUuid())) {
            ownBed = game.getGameArena().redBed();
        } else if (game.getBlue().containsKey(pp.getUuid())) {
            ownBed = game.getGameArena().blueBed();
        } else {
            return false;
        }

        return isBedNear(ownBed, loc);
    }

    public boolean isRed(PracticePlayer practicePlayer) {
        return game.getRed().containsKey(practicePlayer.getUuid());
    }

    public void death(PracticePlayer victim, PracticePlayer killer, boolean walkedOff) {
        if (game.isEnded()) return;
        if (victim == null) return;
        Player victimPlayer = victim.getPlayer();
        Player killerPlayer;
        String killMessage;
        if (killer == null) {
            if (walkedOff) {
                killMessage = teamPrefixedName(victim) + "&r 自己走了下去" + (game.getRespawnable().getOrDefault(victim.getUuid(), false) ? "" : " &b最終擊殺!");
            } else {
                killMessage = teamPrefixedName(victim) + "&r 不知道為什麼死了" + (game.getRespawnable().getOrDefault(victim.getUuid(), false) ? "" : " &b最終擊殺!");
            }
        } else {
            if (walkedOff) {
                killMessage = teamPrefixedName(victim) + "&r 在 " + teamPrefixedName(killer) + "&r 的幫助下走了下去" + (game.getRespawnable().getOrDefault(victim.getUuid(), false) ? "" : "&b最終擊殺!");
            } else {
                killMessage = teamPrefixedName(victim) + "&r 被 " + teamPrefixedName(killer) + "&r 擊殺了" + (game.getRespawnable().getOrDefault(victim.getUuid(), false) ? "" : " &b最終擊殺!");
            }
        }
        game.getLasthit().put(victim.getUuid(), null);
        game.broadcast(killMessage);
        if (!game.getRespawnable().getOrDefault(victim.getUuid(), false)) {
            if (isRed(victim)) {
                if (game.getRed().size() - 1 == 0) end(false);
                game.getRed().remove(victim.getUuid());
            } else {
                if (game.getBlue().size() - 1 == 0) end(true);
                game.getBlue().remove(victim.getUuid());
            }
        } else {
            game.getCanGetDamaged().put(victim.getUuid(), false);
            game.getRespawning().put(victim.getUuid(), true);
            victimPlayer.teleport(isRed(victim) ? game.getGameArena().redSpawn() : game.getGameArena().blueSpawn());
            for (PracticePlayer practicePlayer : game.getRed().values()) {
                Player player = practicePlayer.getPlayer();
                player.hidePlayer(victimPlayer);
            }
            for (PracticePlayer practicePlayer : game.getBlue().values()) {
                Player player = practicePlayer.getPlayer();
                player.hidePlayer(victimPlayer);
            }

            final int[] respawnTime = {game.getKit().getInt(Kit.KitOption.RESPAWNTIME)};
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (game.isEnded()) return;
                    if (respawnTime[0] <= 0) {
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
                        victimPlayer.teleport(isRed(victim) ? findSpawnpoint(game.getGameArena().redSpawn()) : findSpawnpoint(game.getGameArena().blueSpawn()));
                        game.getRespawning().put(victim.getUuid(), false);
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                game.getCanGetDamaged().put(victim.getUuid(), true);
                            }
                        }.runTaskLater(plugin, 30L);

                        cancel();
                    }
                    Msg.send(victimPlayer, "&c在" + respawnTime[0] + "後重生...");
                    respawnTime[0]--;
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }

    public void end(boolean redWon) {
        if (game.isEnded()) return;
        game.setEnded(true);
        StringBuilder sb = new StringBuilder("&m                              \n");
        sb.append("&a贏家: &r");
        if (redWon) {
            for (PracticePlayer practicePlayer1 : game.getAllRed().values()) {
                sb.append(practicePlayer1.getPlayer().getName()).append(", ");
            }
            if (!game.getAllRed().isEmpty()) sb.setLength(sb.length() - 2);
            sb.append("\n&c輸家: &r");
            for (PracticePlayer practicePlayer1 : game.getAllBlue().values()) {
                sb.append(practicePlayer1.getPlayer().getName()).append(", ");
            }
            if (!game.getAllBlue().isEmpty()) sb.setLength(sb.length() - 2);
        } else {
            for (PracticePlayer practicePlayer1 : game.getAllBlue().values()) {
                sb.append(practicePlayer1.getPlayer().getName()).append(", ");
            }
            if (!game.getAllBlue().isEmpty()) sb.setLength(sb.length() - 2);
            sb.append("\n&c輸家: &r");
            for (PracticePlayer practicePlayer1 : game.getAllRed().values()) {
                sb.append(practicePlayer1.getPlayer().getName()).append(", ");
            }
            if (!game.getAllRed().isEmpty()) sb.setLength(sb.length() - 2);
        }
        sb.append("\n&m                              ");

        for (PracticePlayer practicePlayer : game.getRed().values()) {
            if (practicePlayer.getPlayer() == null) return;
            teleportSpawn(practicePlayer, sb.toString());
        }
        for (PracticePlayer practicePlayer : game.getBlue().values()) {
            if (practicePlayer.getPlayer() == null) return;
            teleportSpawn(practicePlayer, sb.toString());
        }

        World world = game.getGameArena().redSpawn().getWorld();

        for (Player player : world.getPlayers()) {
            if (player == null || PracticePlayer.get(player.getUniqueId()) == null) continue;
            teleportSpawn(PracticePlayer.get(player.getUniqueId()), sb.toString());
        }
    }
    private void teleportSpawn(PracticePlayer practicePlayer, String msg) {
        Player player = practicePlayer.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFireTicks(0);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(new ItemStack[36]);
        player.teleport(PreDefinedData.spawnLocation);
        practicePlayer.setState(PracticePlayer.SpawnState.SPAWN);
        practicePlayer.setCurrentGame(null);
        Msg.send(player, msg);
    }

}
