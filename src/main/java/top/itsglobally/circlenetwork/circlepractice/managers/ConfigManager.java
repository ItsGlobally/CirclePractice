package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.utils.ConfigRegister;
import top.nontage.nontagelib.config.BaseConfig;

public class ConfigManager extends Managers{
    private static MainConfig mainConfig;

    public ConfigManager() {
        mainConfig = ConfigRegister.register(new MainConfig(), "config");
    }

    public static MainConfig getMainConfig() {
        return mainConfig;
    }

    public class MainConfig extends BaseConfig {
        // -------------------- 可配置的欄位 --------------------
        public String defaultKit = "NoDebuff";
        public boolean allowSpectators = true;
        public int duelRequestExpire = 60;
        public int maxGameTime = 900;
        public String spawnWorld = "spawn";
        public double spawnX = 0.5;
        public double spawnY = 65;
        public double spawnZ = 0.5;
        public float spawnYaw = 0f;
        public float spawnPitch = 0f;

        public Location getSpawn() {
            if (Bukkit.getWorld(spawnWorld) == null) return null;
            return new Location(Bukkit.getWorld(spawnWorld), spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);
        }

        // -------------------- API --------------------
        public String getDefaultKit() {
            return defaultKit;
        }

        public boolean isAllowSpectators() {
            return allowSpectators;
        }

        public int getDuelRequestExpire() {
            return duelRequestExpire;
        }

        public int getMaxGameTime() {
            return maxGameTime;
        }
    }

    public void teleportToSpawn(Player p) {
        p.teleport(mainConfig.getSpawn());
    }
}
