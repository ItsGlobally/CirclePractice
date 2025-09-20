package me.itsglobally.circlePractice.menus;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.utils.ItemBuilder;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import top.nontage.nontagelib.utils.inventory.InventoryBuilder;

import java.util.Collections;
import java.util.List;


public class RankMenu {

    private static final CirclePractice plugin = CirclePractice.getInstance();

    public static void open(Player p) {
        PracticePlayer pp = CirclePractice.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
        if (pp.getState() != PracticePlayer.PlayerState.SPAWN) {
            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            MessageUtil.sendMessage(p, "&cYou are not in the spawn!");
            return;
        }

        InventoryBuilder inv = new InventoryBuilder(27, "Ranks");
        ItemStack filler = new ItemBuilder(Material.STAINED_GLASS_PANE)
                .setWoolColor(DyeColor.GRAY)
                .setDisplayName(" ")
                .setLore(Collections.emptyList())
                .build();

        for (int i = 0; i < 27; i++) {
            inv.setItem(filler, e -> e.getEvent().setCancelled(true), i);
        }
        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.GREEN)
                .setDisplayName("&2VIP")
                .setLore(List.of(pp.isPlayerInGroup("vip") ? "&aPurchased!" : "&cNot Purchased!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            if (!pp.isPlayerInGroup("vip")) {
                if (plugin.getEconomyManager().hasEnough(p, 6999)) {
                    if (!plugin.getEconomyManager().withdraw(p, 6999)) {
                        MessageUtil.sendMessage(p, "&cFailed to take 6999 coins away");
                        return;
                    }
                    MessageUtil.sendMessage(p, "&7You bought &2[VIP]&7!");
                    return;
                }
                MessageUtil.sendMessage(p, "&cStop dreaming! You don't have enough money to buy that!");
                return;
            }
            MessageUtil.sendMessage(p, "&cYou already have that rank!");

        }, 11);

        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.RED)
                .setDisplayName("&4PRIME")
                .setLore(List.of(pp.isPlayerInGroup("prime") ? "&aPurchased!" : "&cNot Purchased!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            if (!pp.isPlayerInGroup("prime")) {
                if (plugin.getEconomyManager().hasEnough(p, 12999)) {
                    if (!plugin.getEconomyManager().withdraw(p, 12999)) {
                        MessageUtil.sendMessage(p, "&cFailed to take 12999 coins away");
                        return;
                    }
                    MessageUtil.sendMessage(p, "&7You bought &4[PRIME]&7!");
                    return;
                }
                MessageUtil.sendMessage(p, "&cStop dreaming! You don't have enough money to buy that!");
                return;
            }
            MessageUtil.sendMessage(p, "&cYou already have that rank!");
        }, 13);

        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.PURPLE)
                .setDisplayName("&5PREMIUM")
                .setLore(List.of(pp.isPlayerInGroup("premium") ? "&aPurchased!" : "&cNot Purchased!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            if (!pp.isPlayerInGroup("premium")) {
                if (plugin.getEconomyManager().hasEnough(p, 16999)) {
                    if (!plugin.getEconomyManager().withdraw(p, 16999)) {
                        MessageUtil.sendMessage(p, "&cFailed to take 16999 coins away");
                        return;
                    }
                    MessageUtil.sendMessage(p, "&7You bought &5[PREMIUM]&7!");
                    return;
                }
                MessageUtil.sendMessage(p, "&cStop dreaming! You don't have enough money to buy that!");
                return;
            }
            MessageUtil.sendMessage(p, "&cYou already have that rank!");
        }, 15);




        p.openInventory(inv.getInventory());
    }
}
