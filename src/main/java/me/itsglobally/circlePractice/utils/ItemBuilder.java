package me.itsglobally.circlePractice.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;


    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(MessageUtil.formatMessage(name));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(MessageUtil.formatMessage(line));
        }
        meta.setLore(newLore);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemBuilder setWoolColor(DyeColor color) {
        if (item.getType() == Material.WOOL) {
            item.setDurability(color.getWoolData());
        }
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean status) {
        meta.spigot().setUnbreakable(status);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
