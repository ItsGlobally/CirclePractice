package top.circlenetwork.circlePractice.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import top.circlenetwork.circlePractice.utils.FileUtil;

import java.util.*;

@Getter
@ToString
public class Arena {
    @Getter
    private static final Map<String, Arena> arenas = new HashMap<>();
    private final String name;
    private final YamlFile sourceYml;
    private final FileConfiguration source;
    private final EnumMap<ArenaOption, Object> optionCache = new EnumMap<>(ArenaOption.class);
    @Setter
    @Getter
    private Location redSpawn, blueSpawn, redBed, blueBed;
    @Getter
    private final List<String> allowedKits = new ArrayList<>();

    // instance

    public Arena(String name) {
        this.name = name;
        this.sourceYml = new YamlFile("arenas/" + name + ".yml");
        this.source = sourceYml.getConfig();
        for (ArenaOption option : ArenaOption.values()) {
            Object value = source.contains(option.getPath())
                    ? source.get(option.getPath())
                    : option.getDefaultValue();

            if (!option.getDefaultValue().getClass().isInstance(value)) {
                Bukkit.getLogger().warning(
                        "[Arena] Invalid type for " + name + "." + option.getPath()
                );
                value = option.getDefaultValue();
            }

            optionCache.put(option, value);
        }

        if (source.contains("allowedKits")) {
            allowedKits.addAll(source.getStringList("allowedKits"));
        }

        arenas.put(name, this);
    }

    public static void loadAll() {
        Map<String, YamlFile> all = FileUtil.loadYamlFolder("arenas");
        for (Map.Entry<String, YamlFile> entry : all.entrySet()) {
            loadFromYml(entry.getValue().getConfig(), entry.getKey());
        }
    }

    public static void saveAll() {
        for (Arena arena : arenas.values()) {
            arena.save();
        }
    }

    public static Arena loadFromYml(FileConfiguration cfg, String name) {
        Location redSpawn = deserializeLocation(cfg, "redSpawn");
        Location blueSpawn = deserializeLocation(cfg, "blueSpawn");
        Location redBed = deserializeLocation(cfg, "redBed");
        Location blueBed = deserializeLocation(cfg, "blueBed");

        Arena arena = new Arena(name);
        arena.setRedSpawn(redSpawn);
        arena.setBlueSpawn(blueSpawn);
        arena.setRedBed(redBed);
        arena.setBlueBed(blueBed);
        return arena;
    }

    private static Location deserializeLocation(FileConfiguration cfg, String path) {
        if (!cfg.contains(path)) return null;
        Map<String, Object> map = cfg.getConfigurationSection(path).getValues(true);
        return Location.deserialize(map);
    }

    public static Arena getArena(String name) {
        return arenas.get(name);
    }

    public void save() {
        source.set("redSpawn", redSpawn.serialize());
        source.set("blueSpawn", blueSpawn.serialize());
        if (redBed != null) source.set("redBed", redBed.serialize());
        if (blueBed != null) source.set("blueBed", blueBed.serialize());
        for (Map.Entry<ArenaOption, Object> entry : optionCache.entrySet()) {
            source.set(entry.getKey().getPath(), entry.getValue());
        }
        source.set("allowedKits", allowedKits);
        sourceYml.save();
    }

    public boolean getBoolean(ArenaOption option) {
        return (boolean) optionCache.get(option);
    }

    public int getInt(ArenaOption option) {
        return (int) optionCache.get(option);
    }

    public void setBoolean(ArenaOption option, boolean status) {
        optionCache.put(option, status);
    }

    public void setInt(ArenaOption option, int value) {
        optionCache.put(option, value);
    }

    public void reload() {
        arenas.remove(this.name);
        loadFromYml(sourceYml.getConfig(), this.name);
    }


    @Getter
    @RequiredArgsConstructor
    public enum ArenaOption {

        ENABLED("enabled", true),
        BUILD("build_limit", 0),
        VOID("void", 0);

        private final String path;
        private final Object defaultValue;
    }

}
