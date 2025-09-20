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

public class DuelMenu {
    public static void open(Player p, boolean queue) {
        open(p, queue, null);
    }

    public static void open(Player p, boolean queue, Player target) {
        PracticePlayer pp = CirclePractice.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
        if (pp.getState() != PracticePlayer.PlayerState.SPAWN) {
            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            MessageUtil.sendMessage(p, "&cYou are not in the spawn!");
            return;
        }
        InventoryBuilder inv = new InventoryBuilder(27, queue ? "Queue Game Modes" : "Duel");
        ItemStack filler = new ItemBuilder(Material.STAINED_GLASS_PANE)
                .setWoolColor(DyeColor.GRAY)
                .setDisplayName(" ")
                .setLore(Collections.emptyList())
                .build();

        for (int i = 0; i < 27; i++) {
            inv.setItem(filler, e -> e.getEvent().setCancelled(true), i);
        }
        inv.setItem(new ItemBuilder(Material.IRON_AXE)
                        .setDisplayName("&cNoDebuff")
                        .build(), clickInventoryEvent -> {
                    InventoryClickEvent e = clickInventoryEvent.getEvent();
                    e.setCancelled(true);
                    if (queue) {
                        p.performCommand("queue NoDebuff");
                    } else {
                        p.performCommand("duel " + target + "NoDebuff");
                    }
                    p.closeInventory();

                }, 1
        );
        p.openInventory(inv.getInventory());
    }
}
