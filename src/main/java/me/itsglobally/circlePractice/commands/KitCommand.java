package me.itsglobally.circlePractice.commands;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.Kit;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "kit", description = "ga", override = true, shouldLoad = true)
public class KitCommand implements NontageCommand {

    private final CirclePractice plugin = CirclePractice.getInstance();

    @Override
    public void execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return;
        }

        if (args.length == 0) {
            MessageUtil.sendMessage(player, "&cUsage: /kit <editor|load> [kit]");
            return;
        }

        String subCommand = args[0];

        if (subCommand.equalsIgnoreCase("editor")) {
            if (args.length != 2) {
                MessageUtil.sendMessage(player, "&cUsage: /kit editor <kit>");
                return;
            }

            String kit = args[1];
            if (!plugin.getKitManager().kitExists(kit)) {
                MessageUtil.sendMessage(player, "&cThat kit doesn't exist!");
                return;
            }

            PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
            practicePlayer.setState(PracticePlayer.PlayerState.EDITING);

            ItemStack[] saved = practicePlayer.getKitContents(kit);

            if (saved == null) {
                String serialized = plugin.getFileDataManager().getKitContents(player.getUniqueId(), kit);
                if (serialized != null) {
                    ItemStack[][] deserialized = me.itsglobally.circlePractice.utils.InventorySerializer.deserializeInventory(serialized);
                    if (deserialized != null) {
                        player.getInventory().setContents(deserialized[0]);
                        player.getInventory().setArmorContents(deserialized[1]);
                    } else {
                        plugin.getKitManager().applyKit(player, kit);
                    }
                } else {
                    plugin.getKitManager().applyKit(player, kit);
                }
            } else {
                player.getInventory().setContents(java.util.Arrays.copyOf(saved, 36));
                player.getInventory().setArmorContents(java.util.Arrays.copyOfRange(saved, 36, 40));
            }

            practicePlayer.setQueuedKit(kit);

            MessageUtil.sendMessage(player, "&aYou are now editing the &e" + kit + " &akit!");
            MessageUtil.sendMessage(player, "&eClose your inventory to save changes!");

        } else if (subCommand.equalsIgnoreCase("load")) {
            if (args.length != 2) {
                MessageUtil.sendMessage(player, "&cUsage: /kit load <kit>");
                return;
            }

            String kit = args[1];
            PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
            ItemStack[] saved = practicePlayer.getKitContents(kit);

            // Try loading from file storage if not in memory
            if (saved != null) {
                player.getInventory().setContents(java.util.Arrays.copyOf(saved, 36));
                player.getInventory().setArmorContents(java.util.Arrays.copyOfRange(saved, 36, 40));
                MessageUtil.sendMessage(player, "&aLoaded your saved &e" + kit + " &akit!");
            } else {
                String serialized = plugin.getFileDataManager().getKitContents(player.getUniqueId(), kit);
                if (serialized != null) {
                    ItemStack[][] deserialized = me.itsglobally.circlePractice.utils.InventorySerializer.deserializeInventory(serialized);
                    if (deserialized != null) {
                        player.getInventory().setContents(deserialized[0]);
                        player.getInventory().setArmorContents(deserialized[1]);
                        MessageUtil.sendMessage(player, "&aLoaded your saved &e" + kit + " &akit!");
                    } else {
                        MessageUtil.sendMessage(player, "&cYou don't have a saved kit for &e" + kit + "&c!");
                    }
                } else {
                    MessageUtil.sendMessage(player, "&cYou don't have a saved kit for &e" + kit + "&c!");
                }
            }
        }
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        if (args.length == 1) {
            return List.of("editor", "load");
        }
        if (args.length == 2) {
            List<String> kitNames = new ArrayList<>();
            for (Kit kit : plugin.getKitManager().getAllKits().values()) {
                if (kit.isEnabled()) kitNames.add(kit.getName());
            }
            return kitNames;
        }
        return List.of();
    }
}