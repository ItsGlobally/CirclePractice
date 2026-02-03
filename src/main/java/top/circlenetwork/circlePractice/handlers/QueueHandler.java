package top.circlenetwork.circlePractice.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.circlenetwork.circlePractice.data.Global;
import top.circlenetwork.circlePractice.data.Kit;
import top.circlenetwork.circlePractice.data.PracticePlayer;
import top.circlenetwork.circlePractice.utils.Msg;
import top.circlenetwork.circlePractice.utils.RandomUtils;

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
        Msg.send(player, "&a已加入模式" + kitName + "的對列");

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
        boolean revert = RandomUtils.nextBoolean();
        HashMap<UUID, PracticePlayer> red = new HashMap<>();
        HashMap<UUID, PracticePlayer> blue = new HashMap<>();
        if (revert) {
            red.put(p1.getUniqueId(), PracticePlayer.get(p1.getUniqueId()));
            blue.put(p2.getUniqueId(), PracticePlayer.get(p2.getUniqueId()));
        } else {
            red.put(p2.getUniqueId(), PracticePlayer.get(p2.getUniqueId()));
            blue.put(p1.getUniqueId(), PracticePlayer.get(p1.getUniqueId()));
        }

        if (GameHandler.startGame(red, blue, Kit.getKit(kitName)) == null) {
            Msg.send(p1, "&c創建遊戲時發生錯誤");
            Msg.send(p2, "&c創建遊戲時發生錯誤");
        }
    }

    public static void leaveQueue(Player player) {
        for (Deque<UUID> queue : queues.values()) {
            queue.remove(player.getUniqueId());
        }
        PracticePlayer pp = PracticePlayer.get(player.getUniqueId());
        pp.setState(PracticePlayer.SpawnState.SPAWN);
        Msg.send(player, "&a已離開隊列");
    }
}
