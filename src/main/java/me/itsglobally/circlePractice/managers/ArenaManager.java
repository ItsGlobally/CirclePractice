package me.itsglobally.circlePractice.managers;

import me.itsglobally.circlePractice.CirclePractice;
import me.itsglobally.circlePractice.data.Arena;
import me.itsglobally.circlePractice.utils.ArenaPasteWE6;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.*;

public class ArenaManager {

    private final CirclePractice plugin;
    private final Map<String, Arena> arenas;
    private final Random rand;

    private final int arenaSize = 1000;
    private final int yLevel = 50;
    private final int gridMin = 1;
    private final int gridMax = 41;

    private final List<String> schematicPaths;
    private final Map<String, List<String>> kitToSchematics;

    private final World world;

    public ArenaManager(CirclePractice plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.rand = new Random();
        this.schematicPaths = new ArrayList<>();
        this.kitToSchematics = new HashMap<>();
        this.world = Bukkit.getWorld("practice");
        loadArenas();
    }

    /**
     * Loads all schematic files and maps them to kits
     */
    public void loadArenas() {
        schematicPaths.clear();
        kitToSchematics.clear();

        File schemFolder = new File("plugins/WorldEdit/schematics");
        if (!schemFolder.exists() || !schemFolder.isDirectory()) {
            Bukkit.getLogger().warning("Schematics folder not found: " + schemFolder.getAbsolutePath());
            return;
        }

        File[] files = schemFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".schematic"));
        if (files != null) {
            for (File f : files) {
                String absPath = f.getAbsolutePath();
                schematicPaths.add(absPath);

                // Remove extension
                String baseName = f.getName().substring(0, f.getName().lastIndexOf('.'));

                // Split by "-" or "_"
                String[] parts = baseName.toLowerCase().split("[-_]");
                for (String kit : parts) {
                    kitToSchematics.computeIfAbsent(kit, k -> new ArrayList<>()).add(absPath);
                }
            }
        }

        Bukkit.getLogger().info("Loaded " + schematicPaths.size() + " schematic(s).");
    }

    public List<String> getArenaFiles(String kit) {
        return new ArrayList<>(kitToSchematics.getOrDefault(kit.toLowerCase(), Collections.emptyList()));
    }

    public List<String> getAllSchematics() {
        return new ArrayList<>(schematicPaths);
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public Arena getAvailableArena(String kit) {
        List<Arena> freeArenas = arenas.values().stream()
                .filter(arena -> !arena.isInUse() && arena.isComplete())
                .toList();

        if (!freeArenas.isEmpty()) {
            Arena chosen = freeArenas.get(rand.nextInt(freeArenas.size()));
            if (chosen.getKits().contains(kit)) {
                chosen.setInUse(true);
                return chosen;
            }
        }

        return createArena(kit);
    }

    private Arena createArena(String kit) {
        List<String> matchingSchematics = kitToSchematics.getOrDefault(kit.toLowerCase(), Collections.emptyList());
        if (matchingSchematics.isEmpty()) {
            Bukkit.getLogger().warning("No schematics found for kit: " + kit);
            return null;
        }
        String chosenPath = matchingSchematics.get(rand.nextInt(matchingSchematics.size()));


        int xIndex = rand.nextInt(gridMax - gridMin + 2) + gridMin;
        int zIndex = rand.nextInt(gridMax - gridMin + 2) + gridMin;

        int originX = xIndex * arenaSize;
        int originZ = zIndex * arenaSize;
        int y = yLevel;

        Bukkit.broadcastMessage("Generating arena at: " + xIndex + "," + zIndex);
        Bukkit.broadcastMessage("Origin: " + originX + "," + originZ + "," + y);
        Bukkit.broadcastMessage("Using schematic: " + chosenPath);

        try {
            ArenaPasteWE6.pasteSchematicAt(world, chosenPath, originX, y, originZ, -26, -26, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Location spectator = new Location(world, originX, y + 10, originZ);
        Location player1Spawn = new Location(world, originX + 23, y + 1, originZ, 90, 0);
        Location player2Spawn = new Location(world, originX - 23, y + 1, originZ, -90, 0);

        Bukkit.broadcastMessage("P1 Spawn: " + player1Spawn);
        Bukkit.broadcastMessage("P2 Spawn: " + player2Spawn);

        String arenaName = kit + "-" + xIndex + "-" + zIndex;
        Arena arena = new Arena(arenaName);
        arena.setPos1(player1Spawn);
        arena.setPos2(player2Spawn);
        arena.setSpectatorSpawn(spectator);
        arena.setInUse(false);


        String baseName = new File(chosenPath).getName().replaceFirst("[.][^.]+$", "");
        String[] parts = baseName.toLowerCase().split("[-_]");
        for (String k : parts) {
            arena.addKits(k);
        }

        arenas.put(arenaName, arena);

        return arena;
    }

    public Map<String, Arena> getAllArenas() {
        return arenas;
    }
}
