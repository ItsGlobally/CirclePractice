package me.itsglobally.circlePractice.data;

import me.itsglobally.circlePractice.CirclePractice;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileDataManager {

    private final CirclePractice plugin;
    private final Map<UUID, CachedPlayerData> cache = new HashMap<>();
    private FileConfiguration playerData;
    private File playerDataFile;

    public FileDataManager(CirclePractice plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        setupPlayerDataFile();
    }

    private void setupPlayerDataFile() {
        playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml file: " + e.getMessage());
            }
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    // ----------------- Caching -----------------

    public CachedPlayerData getCachedData(UUID uuid) {
        return cache.computeIfAbsent(uuid, this::loadFromFile);
    }

    private CachedPlayerData loadFromFile(UUID uuid) {
        String path = "players." + uuid.toString();
        String name = playerData.getString(path + ".name", "Unknown");

        CachedPlayerData data = new CachedPlayerData(uuid, name);

        // Duel stats
        if (playerData.contains(path + ".stats")) {
            for (String kit : playerData.getConfigurationSection(path + ".stats").getKeys(false)) {
                int wins = playerData.getInt(path + ".stats." + kit + ".wins", 0);
                int losses = playerData.getInt(path + ".stats." + kit + ".losses", 0);
                data.getStats().put(kit, new PlayerStats(wins, losses));
            }
        }

        // Kits
        if (playerData.contains(path + ".kits")) {
            for (String kit : playerData.getConfigurationSection(path + ".kits").getKeys(false)) {
                data.getKits().put(kit, playerData.getString(path + ".kits." + kit));
            }
        }

        // FFA
        if (playerData.contains(path + ".ffa")) {
            int kills = playerData.getInt(path + ".ffa.kills", 0);
            int deaths = playerData.getInt(path + ".ffa.deaths", 0);
            data.setFfaStats(new FfaStats(kills, deaths));
        }

        // Load stars
        if (playerData.contains(path + ".stars")) {
            data.setStars(playerData.getLong(path + ".stars", 1));
        }

        if (playerData.contains(path + ".xp")) {
            data.setXp(playerData.getLong(path + ".xp", 0));
        }


        return data;
    }

    public void saveCachedData(UUID uuid) {
        CachedPlayerData data = cache.get(uuid);
        if (data == null) return;

        String path = "players." + uuid.toString();
        playerData.set(path + ".name", data.getName());
        playerData.set(path + ".first-join", data.getFirstJoin());
        playerData.set(path + ".last-seen", System.currentTimeMillis());

        // Duel stats
        for (Map.Entry<String, PlayerStats> entry : data.getStats().entrySet()) {
            String kitPath = path + ".stats." + entry.getKey();
            playerData.set(kitPath + ".wins", entry.getValue().wins());
            playerData.set(kitPath + ".losses", entry.getValue().losses());
        }

        // Kits
        for (Map.Entry<String, String> entry : data.getKits().entrySet()) {
            playerData.set(path + ".kits." + entry.getKey(), entry.getValue());
        }

        // FFA
        FfaStats ffa = data.getFfaStats();
        playerData.set(path + ".ffa.kills", ffa.kills());
        playerData.set(path + ".ffa.deaths", ffa.deaths());


        playerData.set(path + ".stars", data.getStars());

        playerData.set(path + ".xp", data.getXp());

        savePlayerDataFile();
    }

    public void saveAllCached() {
        for (UUID uuid : cache.keySet()) {
            saveCachedData(uuid);
        }
    }

    // ----------------- API -----------------

    public void savePlayerData(UUID uuid, String name) {
        CachedPlayerData data = getCachedData(uuid);
        data.setName(name);
        if (data.getFirstJoin() == 0L) {
            data.setFirstJoin(System.currentTimeMillis());
        }
        data.setLastSeen(System.currentTimeMillis());
    }

    public void updatePlayerStats(UUID uuid, String kit, boolean won) {
        CachedPlayerData data = getCachedData(uuid);
        PlayerStats stats = data.getStats().getOrDefault(kit, new PlayerStats());

        if (won) {
            stats = new PlayerStats(stats.wins() + 1, stats.losses());
        } else {
            stats = new PlayerStats(stats.wins(), stats.losses() + 1);
        }

        data.getStats().put(kit, stats);
    }

    public PlayerStats getPlayerStats(UUID uuid, String kit) {
        CachedPlayerData data = getCachedData(uuid);
        return data.getStats().getOrDefault(kit, new PlayerStats());
    }

    public void saveKitContents(UUID uuid, String kit, String serializedContents) {
        CachedPlayerData data = getCachedData(uuid);
        data.getKits().put(kit, serializedContents);
    }

    public String getKitContents(UUID uuid, String kit) {
        CachedPlayerData data = getCachedData(uuid);
        return data.getKits().get(kit);
    }

    // --- FFA API ---

    public void updateFfaStats(UUID uuid, long kills, long deaths) {
        CachedPlayerData data = getCachedData(uuid);
        FfaStats ffa = data.getFfaStats();

        long newKills = ffa.kills() + kills;
        long newDeaths = ffa.deaths() + deaths;

        data.setFfaStats(new FfaStats(newKills, newDeaths));
    }

    public FfaStats getFfaStats(UUID uuid) {
        return getCachedData(uuid).getFfaStats();
    }

    // --- Stars API ---

    public long getStars(UUID uuid) {
        return getCachedData(uuid).getStars();
    }

    public void setStars(UUID uuid, long stars) {
        getCachedData(uuid).setStars(stars);
    }

    public void addStars(UUID uuid, long stars) {
        CachedPlayerData data = getCachedData(uuid);
        data.setStars(data.getStars() + stars);
    }

    public boolean removeStars(UUID uuid, long stars) {
        CachedPlayerData data = getCachedData(uuid);
        if (data.getStars() >= stars) {
            data.setStars(data.getStars() - stars);
            return true;
        }
        return false;
    }

    public long getXp(UUID uuid) {
        return getCachedData(uuid).getXp();
    }

    public void setXp(UUID uuid, long xp) {
        CachedPlayerData data = getCachedData(uuid);
        data.setXp(xp);
        checkStarUpgrade(data);
    }

    public void addXp(UUID uuid, long xp) {
        CachedPlayerData data = getCachedData(uuid);
        data.setXp(data.getXp() + xp);
        checkStarUpgrade(data);
    }

    private void checkStarUpgrade(CachedPlayerData data) {
        while (data.getXp() >= 100) {
            data.setXp(data.getXp() - 100);
            data.setStars(data.getStars() + 1);
        }
    }

    private void savePlayerDataFile() {
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml: " + e.getMessage());
        }
    }

    public void reloadPlayerData() {
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        cache.clear();
    }

    // ----------------- Inner Classes -----------------

    public record PlayerStats(int wins, int losses) {
        public PlayerStats() {
            this(0, 0);
        }

        public double getWinRate() {
            int total = wins + losses;
            return total == 0 ? 0.0 : (double) wins / total * 100;
        }
    }

    public record FfaStats(long kills, long deaths) {
        public FfaStats() {
            this(0, 0);
        }

        public double getKDR() {
            return deaths == 0 ? kills : (double) kills / deaths;
        }
    }

    public static class CachedPlayerData {
        private final UUID uuid;
        private final Map<String, PlayerStats> stats = new HashMap<>();
        private final Map<String, String> kits = new HashMap<>();
        private String name;
        private long firstJoin;
        private long lastSeen;
        private FfaStats ffaStats = new FfaStats();

        private long stars = 1;
        private long xp = 0;

        public CachedPlayerData(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getFirstJoin() {
            return firstJoin;
        }

        public void setFirstJoin(long firstJoin) {
            this.firstJoin = firstJoin;
        }

        public long getLastSeen() {
            return lastSeen;
        }

        public void setLastSeen(long lastSeen) {
            this.lastSeen = lastSeen;
        }

        public Map<String, PlayerStats> getStats() {
            return stats;
        }

        public Map<String, String> getKits() {
            return kits;
        }

        public FfaStats getFfaStats() {
            return ffaStats;
        }

        public void setFfaStats(FfaStats ffaStats) {
            this.ffaStats = ffaStats;
        }

        public long getStars() {
            return stars;
        }

        public void setStars(long stars) {
            this.stars = stars;
        }

        public long getXp() {
            return xp;
        }

        public void setXp(long xp) {
            this.xp = xp;
        }

    }
}
