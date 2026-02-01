package top.circlenetwork.circlePractice.utils;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class TeamColorUtil {

    public static final Color RED_TEAM_COLOR = Color.fromRGB(255, 85, 85);
    public static final Color BLUE_TEAM_COLOR = Color.fromRGB(85, 85, 255);

    public static ItemStack[] colorTeamItems(ItemStack[] items, boolean isRedTeam) {
        if (items == null) return null;

        ItemStack[] coloredItems = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                coloredItems[i] = colorItem(items[i].clone(), isRedTeam);
            }
        }
        return coloredItems;
    }

    public static ItemStack colorItem(ItemStack item, boolean isRedTeam) {
        if (item == null) return null;

        Material type = item.getType();

        if (isLeatherArmor(type)) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            if (meta != null) {
                meta.setColor(isRedTeam ? RED_TEAM_COLOR : BLUE_TEAM_COLOR);
                item.setItemMeta(meta);
            }
        } else {
            byte durability = isRedTeam ? DyeColor.RED.getWoolData() : DyeColor.BLUE.getWoolData();
            if (isWool(type)) {
                item.setType(Material.WOOL);
                item.setDurability(durability);
            } else if (isStainedClay(type)) {
                item.setType(Material.STAINED_CLAY);
                item.setDurability(isRedTeam ? DyeColor.RED.getData() : DyeColor.BLUE.getData());
            } else if (isStainedGlass(type)) {
                item.setType(Material.STAINED_GLASS);
                item.setDurability(isRedTeam ? DyeColor.RED.getData() : DyeColor.BLUE.getData());
            } else if (isStainedGlassPane(type)) {
                item.setType(Material.STAINED_GLASS_PANE);
                item.setDurability(isRedTeam ? DyeColor.RED.getData() : DyeColor.BLUE.getData());
            } else if (isCarpet(type)) {
                item.setType(Material.CARPET);
                item.setDurability(durability);
            } else if (isBanner(type)) {
                item.setType(Material.BANNER);
                item.setDurability(isRedTeam ? (short) 1 : (short) 11);
            }
        }

        return item;
    }

    private static boolean isLeatherArmor(Material material) {
        return material == Material.LEATHER_HELMET ||
                material == Material.LEATHER_CHESTPLATE ||
                material == Material.LEATHER_LEGGINGS ||
                material == Material.LEATHER_BOOTS;
    }

    private static boolean isWool(Material material) {
        return material == Material.WOOL;
    }

    private static boolean isStainedClay(Material material) {
        return material == Material.STAINED_CLAY ||
                material == Material.HARD_CLAY;
    }

    private static boolean isStainedGlass(Material material) {
        return material == Material.STAINED_GLASS;
    }

    private static boolean isStainedGlassPane(Material material) {
        return material == Material.STAINED_GLASS_PANE;
    }

    private static boolean isCarpet(Material material) {
        return material == Material.CARPET;
    }

    private static boolean isBanner(Material material) {
        return material == Material.BANNER ||
                material == Material.WALL_BANNER ||
                material == Material.STANDING_BANNER;
    }
}