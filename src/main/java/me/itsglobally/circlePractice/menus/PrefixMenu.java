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


public class PrefixMenu {

    private static final CirclePractice plugin = CirclePractice.getInstance();

    public static void open(Player p) {
        PracticePlayer pp = CirclePractice.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
        if (pp.getState() != PracticePlayer.PlayerState.SPAWN) {
            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            MessageUtil.sendMessage(p, "&cYou are not in the spawn!");
            return;
        }
        InventoryBuilder inv = new InventoryBuilder(27, "Set your prefix");
        ItemStack filler = new ItemBuilder(Material.STAINED_GLASS_PANE)
                .setWoolColor(DyeColor.GRAY)
                .setDisplayName(" ")
                .setLore(Collections.emptyList())
                .build();

        for (int i = 0; i < 27; i++) {
            inv.setItem(filler, e -> e.getEvent().setCancelled(true), i);
        }
        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.GRAY)
                .setDisplayName("&7DEFAULT")
                .setLore(List.of("&aPurchased!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            plugin.getPlayerManager().setPrefixAsRank(p, "default");
            MessageUtil.sendActionBar(p, "&cSet your prefix as " + plugin.getPlayerManager().getPrefix(p));

        }, 9);
        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.GREEN)
                .setDisplayName("&2VIP")
                .setLore(List.of(pp.isPlayerInGroup("vip") ? "&aPurchased!" : "&cNot Purchased!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            plugin.getPlayerManager().setPrefixAsRank(p, "vip");
            MessageUtil.sendActionBar(p, "&cSet your prefix as " + plugin.getPlayerManager().getPrefix(p));

        }, 11);

        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.RED)
                .setDisplayName("&4PRIME")
                .setLore(List.of(pp.isPlayerInGroup("prime") ? "&aPurchased!" : "&cNot Purchased!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            plugin.getPlayerManager().setPrefixAsRank(p, "prime");
            MessageUtil.sendActionBar(p, "&cSet your prefix as " + plugin.getPlayerManager().getPrefix(p));
        }, 13);

        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.PURPLE)
                .setDisplayName("&5PREMIUM")
                .setLore(List.of(pp.isPlayerInGroup("premium") ? "&aPurchased!" : "&cNot Purchased!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            plugin.getPlayerManager().setPrefixAsRank(p, "premium");
            MessageUtil.sendActionBar(p, "&cSet your prefix as " + plugin.getPlayerManager().getPrefix(p));

        }, 15);
        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.YELLOW)
                .setDisplayName("&eYOUTUBER")
                .setLore(List.of(pp.isPlayerInGroup("youtuber") ? "&aOwned!" : "&cNot owner!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            plugin.getPlayerManager().setPrefixAsRank(p, "youtuber");
            MessageUtil.sendActionBar(p, "&cSet your prefix as " + plugin.getPlayerManager().getPrefix(p));

        }, 17);
        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.PURPLE)
                .setDisplayName("&aBUILDER")
                .setLore(List.of(pp.isPlayerInGroup("builder") ? "&aOwned!" : "&cNot owner!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            plugin.getPlayerManager().setPrefixAsRank(p, "builder");
            MessageUtil.sendActionBar(p, "&cSet your prefix as " + plugin.getPlayerManager().getPrefix(p));

        }, 19);
        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.PURPLE)
                .setDisplayName("&1MOD")
                .setLore(List.of(pp.isPlayerInGroup("mod") ? "&aOwned!" : "&cNot owner!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            plugin.getPlayerManager().setPrefixAsRank(p, "mod");
            MessageUtil.sendActionBar(p, "&cSet your prefix as " + plugin.getPlayerManager().getPrefix(p));

        }, 21);
        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.PURPLE)
                .setDisplayName("&bADMIN")
                .setLore(List.of(pp.isPlayerInGroup("admin") ? "&aOwned!" : "&cNot owner!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            plugin.getPlayerManager().setPrefixAsRank(p, "admin");
            MessageUtil.sendActionBar(p, "&cSet your prefix as " + plugin.getPlayerManager().getPrefix(p));

        }, 23);
        inv.setItem(new ItemBuilder(Material.WOOL)
                .setWoolColor(DyeColor.PURPLE)
                .setDisplayName("&dDEV")
                .setLore(List.of(pp.isPlayerInGroup("dev") ? "&aOwned!" : "&cNot owner!"))
                .build(), clickInventoryEvent -> {
            InventoryClickEvent e = clickInventoryEvent.getEvent();
            e.setCancelled(true);
            plugin.getPlayerManager().setPrefixAsRank(p, "dev");
            MessageUtil.sendActionBar(p, "&cSet your prefix as " + plugin.getPlayerManager().getPrefix(p));

        }, 25);


        p.openInventory(inv.getInventory());
    }
}
