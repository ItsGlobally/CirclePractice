package me.itsglobally.circlePractice.managers;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.data.TempData;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public record FFAManager(CirclePractice plugin) {

    public void joinFFA(Player p) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
        if (pp.getState() != PracticePlayer.PlayerState.SPAWN) {
            MessageUtil.sendActionBar(p, "&cYou are not in the spawn!");
            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            return;
        }
        pp.setState(PracticePlayer.PlayerState.FFA);
        spawn(p);
    }

    public void leaveFFA(Player p) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
        if (pp.getState() != PracticePlayer.PlayerState.FFA) {
            MessageUtil.sendActionBar(p, "&cYou are not in the ffa!");
            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            return;
        }
        pp.setState(PracticePlayer.PlayerState.FFA);
        plugin.getConfigManager().teleportToSpawn(p);
        pp.restoreInventory(p);
        plugin.getConfigManager().teleportToSpawn(p);
    }

    public void spawn(Player p) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
        pp.saveInventory(p);
        plugin.getKitManager().applyKit(p, "FFA");
        plugin.getConfigManager().teleportToFFASpawn(p);

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

        // Reward coins for kill
        plugin.getEconomyManager().rewardKill(klr);

        MessageUtil.sendActionBar(klr, plugin.getPlayerManager().getPrefix(klr) + klr.getName() + "&rhas killed " + plugin.getPlayerManager().getPrefix(vic) + vic.getName() + "&r!");

        klr.setHealth(20.0);

        klr.playSound(klr.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);


        if (vic != null) {
            vic.playSound(vic.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
            MessageUtil.sendActionBar(vic, plugin.getPlayerManager().getPrefix(klr) + klr.getName() + "&rhas killed " + plugin.getPlayerManager().getPrefix(vic) + vic.getName() + "&r!");
        }

        long streak = TempData.getKs(klr.getUniqueId());
        if (streak >= 10 && streak % 5 == 0) {
            // Reward killstreak bonus
            plugin.getEconomyManager().rewardKillstreak(klr, streak);

            for (Player op : Bukkit.getOnlinePlayers()) {
                op.playSound(op.getLocation(), Sound.ENDERDRAGON_GROWL, 0.75f, 2.0f);
            }
            Bukkit.broadcastMessage(plugin.getPlayerManager().getPrefix(klr) + klr.getName() + " §ahas reached " + streak + " §akillstreaks!");
        }

    }

}
