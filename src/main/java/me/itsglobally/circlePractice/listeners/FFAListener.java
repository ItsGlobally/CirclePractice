package me.itsglobally.circlePractice.listeners;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.data.TempData;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.ArrayList;
import java.util.List;

@AutoListener
public class FFAListener implements Listener {

    private final CirclePractice plugin = CirclePractice.getInstance();
    List<Location> blockplaced = new ArrayList<>();


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        PracticePlayer pP = plugin.getPlayerManager().getPlayer(player);
        if (pP.getState() == PracticePlayer.PlayerState.FFA) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
                return;
            }
            if (e instanceof EntityDamageByEntityEvent edbee) {
                Entity dmgere = edbee.getDamager();
                Entity vice = e.getEntity();
                if (!(vice instanceof Player vic)) {
                    e.setCancelled(true);
                    return;
                }
                if (!(dmgere instanceof Player dmger)) {
                    e.setCancelled(true);
                    return;
                }
                if (vic.getLocation().getY() <= 200 || dmger.getLocation().getY() <= 200) {
                    e.setCancelled(true);
                    return;
                }
                TempData.setLastHit(vic.getUniqueId(), dmger.getUniqueId());
                TempData.setLastHit(dmger.getUniqueId(), vic.getUniqueId());
                if (vic.getHealth() < e.getFinalDamage()) {
                    e.setCancelled(true);
                    plugin.getFFAManager().kill(vic, dmger);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        PracticePlayer pP = plugin.getPlayerManager().getPlayer(player);
        if (pP.getState() == PracticePlayer.PlayerState.FFA) {
            if (e.getBlockPlaced().getY() <= 200) {
                e.setCancelled(true);
                MessageUtil.sendActionBar(player, "&cYou cannot place blocks here!");
            }
            Material against = e.getBlockAgainst().getType();
            if (against == Material.WATER || against == Material.STATIONARY_WATER || against == Material.LAVA || against == Material.STATIONARY_LAVA) {
                MessageUtil.sendActionBar(player, "&cYou cannot place blocks here!");
                e.setCancelled(true);
                return;
            }
            blockplaced.add(e.getBlockPlaced().getLocation());
            new BukkitRunnable() {
                @Override
                public void run() {
                    e.getBlockPlaced().setType(Material.AIR);
                    blockplaced.remove(e.getBlockPlaced().getLocation());
                }
            }.runTaskLater(plugin, 8 * 20L);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        PracticePlayer pP = plugin.getPlayerManager().getPlayer(player);
        if (pP.getState() == PracticePlayer.PlayerState.FFA) {
            if (e.getBlock().getY() <= 200) {
                e.setCancelled(true);
                MessageUtil.sendActionBar(player, "&cYou cannot break blocks here!");
            }
            if (!blockplaced.contains(e.getBlock().getLocation())) {
                e.setCancelled(true);
                MessageUtil.sendActionBar(player, "&cYou cannot break this block!");
            }
            blockplaced.remove(e.getBlock().getLocation());
        }
    }
}
