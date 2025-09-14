package me.itsglobally.circlePractice.utils;

import me.itsglobally.circlePractice.CirclePractice;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfigManager {

    private final CirclePractice plugin;
    private FileConfiguration config;
    private FileConfiguration kits;

    private File configFile;
    private File kitsFile;

    private final Random random = new Random();

    public ConfigManager(CirclePractice plugin) {
        this.plugin = plugin;
    }

    public void setupConfig() {
        // Main config
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        config = plugin.getConfig();

        // Kits config
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
        // Default configuration values
        if (!config.contains("spawn")) {
            config.set("spawn.world", "practice");
            config.set("spawn.x", 0.5);
            config.set("spawn.y", 50);
            config.set("spawn.z", 0.5);
            config.set("spawn.yaw", 0);
            config.set("spawn.pitch", 0);
        }

        // Example default FFA spawn list
        if (!config.contains("ffa")) {
            List<String> defaults = new ArrayList<>();
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
        ConfigurationSection ffaSection = config.getConfigurationSection("ffa");
        if (ffaSection == null) return spawns;

        for (String key : ffaSection.getKeys(false)) {
            ConfigurationSection spawnSec = ffaSection.getConfigurationSection(key + ".spawn");
            if (spawnSec == null) continue;

            String world = spawnSec.getString("world", "world");
            double x = spawnSec.getDouble("x", 0.5);
            double y = spawnSec.getDouble("y", 50);
            double z = spawnSec.getDouble("z", 0.5);
            float yaw = (float) spawnSec.getDouble("yaw", 0);
            float pitch = (float) spawnSec.getDouble("pitch", 0);

            Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
            spawns.add(loc);
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
