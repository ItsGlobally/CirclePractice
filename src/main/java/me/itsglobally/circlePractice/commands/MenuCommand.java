package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.menus.GamemodeMenu;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "menu")
public class MenuCommand implements NontageCommand {

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            return;
        }
        if (strings.length < 1) {
            MessageUtil.sendMessage(p, "&cUsage: /menu <name>");
            return;
        }
        String menuName = strings[0];

        switch (menuName) {
            case "gamemode" -> GamemodeMenu.open(p);
            default -> MessageUtil.sendMessage(p, "&cMenu not found!");
        }
    }
}
