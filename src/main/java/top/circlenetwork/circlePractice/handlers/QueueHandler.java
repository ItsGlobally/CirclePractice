package top.circlenetwork.circlePractice.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.circlenetwork.circlePractice.data.Global;
import top.circlenetwork.circlePractice.data.PracticePlayer;
import top.circlenetwork.circlePractice.utils.Msg;

import java.util.*;

public class QueueHandler implements Global {
    private static final Map<String, Deque<UUID>> queues = new HashMap<>();

    public static void joinQueue(Player player, String kitName) {
        queues.putIfAbsent(kitName, new ArrayDeque<>());
        Deque<UUID> queue = queues.get(kitName);
        PracticePlayer pp = PracticePlayer.get(player.getUniqueId());

        if (pp.getQueuedKit() != null || pp.getCurrentGame() != null) {
            Msg.send(player, "&cYou are not in the spawn.");
            return;
        }

        queue.addLast(player.getUniqueId());
        pp.setState(PracticePlayer.SpawnState.QUEUING);
        Msg.send(player, "&cJoined " + kitName + " queue.");

        tryMatch(kitName);
    }

    private static void tryMatch(String kitName) {
        Deque<UUID> queue = queues.get(kitName);

        if (queue.size() < 2) return;

        Player p1 = Bukkit.getPlayer(queue.pollFirst());
        Player p2 = Bukkit.getPlayer(queue.pollFirst());

        if (p1 == null || p2 == null) {
            tryMatch(kitName);
            return;
        }
    }

    public static void leaveQueue(Player player) {
        for (Deque<UUID> queue : queues.values()) {
            queue.remove(player.getUniqueId());
        }
        PracticePlayer pp = PracticePlayer.get(player.getUniqueId());
        pp.setState(PracticePlayer.SpawnState.SPAWN);
    }
}
