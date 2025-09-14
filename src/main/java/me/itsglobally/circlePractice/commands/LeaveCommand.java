package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "leave", description = "ga", aliases = {"l"}, override = true, shouldLoad = true)
public class LeaveCommand implements NontageCommand {

    private final CirclePractice plugin = CirclePractice.getInstance();

    @Override
    public void execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return;
        }

        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);

        if (practicePlayer.isInQueue()) {
            plugin.getQueueManager().leaveQueue(player);
        } else if (practicePlayer.isInDuel()) {
            plugin.getDuelManager().endDuel(practicePlayer.getCurrentDuel(),
                    practicePlayer.getCurrentDuel().getOpponent(practicePlayer));
        } else if (practicePlayer.isInFFA()) {
            plugin.getFFAManager().leaveFFA(player);
        }
        else {
            MessageUtil.sendMessage(player, "&cYou're not in a queue or duel!");
        }

    }
}