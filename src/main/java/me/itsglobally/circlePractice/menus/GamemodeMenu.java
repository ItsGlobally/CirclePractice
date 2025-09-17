package me.itsglobally.circlePractice.menus;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.PracticePlayer;
import me.itsglobally.circlePractice.utils.ItemBuilder;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import top.nontage.nontagelib.utils.inventory.InventoryBuilder;

public class GamemodeMenu {
    public static void open(Player p) {
        PracticePlayer pp = CirclePractice.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
        if (pp.getState() != PracticePlayer.PlayerState.SPAWN) {
            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
            MessageUtil.sendMessage(p, "&cYou are not in the spawn!");
            return;
        }
        InventoryBuilder inv = new InventoryBuilder(27, "Game modes");
        inv.setItem(new ItemBuilder(Material.IRON_AXE)
                        .setDisplayName("&aFFA")
                        .build(), clickInventoryEvent -> {
                    InventoryClickEvent e = clickInventoryEvent.getEvent();
                    e.setCancelled(true);

                    CirclePractice.getInstance().getFFAManager().spawn(p);

                    p.closeInventory();
                }, 12
        );
        inv.setItem(new ItemBuilder(Material.IRON_AXE)
                        .setDisplayName("&bDuels")
                        .build(), clickInventoryEvent -> {
                    InventoryClickEvent e = clickInventoryEvent.getEvent();
                    e.setCancelled(true);

                    DuelMenu.open(p, true);

                }, 14
        );

        p.openInventory(inv.getInventory());
    }
}
