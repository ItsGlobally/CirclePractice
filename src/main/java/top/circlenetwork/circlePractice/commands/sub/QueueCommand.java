package top.circlenetwork.circlePractice.commands.sub;

import org.bukkit.entity.Player;
import top.circlenetwork.circlePractice.annotation.CommandInfo;
import top.circlenetwork.circlePractice.commands.CommandBase;
import top.circlenetwork.circlePractice.data.Kit;
import top.circlenetwork.circlePractice.data.PracticePlayer;
import top.circlenetwork.circlePractice.handlers.QueueHandler;
import top.circlenetwork.circlePractice.utils.Msg;

@CommandInfo(name = "queue", aliases = {"q"})
public class QueueCommand extends CommandBase {
    @Override
    public void playerExecute(Player player, String label, String[] args) {
        PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());
        if (practicePlayer.getCurrentGame() != null) {
            Msg.send(player, "&c你不在出生點");
            return;
        }

        if (args.length < 1) {
            Msg.send(player, "&c用法: /queue <join/leave>");
            return;
        }
        switch (args[0]) {
            case "join" -> {
                if (practicePlayer.getState() == PracticePlayer.SpawnState.QUEUING) {
                    Msg.send(player, "&c你已經排隊了");
                }
                if (args.length < 2) {
                    Msg.send(player, "&c用法: /queue join <模式>");
                    return;
                }
                String kit = args[1];
                if (Kit.getKit(kit) == null) {
                    Msg.send(player, "&c這個模式不存在!");
                    return;
                }
                if (practicePlayer.getState() != PracticePlayer.SpawnState.SPAWN || practicePlayer.getQueuedKit() != null) {
                    Msg.send(player, "&c你不在出生點");
                    return;
                }
                QueueHandler.joinQueue(player, kit);
            }
            case "leave" -> {
                if (practicePlayer.getState() != PracticePlayer.SpawnState.SPAWN || practicePlayer.getQueuedKit() != null) {
                    Msg.send(player, "&c你不在出生點");
                    return;
                }
                if (practicePlayer.getState() != PracticePlayer.SpawnState.QUEUING) {
                    Msg.send(player, "&c你沒有在排隊");
                    return;
                }
                QueueHandler.leaveQueue(player);
            }
        }


    }
}
