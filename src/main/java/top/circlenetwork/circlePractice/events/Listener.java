package top.circlenetwork.circlePractice.events;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import top.circlenetwork.circlePractice.annotation.AutoListener;
import top.circlenetwork.circlePractice.data.*;
import top.circlenetwork.circlePractice.utils.Msg;

import java.util.*;

@AutoListener
public class Listener implements Global, org.bukkit.event.Listener {

    private final Map<Location, UUID> tntPlacers = new HashMap<>();
    private final Map<Location, UUID> lastIgnitedTnt = new HashMap<>();


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PracticePlayer pp = PracticePlayer.of(event.getPlayer());
        PreDefinedData.teleportSpawn(pp, "");
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

    @EventHandler(ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }

        PracticePlayer aPP = PracticePlayer.get(attacker.getUniqueId());

        if (aPP.getCurrentGame() == null) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        PracticePlayer vPP = PracticePlayer.get(victim.getUniqueId());

        if (vPP.getCurrentGame() == null
                || vPP.getCurrentGame() != aPP.getCurrentGame()) {
            event.setCancelled(true);
            return;
        }

        if (vPP.getState() != PracticePlayer.SpawnState.NOTSPAWN) {
            event.setCancelled(true);
            return;
        }

        Game game = vPP.getCurrentGame();

        if (game.isEnded() || !game.isStarted()) {
            event.setCancelled(true);
            return;
        }

        if (!game.getCanGetDamaged().get(attacker.getUniqueId())) {
            game.getCanGetDamaged().put(attacker.getUniqueId(), true);
            Msg.send(attacker, "&c你攻擊了其他玩家所以失去了重生保護!");
        }

        game.getLasthit().put(vPP.getUuid(), aPP.getUuid());
    }


    @EventHandler()
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
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
            if (game.getKit().getBoolean(Kit.KitOption.FREEZE)) {
                if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                    event.setTo(event.getFrom());
                }
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



            if (event.getBlockPlaced().getType() == Material.TNT) {
                event.setCancelled(true);
                event.getBlockPlaced().setType(Material.AIR);

                Location loc = event.getBlockPlaced().getLocation().add(0.5, 0, 0.5);
                TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);

                tnt.setFuseTicks(40);

                tnt.setMetadata("placer",
                        new FixedMetadataValue(plugin,
                                player.getUniqueId().toString())
                );
                tntPlacers.put(
                        event.getBlockPlaced().getLocation(),
                        event.getPlayer().getUniqueId()
                );
            }
        }
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        if (event.getBlock().getType() != Material.TNT) return;

        Location loc = event.getBlock().getLocation();
        UUID placer = tntPlacers.get(loc);

        if (placer != null) {
            lastIgnitedTnt.put(loc, placer);
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed tnt)) return;

        Location loc = event.getLocation().getBlock().getLocation();
        UUID placer = lastIgnitedTnt.remove(loc);

        if (placer == null) return;

        tnt.setMetadata(
                "placer",
                new FixedMetadataValue(plugin, placer.toString())
        );
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


    @EventHandler
    public void onFireball(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getItemInHand() == null ||
                player.getItemInHand().getType() != Material.FIREBALL) {
            return;
        }

        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();
        Location spawnLoc = eyeLoc.clone().add(direction.multiply(1.2));

        Fireball fireball = player.getWorld().spawn(spawnLoc, Fireball.class);

        fireball.setShooter(player);
        fireball.setVelocity(direction.multiply(1.5));
        fireball.setYield(2.0f);
        fireball.setIsIncendiary(true);
        event.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {

        Entity entity = event.getEntity();
        Player player = null;

        if (entity instanceof Fireball fireball) {
            if (fireball.getShooter() instanceof Player) {
                player = (Player) fireball.getShooter();
            }
        }

        if (entity instanceof TNTPrimed tnt) {
            if (tnt.hasMetadata("placer")) {
                UUID uuid = UUID.fromString(
                        tnt.getMetadata("placer").getFirst().asString()
                );
                player = Bukkit.getPlayer(uuid);
            }
        }


        if (player == null) {
            event.setCancelled(true);
            return;
        }

        PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());
        Game game = practicePlayer.getCurrentGame();

        if (game == null) {
            event.setCancelled(true);
            return;
        }

        List<Block> blocks = event.blockList();
        Iterator<Block> iterator = blocks.iterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            Location loc = block.getLocation();

            if (game.getGameHandler().isNearAnyBed(loc)) {
                if (!game.getKit().getAllowedBreakBlocksAroundBed().contains(block.getType())) {
                    iterator.remove();
                    continue;
                }
            }

            if (game.getKit().getBoolean(Kit.KitOption.BUILD)) {
                UUID uuid = player.getUniqueId();
                if (!game.getBlocks().get(uuid).contains(loc)) {
                    iterator.remove();
                    continue;
                }
            }

            if (block.getType() == Material.BED) {
                iterator.remove();
                continue;
            }

            game.getBlocks().get(player.getUniqueId()).remove(loc);
        }
    }

}
