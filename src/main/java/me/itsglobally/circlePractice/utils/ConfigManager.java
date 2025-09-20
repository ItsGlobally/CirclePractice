package me.itsglobally.circlePractice.utils;

import me.itsglobally.circlePractice.CirclePractice;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConfigManager {

    private final CirclePractice plugin;
    private final Random random = new Random();
    private FileConfiguration config;
    private FileConfiguration kits;
    private File configFile;
    private File kitsFile;

    public ConfigManager(CirclePractice plugin) {
        this.plugin = plugin;
    }

    public void setupConfig() {

        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        config = plugin.getConfig();

        kitsFile = new File(plugin.getDataFolder(), "kits.yml");
        if (!kitsFile.exists()) {
            try {
                kitsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        kits = YamlConfiguration.loadConfiguration(kitsFile);

        setupDefaults();
    }

    private void setupDefaults() {
        if (!config.contains("spawn")) {
            config.set("spawn.world", "practice");
            config.set("spawn.x", 0.5);
            config.set("spawn.y", 50);
            config.set("spawn.z", 0.5);
            config.set("spawn.yaw", 0);
            config.set("spawn.pitch", 0);
        }

        if (!config.contains("ffa")) {
            config.set("ffa.1.spawn.world", "practice");
            config.set("ffa.1.spawn.x", -1000.5);
            config.set("ffa.1.spawn.y", 100);
            config.set("ffa.1.spawn.z", 1000.5);
            config.set("ffa.1.spawn.yaw", 0);
            config.set("ffa.1.spawn.pitch", 0);
        }

        if (!config.contains("settings.queue-time")) {
            config.set("settings.queue-time", 60);
            config.set("settings.duel-time", 300);
            config.set("settings.spectator-enabled", true);
        }

        saveConfig();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveKits() {
        try {
            kits.save(kitsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getKits() {
        return kits;
    }


    public List<Location> getFFASpawns() {
        List<Location> spawns = new ArrayList<>();
        List<?> ffaList = config.getList("ffa");

        if (ffaList == null) return spawns;

        for (Object obj : ffaList) {
            if (!(obj instanceof Map)) continue;

            @SuppressWarnings("unchecked")
            Map<String, Object> arenaMap = (Map<String, Object>) obj;

            for (Object arenaData : arenaMap.values()) {
                if (!(arenaData instanceof Map)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> arenaValues = (Map<String, Object>) arenaData;

                Object spawnObj = arenaValues.get("spawn");
                if (!(spawnObj instanceof Map)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> spawnValues = (Map<String, Object>) spawnObj;

                String world = (String) spawnValues.getOrDefault("world", "world");
                double x = ((Number) spawnValues.getOrDefault("x", -1000.5)).doubleValue();
                double y = ((Number) spawnValues.getOrDefault("y", 50)).doubleValue();
                double z = ((Number) spawnValues.getOrDefault("z", 1000.5)).doubleValue();
                float yaw = ((Number) spawnValues.getOrDefault("yaw", 0)).floatValue();
                float pitch = ((Number) spawnValues.getOrDefault("pitch", 0)).floatValue();

                Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                spawns.add(loc);
            }
        }

        return spawns;
    }


    public void teleportToSpawn(Player player) {
        String world = config.getString("spawn.world", "world");
        double x = config.getDouble("spawn.x", 0.5);
        double y = config.getDouble("spawn.y", 50);
        double z = config.getDouble("spawn.z", 0.5);
        float yaw = (float) config.getDouble("spawn.yaw", 0);
        float pitch = (float) config.getDouble("spawn.pitch", 0);

        Location spawn = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        player.teleport(spawn);
    }
}
