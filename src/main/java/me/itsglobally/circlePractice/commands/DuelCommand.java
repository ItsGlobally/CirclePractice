package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.menus.DuelMenu;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "duel", description = "ga", override = true, shouldLoad = true)
public class DuelCommand implements NontageCommand {

    private final CirclePractice plugin = CirclePractice.getInstance();

    @Override
    public void execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return;
        }
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            MessageUtil.sendMessage(player, "&cPlayer not found!");
            return;
        }
        if (target.equals(player)) {
            MessageUtil.sendMessage(player, "&cYou cannot duel yourself!");
            return;
        }
        if (args.length == 1) {

            DuelMenu.open(player, false, target);
            return;
        }
        String kit = args[1];


        if (!plugin.getKitManager().kitExists(kit)) {
            MessageUtil.sendMessage(player, "&cThat kit doesn't exist!");
            return;
        }

        plugin.getDuelManager().sendDuelRequest(player, target, kit);
    }
}