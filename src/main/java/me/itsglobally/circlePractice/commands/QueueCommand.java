package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.managers.KitManager;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "queue", description = "ga", override = true, shouldLoad = true)
public class QueueCommand implements NontageCommand {

    private final CirclePractice plugin = CirclePractice.getInstance();

    @Override
    public void execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return;
        }

        if (args.length != 1) {
            MessageUtil.sendMessage(player, "&cUsage: /queue <kit>");
            return;
        }

        String kit = args[0];
        plugin.getQueueManager().joinQueue(player, kit);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        if (args.length == 1) {
            return new ArrayList<>(plugin.getKitManager().getAllKits().keySet());
        }
        return List.of();
    }
}