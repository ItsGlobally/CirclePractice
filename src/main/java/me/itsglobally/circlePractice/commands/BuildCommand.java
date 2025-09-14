package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.data.TempData;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "build", permission = "circlepractice.build")
public class BuildCommand implements NontageCommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            return;
        }
        TempData.toggleBuild(p.getUniqueId());
        MessageUtil.sendActionBar(p, "Set your build mode to " + TempData.getBuild(p.getUniqueId()));
    }
}
