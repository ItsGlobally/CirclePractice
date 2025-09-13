package me.itsglobally.circlePractice.managers;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.Kit;
import me.itsglobally.circlePractice.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitManager {

    private final CirclePractice plugin;
    private final Map<String, Kit> kits;

    public KitManager(CirclePractice plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
        loadKits();
        createDefaultKits();
    }

    public void loadKits() {
        FileConfiguration config = plugin.getConfigManager().getKits();
        ConfigurationSection kitsSection = config.getConfigurationSection("kits");

        if (kitsSection == null) return;

        for (String name : kitsSection.getKeys(false)) {
            Kit kit = new Kit(name);
            ConfigurationSection kitSection = kitsSection.getConfigurationSection(name);

            if (kitSection.contains("contents")) {
                List<ItemStack> contentsList = (List<ItemStack>) kitSection.getList("contents");
                if (contentsList != null) kit.setContents(contentsList.toArray(new ItemStack[0]));
            }

            if (kitSection.contains("armor")) {
                List<ItemStack> armorList = (List<ItemStack>) kitSection.getList("armor");
                if (armorList != null) kit.setArmor(armorList.toArray(new ItemStack[0]));
            }

            kit.setEnabled(kitSection.getBoolean("enabled", true));
            kit.setCanBuild(kitSection.getBoolean("canBuild", true));

            kits.put(name, kit);
        }
    }


    private void createDefaultKits() {
        if (!kits.containsKey("NoDebuff")) {
            Kit noDebuff = new Kit("NoDebuff");

            ItemStack[] contents = new ItemStack[36];
            contents[0] = new ItemBuilder(Material.DIAMOND_SWORD)
                    .addEnchantment(Enchantment.DAMAGE_ALL, 3)
                    .addEnchantment(Enchantment.DURABILITY, 3)
                    .addEnchantment(Enchantment.FIRE_ASPECT, 2)
                    .setUnbreakable(true)
                    .build();

            contents[1] = new ItemStack(Material.COOKED_BEEF, 64);

            contents[2] = new ItemBuilder(Material.POTION)
                    .setDurability((short) 8194)
                    .build();


            contents[3] = new ItemBuilder(Material.POTION)
                    .setDurability((short) 8195)
                    .build();

            for (int i = 4; i < 32; i++) {
                contents[i] = new ItemBuilder(Material.POTION)
                        .setDurability((short) 16421)
                        .build();
            }
            for (int i = 33; i < 36; i++) {
                contents[i] = new ItemBuilder(Material.POTION)
                        .setDurability((short) 8194)
                        .build();
            }

            // Armor
            ItemStack[] armor = new ItemStack[4];
            armor[3] = new ItemBuilder(Material.DIAMOND_HELMET)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .setUnbreakable(true)
                    .build();
            armor[2] = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .setUnbreakable(true)
                    .build();
            armor[1] = new ItemBuilder(Material.DIAMOND_LEGGINGS)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .setUnbreakable(true)
                    .build();
            armor[0] = new ItemBuilder(Material.DIAMOND_BOOTS)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .setUnbreakable(true)
                    .build();

            noDebuff.setContents(contents);
            noDebuff.setArmor(armor);
            noDebuff.setCanBuild(false);
            kits.put("NoDebuff", noDebuff);
        }
        if (!kits.containsKey("FFA")) {
            Kit FFA = new Kit("FFA");


            ItemStack[] contents = new ItemStack[36];

            contents[0] = new ItemBuilder(Material.WOOD_SWORD)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ALL, 2)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .build();
            contents[1] = new ItemBuilder(Material.BOW)
                    .addEnchantment(Enchantment.ARROW_KNOCKBACK, 1)
                    .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                    .setUnbreakable(true)
                    .build();
            contents[2] = new ItemBuilder(Material.ARROW, 16)
                    .build();
            contents[3] = new ItemBuilder(Material.ENDER_PEARL)
                    .build();
            contents[4] = new ItemBuilder(Material.SANDSTONE, 64)
                    .build();
            contents[5] = new ItemBuilder(Material.STONE_PICKAXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 2)
                    .setUnbreakable(true)
                    .build();

            ItemStack[] armor = new ItemStack[4];
            armor[3] = new ItemBuilder(Material.LEATHER_HELMET)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .setUnbreakable(true)
                    .build();
            armor[2] = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .setUnbreakable(true)
                    .build();
            armor[1] = new ItemBuilder(Material.LEATHER_LEGGINGS)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .setUnbreakable(true)
                    .build();
            armor[0] = new ItemBuilder(Material.LEATHER_BOOTS)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .setUnbreakable(true)
                    .build();

            FFA.setContents(contents);
            FFA.setArmor(armor);
            FFA.setCanBuild(true);
            kits.put("FFA", FFA);
        }
        saveKits();
    }

    private void saveKits() {
        FileConfiguration config = plugin.getConfigManager().getKits();

        for (Kit kit : kits.values()) {
            String path = "kits." + kit.getName();
            config.set(path + ".contents", kit.getContents());
            config.set(path + ".armor", kit.getArmor());
            config.set(path + ".enabled", kit.isEnabled());
            config.set(path + ".canBuild", kit.canBuild());
        }

        plugin.getConfigManager().saveKits();
    }

    public void applyKit(Player player, String kitName) {
        Kit kit = kits.get(kitName);
        if (kit == null) return;

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (kit.getContents() != null) {
            player.getInventory().setContents(kit.getContents());
        }
        if (kit.getArmor() != null) {
            player.getInventory().setArmorContents(kit.getArmor());
        }

        player.updateInventory();
    }

    public boolean kitExists(String name) {
        return kits.containsKey(name);
    }

    public Kit getKit(String name) {
        return kits.get(name);
    }

    public Map<String, Kit> getAllKits() {
        return kits;
    }

    public void saveKit(String name, ItemStack[] contents, ItemStack[] armor) {
        Kit kit = kits.getOrDefault(name, new Kit(name));
        kit.setContents(contents);
        kit.setArmor(armor);
        kits.put(name, kit);
        saveKits();
    }
}