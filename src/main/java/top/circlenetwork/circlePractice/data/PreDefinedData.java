package top.circlenetwork.circlePractice.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.circlenetwork.circlePractice.utils.Msg;

public class PreDefinedData {
    public static Location spawnLocation = new Location(Bukkit.getWorld("spawn"), 0, 51.5, 0, 0, 0);

    public static void teleportSpawn(PracticePlayer practicePlayer, String msg) {
        Player player = practicePlayer.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFireTicks(0);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(new ItemStack[36]);
        player.teleport(PreDefinedData.spawnLocation);
        practicePlayer.setState(PracticePlayer.SpawnState.SPAWN);
        practicePlayer.setCurrentGame(null);
        if (msg != null && !msg.isBlank()) Msg.send(player, msg);
    }
}