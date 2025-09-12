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

    private final int arenaSize = 1000; // distance between arenas
    private final int yLevel = 50;
    private final int gridMin = 1; // min grid index
    private final int gridMax = 41; // max grid index
    private final List<File> schematicFiles;

    private final World world;
    private final Map<String, List<File>> kitToSchematics;

    public ArenaManager(CirclePractice plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.rand = new Random();
        this.schematicFiles = new ArrayList<>();
        this.kitToSchematics = new HashMap<>();
        this.world = Bukkit.getWorld("ffa");
        loadArenas();
    }

    /**
     * Loads all schematic files and maps them to kits
     */
    public void loadArenas() {
        schematicFiles.clear();
        kitToSchematics.clear();

        File schemFolder = new File("plugins/WorldEdit/schematics");
        if (!schemFolder.exists() || !schemFolder.isDirectory()) {
            Bukkit.getLogger().warning("Schematics folder not found: " + schemFolder.getAbsolutePath());
            return;
        }

        File[] files = schemFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".schematic"));
        if (files != null) {
            for (File f : files) {
                schematicFiles.add(f);

                // Remove extension
                String baseName = f.getName().substring(0, f.getName().lastIndexOf('.'));

                // Split by "-" or "_"
                String[] parts = baseName.toLowerCase().split("[-_]");
                for (String kit : parts) {
                    kitToSchematics.computeIfAbsent(kit, k -> new ArrayList<>()).add(f);
                }
            }
        }

        Bukkit.getLogger().info("Loaded " + schematicFiles.size() + " schematic(s).");
    }

    /**
     * Get schematic file paths for a kit
     */
    public List<String> getArenaFiles(String kit) {
        List<File> files = kitToSchematics.getOrDefault(kit.toLowerCase(), Collections.emptyList());
        List<String> result = new ArrayList<>();
        for (File f : files) {
            result.add(f.getAbsolutePath());
        }
        return result;
    }

    /**
     * Optional: get the raw File list
     */
    public List<File> getArenaFilesAsFiles(String kit) {
        return new ArrayList<>(kitToSchematics.getOrDefault(kit.toLowerCase(), Collections.emptyList()));
    }

    public List<File> getAllSchematics() {
        return new ArrayList<>(schematicFiles); // defensive copy
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

        createArena(kit);

        List<Arena> freeArenasAfterCreate = arenas.values().stream()
                .filter(arena -> !arena.isInUse() && arena.isComplete())
                .toList();

        if (!freeArenasAfterCreate.isEmpty()) {
            Arena chosen = freeArenasAfterCreate.get(rand.nextInt(freeArenasAfterCreate.size()));
            if (chosen.getKits().contains(kit)) {
                chosen.setInUse(true);
                return chosen;
            }

        }

        return null;
    }


    private void createArena(String kit) {
        // Pick a random schematic that matches this kit
        List<File> matchingSchematics = kitToSchematics.getOrDefault(kit.toLowerCase(), Collections.emptyList());
        if (matchingSchematics.isEmpty()) {
            Bukkit.getLogger().warning("No schematics found for kit: " + kit);
            return;
        }
        File chosenSchematic = matchingSchematics.get(rand.nextInt(matchingSchematics.size()));

        // Pick random grid position
        int xIndex = rand.nextInt(gridMax - gridMin + 1) + gridMin;
        int zIndex = rand.nextInt(gridMax - gridMin + 1) + gridMin;

        int originX = xIndex * arenaSize;
        int originZ = zIndex * arenaSize;
        int y = yLevel;

        Bukkit.broadcastMessage("Generating arena at: " + xIndex + "," + zIndex);
        Bukkit.broadcastMessage("Origin: " + originX + "," + originZ + "," + y);
        Bukkit.broadcastMessage("Using schematic: " + chosenSchematic.getName());

        try {
            // Paste chosen schematic
            ArenaPasteWE6.pasteSchematicAt(
                    world,
                    chosenSchematic.getAbsolutePath(),
                    originX,
                    y,
                    originZ,
                    -26, -26, true
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Spawns
        Location spectator = new Location(world, originX, y + 10, originZ);
        Location player1Spawn = new Location(world, originX + 25, y + 1, originZ);
        Location player2Spawn = new Location(world, originX - 25, y + 1, originZ);

        Bukkit.broadcastMessage("P1 Spawn: " + player1Spawn);
        Bukkit.broadcastMessage("P2 Spawn: " + player2Spawn);

        // Arena registration
        String arenaName = kit + "-" + xIndex + "-" + zIndex;
        Arena arena = new Arena(arenaName);
        arena.setPos1(player1Spawn);
        arena.setPos2(player2Spawn);
        arena.setSpectatorSpawn(spectator);
        arena.setInUse(true);

        // Add all kits from schematic name
        String baseName = chosenSchematic.getName().substring(0, chosenSchematic.getName().lastIndexOf('.'));
        String[] parts = baseName.toLowerCase().split("[-_]");
        for (String k : parts) {
            arena.addKits(k);
        }

        arenas.put(arenaName, arena);
    }




    public Map<String, Arena> getAllArenas() {
        return arenas;
    }
}
