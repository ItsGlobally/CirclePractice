package top.circlenetwork.circlePractice.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import top.circlenetwork.circlePractice.utils.FileUtil;

import java.util.*;

@Getter
@ToString
public class Kit {
    private static final Map<String, Kit> kits = new HashMap<>();
    private final String name;
    private final YamlFile sourceYml;
    private final FileConfiguration source;
    private final EnumMap<KitOption, Object> optionCache = new EnumMap<>(KitOption.class);
    private final Set<Material> allowedBreakBlocksAroundBed = EnumSet.noneOf(Material.class);

    // instance
    @Setter
    private ItemStack[] inventory;
    @Setter
    private ItemStack[] armor;
    public Kit(String name) {
        this.name = name;
        this.inventory = new ItemStack[36];
        this.armor = new ItemStack[4];
        this.sourceYml = new YamlFile("kits/" + name + ".yml");
        this.source = sourceYml.getConfig();

        for (KitOption option : KitOption.values()) {
            Object value = source.contains(option.getPath())
                    ? source.get(option.getPath())
                    : option.getDefaultValue();

            if (!option.getDefaultValue().getClass().isInstance(value)) {
                Bukkit.getLogger().warning(
                        "[Kit] Invalid type for " + name + "." + option.getPath()
                );
                value = option.getDefaultValue();
            }

            if (source.contains("allowedBreakBlocksAroundBed")) {
                try {
                    for (String block : source.getStringList("allowedBreakBlocksAroundBed")) {
                        Material material = Material.valueOf(block);
                        allowedBreakBlocksAroundBed.add(material);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            optionCache.put(option, value);
        }


        kits.put(name, this);
    }

    public static void loadAll() {
        Map<String, YamlFile> all = FileUtil.loadYamlFolder("kits");
        for (Map.Entry<String, YamlFile> entry : all.entrySet()) {
            loadFromYml(entry.getValue().getConfig(), entry.getKey());
        }
    }

    public static void saveAll() {
        for (Kit kit : kits.values()) {
            kit.save();
        }
    }

    @SuppressWarnings("unchecked")
    public static Kit loadFromYml(FileConfiguration cfg, String name) {

        ItemStack[] inventory = new ItemStack[0];
        ItemStack[] armor = new ItemStack[0];

        if (cfg.contains("inventory")) {
            List<ItemStack> invList = (List<ItemStack>) cfg.get("inventory");
            inventory = invList.toArray(new ItemStack[0]);
        }

        if (cfg.contains("armor")) {
            List<ItemStack> armorList = (List<ItemStack>) cfg.get("armor");
            armor = armorList.toArray(new ItemStack[0]);
        }

        Kit kit = new Kit(name);
        kit.setInventory(inventory);
        kit.setArmor(armor);
        return kit;
    }

    public static Kit getKit(String name) {
        return kits.get(name);
    }

    public void save() {
        source.set("inventory", inventory);
        source.set("armor", armor);
        for (Map.Entry<Kit.KitOption, Object> entry : optionCache.entrySet()) {
            source.set(entry.getKey().getPath(), entry.getValue());
        }
        List<String> saveableAllowedBreakBlocksAroundBed = new ArrayList<>();
        for (Material material : allowedBreakBlocksAroundBed) {
            saveableAllowedBreakBlocksAroundBed.add(material.name());
        }
        source.set("allowedBreakBlocksAroundBed", saveableAllowedBreakBlocksAroundBed);
        sourceYml.save();
    }

    public boolean getBoolean(KitOption option) {
        return (boolean) optionCache.get(option);
    }

    public int getInt(KitOption option) {
        return (int) optionCache.get(option);
    }

    public void setBoolean(KitOption option, boolean status) {
        optionCache.put(option, status);
    }

    public void setInt(KitOption option, int value) {
        optionCache.put(option, value);
    }

    public void reload() {
        kits.remove(this.name);
        loadFromYml(sourceYml.getConfig(), this.name);
    }

    @Getter
    @RequiredArgsConstructor
    public enum KitOption {
        ENABLED("enabled", true),
        BUILD("build", false),
        DAMAGE("damage", true),
        BED("bed", false),
        HUNGER("hunger", true),
        FREEZE("freeze_on_cooldown", false),
        BOXING("boxing", false),
        VOIDTP("void_tp", false),
        FALLDAMAGE("fall_damage", true),
        BOXINGHITS("boxing_hits", 100),
        VOIDADDBOXINGHITS("void_add_boxing_hits", 0),
        RESPAWNTIME("respawn_time", 5);

        private final String path;
        private final Object defaultValue;
    }
}
