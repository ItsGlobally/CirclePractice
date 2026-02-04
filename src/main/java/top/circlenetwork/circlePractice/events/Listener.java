package top.circlenetwork.circlePractice.events;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.circlenetwork.circlePractice.annotation.AutoListener;
import top.circlenetwork.circlePractice.data.Arena;
import top.circlenetwork.circlePractice.data.Game;
import top.circlenetwork.circlePractice.data.Kit;
import top.circlenetwork.circlePractice.data.PracticePlayer;
import top.circlenetwork.circlePractice.utils.Msg;

import java.util.Collection;
import java.util.UUID;

@AutoListener
public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PracticePlayer pp = PracticePlayer.of(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        PracticePlayer practicePlayer = PracticePlayer.get(event.getPlayer().getUniqueId());

        Game game = practicePlayer.getCurrentGame();
        if (game != null) {
            if (game.getGameHandler().isRed(practicePlayer)) {
                if (game.getRed().size() - 1 == 0) game.getGameHandler().end(false);
                game.getRed().remove(practicePlayer.getUuid());
            } else {
                if (game.getBlue().size() - 1 == 0) game.getGameHandler().end(true);
                game.getBlue().remove(practicePlayer.getUuid());
            }
        }

        PracticePlayer.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            event.setCancelled(true);
            return;
        }
        if (!(event.getDamager() instanceof Player attacker)) {
            event.setCancelled(true);
            return;
        }

        PracticePlayer vPP = PracticePlayer.get(victim.getUniqueId());
        PracticePlayer aPP = PracticePlayer.get(attacker.getUniqueId());

        if (vPP.getState() != PracticePlayer.SpawnState.NOTSPAWN) {
            event.setCancelled(true);
            return;
        }

        if (vPP.getCurrentGame() == null
                || aPP.getCurrentGame() == null
                || vPP.getCurrentGame() != aPP.getCurrentGame()) {
            event.setCancelled(true);
            return;
        }

        Game game = vPP.getCurrentGame();
        if (!game.getCanGetDamaged().get(attacker.getUniqueId())) {
            game.getCanGetDamaged().put(attacker.getUniqueId(), true);
            Msg.send(attacker, "&c你攻擊了其他玩家所以失去了重生保護!");
        }
        game.getLasthit().put(vPP.getUuid(), aPP.getUuid());
    }

    @EventHandler()
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            event.setCancelled(true);
            return;
        }

        PracticePlayer pp = PracticePlayer.get(player.getUniqueId());

        if (pp.getCurrentGame() == null) return;
        Game game = pp.getCurrentGame();

        if (!game.getCanGetDamaged().get(pp.getUuid())) {
            event.setCancelled(true);
            return;
        }

        if (player.getHealth() - event.getFinalDamage() > 0) return;

        event.setCancelled(true);

        UUID lastHit = game.getLasthit().get(pp.getUuid());
        PracticePlayer killer = PracticePlayer.get(lastHit);
        if (lastHit != null && killer != null) {
            game.getGameHandler().death(pp, killer, false);
        } else {
            game.getGameHandler().death(pp, null, false);
        }
    }


    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());

        if (practicePlayer.getCurrentGame() == null) return;

        Game game = practicePlayer.getCurrentGame();

        if (!game.isStarted()) {
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());
            }
            return;
        }

        if (player.getLocation().getY() <= game.getGameArena().arena().getInt(Arena.ArenaOption.VOID)) {
            if (game.getLasthit().get(practicePlayer.getUuid()) != null) {
                game.getGameHandler().death(practicePlayer, PracticePlayer.get(game.getLasthit().get(practicePlayer.getUuid())), true);
            } else {
                game.getGameHandler().death(practicePlayer, null, true);
            }
        }
    }

    @EventHandler
    public void BlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());
        if (practicePlayer == null) return;

        if (practicePlayer.getState() != PracticePlayer.SpawnState.NOTSPAWN && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }
        if (practicePlayer.getCurrentGame() != null) {
            Game game = practicePlayer.getCurrentGame();

            if (!game.getKit().getBoolean(Kit.KitOption.BUILD)) {
                event.setCancelled(true);
                return;
            }

            game.getBlocks().get(player.getUniqueId()).add(event.getBlockPlaced().getLocation());
        }
    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Bukkit.getLogger().info(player.getName() + " destoryed " + event.getBlock().getType());
        PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());
        if (practicePlayer == null) return;

        if (practicePlayer.getState() != PracticePlayer.SpawnState.NOTSPAWN && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }
        if (practicePlayer.getCurrentGame() != null) {
            Game game = practicePlayer.getCurrentGame();

            if (!game.getKit().getBoolean(Kit.KitOption.BUILD)) {
                event.setCancelled(true);
                return;
            }

            if (event.getBlock().getType() == Material.BED_BLOCK) {

                Location loc = event.getBlock().getLocation();
                boolean isEnemyBed = game.getGameHandler().getIsEnemyBed(practicePlayer, loc);
                boolean isOwnBed = game.getGameHandler().getIsOwnBed(practicePlayer, loc);

                if (isEnemyBed) {
                    event.setCancelled(true);
                    loc.getBlock().setType(Material.AIR);
                    Collection<PracticePlayer> enemyTeam = game.getRed().values();
                    if (game.getRed().containsKey(practicePlayer.getUuid())) enemyTeam = game.getBlue().values();

                    for (PracticePlayer enemy : enemyTeam) {
                        game.getRespawnable().put(enemy.getUuid(), false);

                        enemy.getPlayer().playSound(
                                enemy.getPlayer().getLocation(),
                                Sound.WITHER_DEATH,
                                1.0f,
                                1.0f
                        );
                    }

                    game.broadcast(
                            "&d&lBED DESTROYED &f» " +
                                    (game.getGameHandler().isRed(practicePlayer) ? "&9藍隊" : "&c紅隊") +
                                    " &f的床已被" +
                                    player.getName() + "&f破壞!"
                    );

                    player.playSound(
                            player.getLocation(),
                            Sound.ENDERDRAGON_GROWL,
                            1.0f,
                            1.0f
                    );

                    event.setCancelled(false);
                    return;
                }

                if (isOwnBed) {
                    event.setCancelled(true);
                    Msg.send(player, "&c你不能挖自己的床!");
                    return;
                }

                event.setCancelled(true);
                return;
            }
            if (game.getGameHandler().isNearAnyBed(event.getBlock().getLocation())) {
                if (!game.getKit().getAllowedBreakBlocksAroundBed().contains(event.getBlock().getType())) {
                    event.setCancelled(true);
                    Msg.send(player, "&c你不能破壞這裡的方塊");
                }
                return;
            }
            if (game.getKit().getBoolean(Kit.KitOption.BUILD)) {
                if (!game.getBlocks().get(player.getUniqueId()).contains(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                    Msg.send(player, "&c你不能破壞這裡的方塊");
                }
            }
            game.getBlocks().get(player.getUniqueId()).remove(event.getBlock().getLocation());

        }
    }
}
