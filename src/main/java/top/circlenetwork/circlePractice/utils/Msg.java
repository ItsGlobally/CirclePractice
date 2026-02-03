package top.circlenetwork.circlePractice.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Msg {

    private Msg() {
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void send(Player player, String message) {
        player.sendMessage(color(message));
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    public static void info(String msg) {
        Bukkit.getLogger().info(msg);
    }

    public static void warn(String msg) {
        Bukkit.getLogger().warning(msg);
    }
}
