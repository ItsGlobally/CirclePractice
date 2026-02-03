package top.circlenetwork.circlePractice.utils;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import top.circlenetwork.circlePractice.data.Arena;
import top.circlenetwork.circlePractice.data.Global;

public class WorldUtil implements Global {
    private static final SlimePlugin slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    private final static SlimeLoader fileLoader = slimePlugin.getLoader("file");

    private static SlimePropertyMap getGameArenaProps() {
        SlimePropertyMap props = new SlimePropertyMap();
        props.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        props.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        props.setString(SlimeProperties.DIFFICULTY, "hard");
        return props;
    }

    public static void cloneArena(Arena arena, String newWorldName) {
        if (arena == null || newWorldName == null || newWorldName.isEmpty()) {
            Bukkit.getLogger().warning("Cannot clone arena: invalid parameters");
            return;
        }

        try {
            if (Bukkit.getWorld(arena.getName()) != null) {
                fileLoader.unlockWorld(arena.getName());
            }

            SlimeWorld sourceWorld = slimePlugin.loadWorld(fileLoader, arena.getName(), false, getGameArenaProps());
            SlimeWorld cloned = sourceWorld.clone(newWorldName);
            slimePlugin.generateWorld(cloned);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
