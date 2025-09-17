package me.itsglobally.circlePractice.data;

import me.itsglobally.circlePractice.utils.InventorySerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PracticePlayer {

    private final UUID uuid;
    private final String name;
    private final Map<String, String> kitContents;
    private PlayerState state;
    private Duel currentDuel;
    private String queuedKit;
    private long queueStartTime;
    private ItemStack[] previousInventory;
    private ItemStack[] previousArmor;
    private Player player;

    public PracticePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.state = PlayerState.SPAWN;
        this.kitContents = new HashMap<>();
        this.player = Bukkit.getPlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public Duel getCurrentDuel() {
        return currentDuel;
    }

    public void setCurrentDuel(Duel duel) {
        this.currentDuel = duel;
    }

    public String getQueuedKit() {
        return queuedKit;
    }

    public void setQueuedKit(String kit) {
        this.queuedKit = kit;
    }

    public long getQueueStartTime() {
        return queueStartTime;
    }

    public void setQueueStartTime(long time) {
        this.queueStartTime = time;
    }

    public boolean isInQueue() {
        return state == PlayerState.QUEUE;
    }

    public boolean isInDuel() {
        return state == PlayerState.DUEL;
    }

    public boolean isInFFA() {
        return state == PlayerState.FFA;
    }

    public boolean isSpectating() {
        return state == PlayerState.SPECTATING;
    }

    public void saveInventory() {
        previousInventory = player.getInventory().getContents().clone();
        previousArmor = player.getInventory().getArmorContents().clone();
    }

    public void restoreInventory() {
        if (previousInventory != null) {
            player.getInventory().setContents(previousInventory);
        }
        if (previousArmor != null) {
            player.getInventory().setArmorContents(previousArmor);
        }
    }

    public void saveKitContents(String kit, ItemStack[] contents, ItemStack[] armor) {
        String serialized = InventorySerializer.serializeInventory(contents, armor);
        if (serialized != null) {
            kitContents.put(kit, serialized);
        }
    }

    public ItemStack[] getKitContents(String kit) {
        String serialized = kitContents.get(kit);
        if (serialized != null) {
            ItemStack[][] deserialized = InventorySerializer.deserializeInventory(serialized);
            if (deserialized != null) {

                ItemStack[] combined = new ItemStack[deserialized[0].length + deserialized[1].length];
                System.arraycopy(deserialized[0], 0, combined, 0, deserialized[0].length);
                System.arraycopy(deserialized[1], 0, combined, deserialized[0].length, deserialized[1].length);
                return combined;
            }
        }
        return null;
    }

    public Player getPlayer() {
        return player;
    }

    public enum PlayerState {
        SPAWN, QUEUE, DUEL, SPECTATING, EDITING, FFA
    }

    public boolean isPlayerInGroup(String group) {
        return player.hasPermission("group." + group);
    }

}