package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.command.NontageCommand;

// @CommandInfo(name = "leaderboard", description = "ga", override = true, shouldLoad = true)
public class LeaderboardCommand implements NontageCommand {

    private final CirclePractice plugin = CirclePractice.getInstance();

    @Override
    public void execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return;
        }

        if (args.length != 1) {
            MessageUtil.sendMessage(player, "&cUsage: /leaderboard <kit>");
            return;
        }

        String kit = args[0];

        if (!plugin.getKitManager().kitExists(kit)) {
            MessageUtil.sendMessage(player, "&cThat kit doesn't exist!");
            return;
        }

        MessageUtil.sendMessage(player, "&e&m-----&r &6" + kit + " Leaderboard &e&m-----");
        MessageUtil.sendMessage(player, "&cLeaderboard feature coming soon!");

    }
}