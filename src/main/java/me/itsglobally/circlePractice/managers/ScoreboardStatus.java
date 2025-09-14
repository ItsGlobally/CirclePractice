package me.itsglobally.circlePractice.managers;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.FileDataManager;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public record ScoreboardStatus(CirclePractice plugin) {
    public String getPlayerCurrentLine(Player p, int line) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
        PracticePlayer.PlayerState ps = pp.getState();
        switch (line) {
            case 1 -> {
                if (ps == PracticePlayer.PlayerState.FFA) {
                    return "Kills: " + plugin.getFileDataManager().getFfaStats(p.getUniqueId()).kills();
                }
                if (ps == PracticePlayer.PlayerState.DUEL) {
                    return "Your opponent: " + pp.getCurrentDuel().getOpponent(pp);
                }
            }
            case 2 -> {
                if (ps == PracticePlayer.PlayerState.SPAWN) {
                    return "Online Player: &d" + Bukkit.getOnlinePlayers().toArray().length;
                }
                if (ps == PracticePlayer.PlayerState.DUEL) {
                    return "Your ping: " + NMSUtils.getPing(p);
                }
                if (ps == PracticePlayer.PlayerState.FFA) {
                    return "Deaths: " + plugin.getFileDataManager().getFfaStats(p.getUniqueId()).deaths();
                }
            }
            case 3 -> {
                if (ps == PracticePlayer.PlayerState.SPAWN) {
                    return "Coins: &d" + plugin.getFileDataManager().getCoins(p.getUniqueId());
                }
                if (ps == PracticePlayer.PlayerState.FFA) {
                    return "K/D: " + plugin.getFileDataManager().getFfaStats(p.getUniqueId()).getKDR();
                }
                if (ps == PracticePlayer.PlayerState.DUEL) {
                    return "Their ping: " + NMSUtils.getPing(pp.getCurrentDuel().getOpponent(pp).getPlayer());
                }
            }
            case 4 -> {
                if (ps == PracticePlayer.PlayerState.FFA) {
                    return "Coins: &d" + plugin.getFileDataManager().getCoins(p.getUniqueId());
                }
            }
        }
        return "";
    }
}
/*
Circle Network

1
Online Player: 0 2
Coins: -114514 3
4
itsglobally.top
 */
/*
Circle Network

Your opp: Gayson_TW_awa1
Your ping: -1ms 2
Their ping: -2ms 3
4
itsglobally.top
 */
/*
Circle Network

Kills: 114514 1
Deaths: 114514 2
K/D: 1 3
Coins: -114514 4

itsglobally.top
 */