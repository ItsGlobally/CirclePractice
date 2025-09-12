package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.Duel;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;


@CommandInfo(name = "spectate", aliases = {"spec"}, override = true)
public class SpectateCommand implements NontageCommand {

    CirclePractice plugin = CirclePractice.getInstance();

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            return;
        }

        if (strings.length < 1) {
            MessageUtil.sendMessage(p, "&c/spectate <player>");
            return;
        }
        Player tg = Bukkit.getPlayerExact(strings[0]);

        if (tg == null) {
            MessageUtil.sendMessage(p, "&cThe player is not online!");
            return;
        }

        PracticePlayer tgpp = plugin.getPlayerManager().getPlayer(tg.getUniqueId());

        Duel match = tgpp.getCurrentDuel();

        if (match.getState() == Duel.DuelState.FINISHED) {
            MessageUtil.sendMessage(p, "&cThe game is finished!");
            return;
        }
        match.getPlayer1().getPlayer().hidePlayer(p);
        match.getPlayer1().getPlayer().showPlayer(p);
        match.addSpectator(p.getUniqueId());
        p.teleport(match.getArena().getSpectatorSpawn());

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        return NontageCommand.super.onTabComplete(sender, label, args, location);
    }
}
