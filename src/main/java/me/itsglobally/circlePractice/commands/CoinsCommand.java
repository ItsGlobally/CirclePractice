package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "coins", description = "Manage player coins", override = true, shouldLoad = true)
public class CoinsCommand implements NontageCommand {

    private final CirclePractice plugin = CirclePractice.getInstance();

    @Override
    public void execute(CommandSender sender, String s, String[] args) {
        if (args.length == 0) {
            // Show own balance
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Console cannot have coins!");
                return;
            }

            double coins = plugin.getEconomyManager().getCoins(player.getUniqueId());
            MessageUtil.sendMessage(player, "&eYour balance: &a" +
                    plugin.getEconomyManager().formatBalance(coins));
            return;
        }

        String subCommand = args[0];

        if (subCommand.equalsIgnoreCase("balance") || subCommand.equalsIgnoreCase("bal")) {
            Player target = (sender instanceof Player) ? (Player) sender : null;

            if (args.length == 2) {
                target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    MessageUtil.sendMessage((Player) sender, "&cPlayer not found!");
                    return;
                }
            }

            if (target == null) {
                sender.sendMessage("Please specify a player!");
                return;
            }

            double coins = plugin.getEconomyManager().getCoins(target.getUniqueId());
            String message = target.equals(sender) ?
                    "&eYour balance: &a" + plugin.getEconomyManager().formatBalance(coins) :
                    "&e" + target.getName() + "'s balance: &a" + plugin.getEconomyManager().formatBalance(coins);

            MessageUtil.sendMessage((Player) sender, message);

        } else if (subCommand.equalsIgnoreCase("give")) {
            if (!sender.hasPermission("circlepractice.admin")) {
                MessageUtil.sendMessage((Player) sender, "&cYou don't have permission!");
                return;
            }

            if (args.length != 3) {
                MessageUtil.sendMessage((Player) sender, "&cUsage: /coins give <player> <amount>");
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtil.sendMessage((Player) sender, "&cPlayer not found!");
                return;
            }

            try {
                long amount = Long.parseLong(args[2]);
                plugin.getEconomyManager().addCoins(target.getUniqueId(), amount);

                MessageUtil.sendMessage((Player) sender, "&aGave &e" + amount + " coins &ato " + target.getName());
                MessageUtil.sendMessage(target, "&aYou received &e" + amount + " coins&a!");
            } catch (NumberFormatException e) {
                MessageUtil.sendMessage((Player) sender, "&cInvalid amount!");
            }

        } else if (subCommand.equalsIgnoreCase("take")) {
            if (!sender.hasPermission("circlepractice.admin")) {
                MessageUtil.sendMessage((Player) sender, "&cYou don't have permission!");
                return;
            }

            if (args.length != 3) {
                MessageUtil.sendMessage((Player) sender, "&cUsage: /coins take <player> <amount>");
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtil.sendMessage((Player) sender, "&cPlayer not found!");
                return;
            }

            try {
                long amount = Long.parseLong(args[2]);
                if (plugin.getEconomyManager().removeCoins(target.getUniqueId(), amount)) {
                    MessageUtil.sendMessage((Player) sender, "&aTook &e" + amount + " coins &afrom " + target.getName());
                    MessageUtil.sendMessage(target, "&cYou lost &e" + amount + " coins&c!");
                } else {
                    MessageUtil.sendMessage((Player) sender, "&c" + target.getName() + " doesn't have enough coins!");
                }
            } catch (NumberFormatException e) {
                MessageUtil.sendMessage((Player) sender, "&cInvalid amount!");
            }

        } else if (subCommand.equalsIgnoreCase("set")) {
            if (!sender.hasPermission("circlepractice.admin")) {
                MessageUtil.sendMessage((Player) sender, "&cYou don't have permission!");
                return;
            }

            if (args.length != 3) {
                MessageUtil.sendMessage((Player) sender, "&cUsage: /coins set <player> <amount>");
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtil.sendMessage((Player) sender, "&cPlayer not found!");
                return;
            }

            try {
                long amount = Long.parseLong(args[2]);
                plugin.getEconomyManager().setCoins(target.getUniqueId(), amount);

                MessageUtil.sendMessage((Player) sender, "&aSet " + target.getName() + "'s balance to &e" + amount + " coins");
                MessageUtil.sendMessage(target, "&aYour balance has been set to &e" + amount + " coins&a!");
            } catch (NumberFormatException e) {
                MessageUtil.sendMessage((Player) sender, "&cInvalid amount!");
            }

        } else {
            MessageUtil.sendMessage((Player) sender, "&cUsage: /coins [balance|give|take|set] [player] [amount]");
        }
    }
}