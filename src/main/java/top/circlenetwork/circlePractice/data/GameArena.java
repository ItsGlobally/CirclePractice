package top.circlenetwork.circlePractice.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import top.circlenetwork.circlePractice.utils.RandomUtils;
import top.circlenetwork.circlePractice.utils.WorldUtil;

import java.util.UUID;

public record GameArena(Location redSpawn, Location blueSpawn, Location redBed, Location blueBed, Arena arena,
                        Kit kit) {
    public static GameArena createGameArena(Kit kit) {
        Arena chosen = RandomUtils.randomElement(Arena.getArenas().values());

        if (chosen == null) return null;
        if (chosen.getRedSpawn() == null || chosen.getBlueSpawn() == null) {
            return null;
        }
        try {
            String name;
            name = chosen.getName() + (RandomUtils.nextBoolean() ? "_" : "-") + UUID.randomUUID().toString().substring(0, RandomUtils.nextInt(4, 12));
            if (Bukkit.getWorld(name) == null)  name = chosen.getName() + (RandomUtils.nextBoolean() ? "_" : "-") + UUID.randomUUID().toString().substring(0, RandomUtils.nextInt(4, 12));

            WorldUtil.cloneArena(chosen, name);

            World world = Bukkit.getWorld(name);
            if (chosen.getRedBed() == null || chosen.getBlueBed() == null) {
                return new GameArena(replaceWorld(chosen.getRedSpawn(), world), replaceWorld(chosen.getBlueSpawn(), world), null, null, chosen, kit);
            }
            return new GameArena(replaceWorld(chosen.getRedSpawn(), world), replaceWorld(chosen.getBlueSpawn(), world), replaceWorld(chosen.getRedBed(), world), replaceWorld(chosen.getBlueBed(), world), chosen, kit);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Location replaceWorld(Location original, World newWorld) {
        if (original == null || newWorld == null) return null;

        return new Location(
                newWorld,
                original.getX(),
                original.getY(),
                original.getZ(),
                original.getYaw(),
                original.getPitch()
        );
    }
}
