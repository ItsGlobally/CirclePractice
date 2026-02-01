package top.circlenetwork.circlePractice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
public abstract class CommandBase implements TabCompleter {
    protected void playerExecute(Player player, String label, String[] args) {}
    protected void allExecute(CommandSender sender, String label, String[] args) {}

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
