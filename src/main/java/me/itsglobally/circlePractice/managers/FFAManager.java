package me.itsglobally.circlePractice.managers;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.data.TempData;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public record FFAManager(CirclePractice plugin) {

    public void joinFFA(Player p) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
        if (pp.getState() != PracticePlayer.PlayerState.SPAWN) {
            MessageUtil.sendActionBar(p, "&cYou are not in the spawn!");
            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            return;
        }
        pp.setState(PracticePlayer.PlayerState.FFA);
        pp.saveInventory();
        spawn(p);
    }

    public void leaveFFA(Player p) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
        if (pp.getState() != PracticePlayer.PlayerState.FFA) {
            MessageUtil.sendActionBar(p, "&cYou are not in the ffa!");
            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            return;
        }
        pp.setState(PracticePlayer.PlayerState.SPAWN);
        plugin.getConfigManager().teleportToSpawn(p);
        pp.restoreInventory();
        plugin.getConfigManager().teleportToSpawn(p);
    }

    public void spawn(Player p) {
        try {
            teleportToFFASpawn(p);
        } catch (IllegalStateException e) {
            leaveFFA(p);
            return;
        }
        p.getInventory().clear();
        plugin.getKitManager().applyKit(p, "FFA");

    }

    public void kill(Player vic, Player klr) {
        if (vic == klr) {
            TempData.setKs(vic.getUniqueId(), 0L);
            return;
        }
        if (klr == null) return;

        TempData.setLastHit(vic.getUniqueId(), null);
        if (TempData.getLastHit(klr.getUniqueId()) == vic.getUniqueId()) TempData.setLastHit(klr.getUniqueId(), null);

        TempData.setKs(vic.getUniqueId(), 0);
        TempData.addKs(klr.getUniqueId(), 1);

        plugin.getFileDataManager().updateFfaStats(klr.getUniqueId(), 1, 0);
        plugin.getFileDataManager().updateFfaStats(vic.getUniqueId(), 0, 1);

        plugin.getEconomyManager().rewardKill(klr);

        MessageUtil.sendActionBar(klr, plugin.getPlayerManager().getPrefix(klr) + klr.getName() + "&rhas killed " + plugin.getPlayerManager().getPrefix(vic) + vic.getName() + "&r! (+&d10&r xp)");
        plugin.getFileDataManager().addXp(klr.getUniqueId(), 10);
        klr.setHealth(20.0);

        klr.playSound(klr.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

        plugin.getEconomyManager().addCoins(klr.getUniqueId(), TempData.getKs(klr.getUniqueId()) * 10);

        if (vic != null) {
            vic.playSound(vic.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
            MessageUtil.sendActionBar(vic, plugin.getPlayerManager().getPrefix(klr) + klr.getName() + "&rhas killed " + plugin.getPlayerManager().getPrefix(vic) + vic.getName() + "&r!");
        }

        long streak = TempData.getKs(klr.getUniqueId());
        if (streak >= 10 && streak % 5 == 0) {

            plugin.getEconomyManager().rewardKillstreak(klr, streak);

            for (Player op : Bukkit.getOnlinePlayers()) {
                op.playSound(op.getLocation(), Sound.ENDERDRAGON_GROWL, 0.75f, 2.0f);
            }
            Bukkit.broadcastMessage(plugin.getPlayerManager().getPrefix(klr) + klr.getName() + " §ahas reached " + streak + " §akillstreaks!");
        }

    }

    public Location randomSpawn() throws IllegalStateException {
        List<Location> spawns = plugin.getConfigManager().getFFASpawns();
        if (spawns.isEmpty()) {
            throw new IllegalStateException("No FFA spawns");
        }
        return spawns.get(new Random().nextInt(spawns.size()));
    }

    public void teleportToFFASpawn(Player p) throws IllegalStateException {
        p.teleport(TempData.getFfaCurrentSpawn());
    }

}
