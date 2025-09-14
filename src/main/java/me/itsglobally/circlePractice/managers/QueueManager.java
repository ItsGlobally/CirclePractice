package me.itsglobally.circlePractice.managers;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class QueueManager {

    private final CirclePractice plugin;
    private final Map<String, Queue<PracticePlayer>> queues; // kit -> queue

    public QueueManager(CirclePractice plugin) {
        this.plugin = plugin;
        this.queues = new HashMap<>();

        startMatchmaking();
    }

    public void joinQueue(Player player, String kit) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);

        if (practicePlayer.getState() != PracticePlayer.PlayerState.SPAWN) {
            MessageUtil.sendMessage(player, "&cYou cannot join a queue right now!");
            return;
        }

        if (!plugin.getKitManager().kitExists(kit)) {
            MessageUtil.sendMessage(player, "&cThat kit doesn't exist!");
            return;
        }

        // Add to queue
        queues.computeIfAbsent(kit, k -> new LinkedList<>()).add(practicePlayer);
        practicePlayer.setState(PracticePlayer.PlayerState.QUEUE);
        practicePlayer.setQueuedKit(kit);
        practicePlayer.setQueueStartTime(System.currentTimeMillis());

        MessageUtil.sendMessage(player, "&aYou joined the &e" + kit + " &aqueue!");
    }

    public void leaveQueue(Player player) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);

        if (!practicePlayer.isInQueue()) {
            MessageUtil.sendMessage(player, "&cYou're not in a queue!");
            return;
        }

        String kit = practicePlayer.getQueuedKit();
        Queue<PracticePlayer> queue = queues.get(kit);
        if (queue != null) {
            queue.remove(practicePlayer);
        }

        practicePlayer.setState(PracticePlayer.PlayerState.SPAWN);
        practicePlayer.setQueuedKit(null);
        practicePlayer.setQueueStartTime(0);

        MessageUtil.sendMessage(player, "&cYou left the queue!");
    }

    private void startMatchmaking() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Queue<PracticePlayer>> entry : queues.entrySet()) {
                    String kit = entry.getKey();
                    Queue<PracticePlayer> queue = entry.getValue();

                    // Remove offline players
                    queue.removeIf(pp -> {
                        Player p = Bukkit.getPlayer(pp.getUuid());
                        return p == null || !p.isOnline();
                    });

                    // Match players
                    while (queue.size() >= 2) {
                        PracticePlayer p1 = queue.poll();
                        PracticePlayer p2 = queue.poll();

                        Player player1 = Bukkit.getPlayer(p1.getUuid());
                        Player player2 = Bukkit.getPlayer(p2.getUuid());

                        if (player1 != null && player2 != null && player1.isOnline() && player2.isOnline()) {
                            plugin.getDuelManager().startDuel(player1, player2, kit);
                        }
                    }

                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second
    }
}