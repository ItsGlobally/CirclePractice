package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.FileDataManager;
import me.itsglobally.circlePractice.data.TempData;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "ffa", description = "?", override = true, shouldLoad = true)
public class FFACommand implements NontageCommand {

    private final CirclePractice plugin = CirclePractice.getInstance();


    @Override
    public void execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player p)) {
            return;
        }

        if (args.length < 1) {
            MessageUtil.sendMessage(p, "&c/ffa [join/leave/build/stats]");
            return;
        }

        switch (args[0]) {
            case "join" -> {
                plugin.getFFAManager().joinFFA(p);
            }
            case "leave" -> {
                plugin.getFFAManager().leaveFFA(p);
            }
            case "build" -> {
                TempData.toggleBuild(p.getUniqueId());
                MessageUtil.sendActionBar(p, "&7Changed your build mode to " + TempData.getBuild(p.getUniqueId()));
            }
            case "stats" -> {
                FileDataManager.FfaStats stats = plugin.getFileDataManager().getFfaStats(p.getUniqueId());
                MessageUtil.sendMessage(p, "&cKills&r:" + stats.kills() + "\n&cDeaths&r: " + stats.deaths());
            }
            default -> {
                MessageUtil.sendMessage(p, "&c/ffa [join/leave/build/stats]");
            }
        }
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        if (args.length == 1) {
            return List.of("join", "leave", "build", "stats");
        }
        return List.of();
    }
}
