package me.itsglobally.circlePractice.managers;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.Arena;
import me.itsglobally.circlePractice.data.Duel;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DuelManager {

    private final CirclePractice plugin;
    private final Map<UUID, Duel> duels;
    private final Map<UUID, UUID> duelRequests; // requester -> target
    private final Map<UUID, String> duelRequestsKit;

    // 新增：存每個玩家 Duel 可見的玩家
    private final Map<UUID, Set<UUID>> duelVisible;

    public DuelManager(CirclePractice plugin) {
        this.plugin = plugin;
        this.duelRequestsKit = new HashMap<>();
        this.duels = new HashMap<>();
        this.duelRequests = new HashMap<>();
        this.duelVisible = new HashMap<>();
    }

    public void sendDuelRequest(Player requester, Player target, String kit) {
        UUID requesterUuid = requester.getUniqueId();
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(requesterUuid);

        if (pp.getState() != PracticePlayer.PlayerState.SPAWN) {
            MessageUtil.sendActionBar(requester, "&cYou are not in the spawn!");
            requester.playSound(requester.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            duelRequests.remove(requesterUuid);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        duelRequests.put(requesterUuid, targetUuid);
        duelRequestsKit.put(requesterUuid, kit);

        MessageUtil.sendMessage(requester, "&aYou sent a duel request to &e" + target.getName() + " &afor kit &e" + kit);
        MessageUtil.sendMessage(target, "&e" + requester.getName() + " &ahas sent you a duel request for kit &e" + kit);
        MessageUtil.sendMessage(target, "&aType &e/accept &ato accept the duel!");

        // 自動過期
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (duelRequests.get(requesterUuid) != null && duelRequests.get(requesterUuid).equals(targetUuid)) {
                duelRequests.remove(requesterUuid);
                MessageUtil.sendMessage(requester, "&cYour duel request to " + target.getName() + " has expired.");
                if (target.isOnline()) {
                    MessageUtil.sendMessage(target, "&cThe duel request from " + requester.getName() + " has expired.");
                }
            }
        }, 600L);
    }

    public void acceptDuel(Player accepter) {
        UUID accepterUuid = accepter.getUniqueId();
        UUID requesterUuid = null;

        for (Map.Entry<UUID, UUID> entry : duelRequests.entrySet()) {
            if (entry.getValue().equals(accepterUuid)) {
                requesterUuid = entry.getKey();
                break;
            }
        }

        if (requesterUuid == null) {
            MessageUtil.sendMessage(accepter, "&cYou don't have any pending duel requests!");
            return;
        }

        Player requester = Bukkit.getPlayer(requesterUuid);
        if (requester == null || !requester.isOnline()) {
            MessageUtil.sendMessage(accepter, "&cThe player who sent you the duel request is no longer online!");
            duelRequests.remove(requesterUuid);
            return;
        }

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(requesterUuid);
        if (pp.getState() != PracticePlayer.PlayerState.SPAWN) {
            MessageUtil.sendMessage(accepter, "&cThe player is not in the spawn!");
            accepter.playSound(accepter.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            duelRequests.remove(requesterUuid);
            return;
        }

        duelRequests.remove(requesterUuid);
        startDuel(requester, accepter, duelRequestsKit.get(requesterUuid));
    }

    public void startDuel(Player player1, Player player2, String kit) {
        PracticePlayer pp1 = plugin.getPlayerManager().getPlayer(player1);
        PracticePlayer pp2 = plugin.getPlayerManager().getPlayer(player2);

        if (pp1.getState() != PracticePlayer.PlayerState.SPAWN || pp2.getState() != PracticePlayer.PlayerState.SPAWN) {
            MessageUtil.sendMessage(player1, "&cOne of the players is not available for a duel!");
            MessageUtil.sendMessage(player2, "&cOne of the players is not available for a duel!");
            pp1.setState(PracticePlayer.PlayerState.SPAWN);
            pp2.setState(PracticePlayer.PlayerState.SPAWN);
            return;
        }

        Arena arena = plugin.getArenaManager().getAvailableArena(kit);
        if (arena == null) {
            MessageUtil.sendMessage(player1, "&cNo arenas are available right now!");
            MessageUtil.sendMessage(player2, "&cNo arenas are available right now!");
            pp1.setState(PracticePlayer.PlayerState.SPAWN);
            pp2.setState(PracticePlayer.PlayerState.SPAWN);
            return;
        }

        // 創建 Duel
        Duel duel = new Duel(pp1, pp2, kit, arena);
        duels.put(duel.getId(), duel);

        pp1.setState(PracticePlayer.PlayerState.DUEL);
        pp2.setState(PracticePlayer.PlayerState.DUEL);
        pp1.setCurrentDuel(duel);
        pp2.setCurrentDuel(duel);
        arena.setInUse(true);

        pp1.saveInventory();
        pp2.saveInventory();

        player1.teleport(arena.getPos1());
        player2.teleport(arena.getPos2());
        player1.setAllowFlight(false);
        player1.setFlying(false);
        player2.setAllowFlight(false);
        player2.setFlying(false);

        plugin.getKitManager().applyKit(player1, kit);
        plugin.getKitManager().applyKit(player2, kit);

        // 設定 Duel 可見性
        setupDuelVisibility(duel);

        startCountdown(duel);
    }

    private void setupDuelVisibility(Duel duel) {
        Player p1 = Bukkit.getPlayer(duel.getPlayer1().getUuid());
        Player p2 = Bukkit.getPlayer(duel.getPlayer2().getUuid());
        if (p1 == null || p2 == null) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.equals(p1) && !online.equals(p2)) {
                    online.hidePlayer(p1);
                    online.hidePlayer(p2);
                    p1.hidePlayer(online);
                    p2.hidePlayer(online);
                }
            }
            p1.showPlayer(p2);
            p2.showPlayer(p1);

            duelVisible.put(p1.getUniqueId(), Set.of(p2.getUniqueId()));
            duelVisible.put(p2.getUniqueId(), Set.of(p1.getUniqueId()));
        }, 5L);
    }

    private void startCountdown(Duel duel) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (duel.getState() != Duel.DuelState.STARTING) {
                    cancel();
                    return;
                }

                Player p1 = Bukkit.getPlayer(duel.getPlayer1().getUuid());
                Player p2 = Bukkit.getPlayer(duel.getPlayer2().getUuid());
                if (p1 == null || p2 == null) {
                    cancel();
                    return;
                }

                int countdown = duel.getCountdown();
                if (countdown > 0) {
                    MessageUtil.sendMessage(p1, "&eDuel starting in &c" + countdown + "&e...");
                    MessageUtil.sendMessage(p2, "&eDuel starting in &c" + countdown + "&e...");
                    p1.playSound(p1.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                    p2.playSound(p2.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                    duel.setCountdown(countdown - 1);
                } else {
                    MessageUtil.sendMessage(p1, "&aFight!");
                    MessageUtil.sendMessage(p2, "&aFight!");
                    p1.playSound(p1.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
                    p2.playSound(p2.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
                    duel.setState(Duel.DuelState.ACTIVE);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void endDuel(Duel duel, PracticePlayer winner) {
        duel.setState(Duel.DuelState.FINISHED);

        Player p1 = Bukkit.getPlayer(duel.getPlayer1().getUuid());
        Player p2 = Bukkit.getPlayer(duel.getPlayer2().getUuid());

        duel.getPlayer1().setState(PracticePlayer.PlayerState.SPAWN);
        duel.getPlayer2().setState(PracticePlayer.PlayerState.SPAWN);
        duel.getPlayer1().setCurrentDuel(null);
        duel.getPlayer2().setCurrentDuel(null);

        duel.getArena().setInUse(false);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (p1 != null) online.showPlayer(p1);
            if (p2 != null) online.showPlayer(p2);
            if (p1 != null && plugin.getPlayerManager().getPlayer(online).isInSpawnIncludeQueuing()) p1.showPlayer(online);
            if (p2 != null && plugin.getPlayerManager().getPlayer(online).isInSpawnIncludeQueuing()) p2.showPlayer(online);
        }

        duelVisible.remove(p1 != null ? p1.getUniqueId() : null);
        duelVisible.remove(p2 != null ? p2.getUniqueId() : null);

        if (p1 != null) {
            plugin.getConfigManager().teleportToSpawn(p1);
            duel.getPlayer1().restoreInventory();
        }
        if (p2 != null) {
            plugin.getConfigManager().teleportToSpawn(p2);
            duel.getPlayer2().restoreInventory();
        }

        if (winner != null) {
            Player winnerPlayer = Bukkit.getPlayer(winner.getUuid());
            if (winnerPlayer != null) {
                plugin.getEconomyManager().rewardWin(winnerPlayer, duel.getKit());
                plugin.getFileDataManager().addXp(winnerPlayer.getUniqueId(), 10);
                MessageUtil.sendMessage(winnerPlayer, "You won &d10 xp &rfrom the duel");
            }
            plugin.getFileDataManager().updatePlayerStats(winner.getUuid(), duel.getKit(), true);
            plugin.getFileDataManager().updatePlayerStats(duel.getOpponent(winner).getUuid(), duel.getKit(), false);

            String message = "&f-------------------------\n&bWinner: &f" + winner.getName() + "&r | &cLoser: &f" + duel.getOpponent(winner).getName() + "&r\n&f-------------------------";
            if (p1 != null) MessageUtil.sendMessage(p1, message);
            if (p2 != null) MessageUtil.sendMessage(p2, message);
        }

        duels.remove(duel.getId());
    }

    public Duel getDuel(UUID playerId) {
        for (Duel duel : duels.values()) {
            if (duel.containsPlayer(playerId)) return duel;
        }
        return null;
    }

    public boolean hasPendingRequest(UUID playerId) {
        return duelRequests.containsValue(playerId);
    }

    public Map<UUID, Duel> getAllDuels() {
        return duels;
    }
}
