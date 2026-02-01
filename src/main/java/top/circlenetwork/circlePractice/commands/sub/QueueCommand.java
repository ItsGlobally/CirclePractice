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
            Msg.send(player, "&c用法: /queue <模式>");
            return;
        }

        String kit = args[0];
        if (Kit.getKit(kit) == null) {
            Msg.send(player, "&c這個模式不存在!");
            return;
        }

        QueueHandler.joinQueue(player, kit);


    }
}
