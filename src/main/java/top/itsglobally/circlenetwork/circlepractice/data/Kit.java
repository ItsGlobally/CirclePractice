package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class Kit {
    private final String name;
    private ItemStack[] contents;
    private ItemStack[] armor;
    private boolean hunger;
    private boolean enabled;
    private boolean forDuels;
    private boolean canBuild;
    private boolean respawnable;
    private int respawnTime;
    private Material brokeToNoSpawn;
    private Set<Material> allowBreakBlocks;

    public Kit(String name) {
        this.name = name;
        this.hunger = false;
        this.enabled = false;
        this.forDuels = true;
        this.contents = new ItemStack[36];
        this.armor = new ItemStack[4];
        this.canBuild = false;
        this.respawnable = false;
        this.respawnTime = 5;
        this.allowBreakBlocks = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public boolean isHunger() {
        return hunger;
    }

    public void setHunger(boolean hunger) {
        this.hunger = hunger;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isComplete() {
        return contents != null && armor != null;
    }

    public boolean isForDuels() {
        return forDuels;
    }

    public void setForDuels(boolean forDuels) {
        this.forDuels = forDuels;
    }

    public boolean isCanBuild() {
        return canBuild;
    }

    public void setCanBuild(boolean canBuild) {
        this.canBuild = canBuild;
    }

    public boolean isRespawnable() {
        return respawnable;
    }

    public void setRespawnable(boolean respawnable) {
        this.respawnable = respawnable;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public Material getBrokeToNoSpawn() {
        return brokeToNoSpawn;
    }

    public void setBrokeToNoSpawn(Material brokeToNoSpawn) {
        this.brokeToNoSpawn = brokeToNoSpawn;
    }

    public Set<Material> getAllowBreakBlocks() {
        return allowBreakBlocks;
    }

    public void setAllowBreakBlocks(Set<Material> sm) {
        allowBreakBlocks = sm;
    }

    public void addAllowBreakBlocks(Material m) {
        allowBreakBlocks.add(m);
    }
}
