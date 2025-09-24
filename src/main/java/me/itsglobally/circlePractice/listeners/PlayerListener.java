package me.itsglobally.circlePractice.listeners;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.data.TempData;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class PlayerListener implements Listener {

    private final CirclePractice plugin = CirclePractice.getInstance();

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent e) {
        Player p = e.getPlayer();
        EntityDamageEvent event = p.getLastDamageCause();
        if (event != null && !event.isCancelled() && event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if (damager instanceof Arrow) {
                if (((Arrow) damager).getShooter().equals(p)) {
                    Vector velocity = e.getVelocity();
                    double speed = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
                    Vector dir = damager.getLocation().getDirection().normalize();
                    Vector vector = new Vector(dir.getX() * speed * -1.0D, velocity.getY(), dir.getZ() * speed);
                    e.setVelocity(vector);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("circlepractice.fly")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        plugin.getConfigManager().teleportToSpawn(player);
        plugin.getPlayerManager().addPlayer(player);
        MessageUtil.sendTitle(player, "&cThis server is still in DEVELOPMENT!", "&aFeel free to report any bugs!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);

        if (practicePlayer != null) {

            if (practicePlayer.isInQueue()) {
                plugin.getQueueManager().leaveQueue(player);
            }

            if (practicePlayer.isInDuel()) {
                plugin.getDuelManager().endDuel(practicePlayer.getCurrentDuel(),
                        practicePlayer.getCurrentDuel().getOpponent(practicePlayer));
            }
            if (practicePlayer.isInFFA()) {
                plugin.getFFAManager().kill(player, Bukkit.getPlayer(TempData.getLastHit(player.getUniqueId())));
            }
        }

        plugin.getPlayerManager().removePlayer(player.getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer().getLocation().getY() <= 0 && plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId()).isInSpawn()) plugin.getConfigManager().teleportToSpawn(e.getPlayer());
    }
    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        e.setFormat(MessageUtil.formatMessage(
                plugin.getFileDataManager().getStars(e.getPlayer().getUniqueId()) + " " +
                        plugin.getPlayerManager().getPrefixedName(e.getPlayer()) +
                        "&r » %2$s"
        ));
    }


    @EventHandler
    public void onHungry(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player p)) {
            return;
        }
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
        if (!pp.isInDuel()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        PracticePlayer pP = plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (pP.getState() == PracticePlayer.PlayerState.SPAWN || pP.isInFFA()) {
            if (TempData.getBuild(e.getPlayer().getUniqueId())) {
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
        PracticePlayer pP = plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (pP.isInSpawn()) {
            if (TempData.getBuild(e.getPlayer().getUniqueId())) {
                return;
            }
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        if (plugin.getPlayerManager().getPlayer(p.getUniqueId()).isInSpawn()) e.setCancelled(true);
    }
    @EventHandler
    public void onDmg(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) e.setCancelled(true);
    }
    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() == Material.POTION) {
            event.getPlayer().getServer().getScheduler().runTaskLater(
                    plugin,
                    () -> {
                        event.getPlayer().getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE, 1));
                    },
                    1L
            );
        }
    }

}