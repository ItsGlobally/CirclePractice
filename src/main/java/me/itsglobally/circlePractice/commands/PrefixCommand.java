package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.menus.PrefixMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;


@CommandInfo(name = "prefix")
public class PrefixCommand implements NontageCommand {

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage("This command can only be used by players!");
            return;
        }
        PrefixMenu.open(p);
    }
}
