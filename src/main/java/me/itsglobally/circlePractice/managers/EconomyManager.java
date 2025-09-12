package me.itsglobally.circlePractice.managers;

import me.itsglobally.circlePractice.CirclePractice;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class EconomyManager {

    private final CirclePractice plugin;
    private Economy economy;
    private boolean vaultEnabled = false;

    public EconomyManager(CirclePractice plugin) {
        this.plugin = plugin;
        setupEconomy();
    }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault not found! Economy features will be limited.");
            return;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("No economy plugin found! Using internal coin system only.");
            return;
        }

        economy = rsp.getProvider();
        vaultEnabled = true;
        plugin.getLogger().info("Vault economy integration enabled with " + economy.getName());
    }

    public boolean isVaultEnabled() {
        return vaultEnabled;
    }

    public Economy getEconomy() {
        return economy;
    }

    public long getCoins(UUID uuid) {
        return plugin.getFileDataManager().getCoins(uuid);
    }

    public void setCoins(UUID uuid, long amount) {
        plugin.getFileDataManager().setCoins(uuid, amount);
    }

    public void addCoins(UUID uuid, long amount) {
        plugin.getFileDataManager().addCoins(uuid, amount);
    }

    public boolean removeCoins(UUID uuid, long amount) {
        return plugin.getFileDataManager().removeCoins(uuid, amount);
    }

    // Vault integration methods
    public double getBalance(Player player) {
        if (vaultEnabled) {
            return economy.getBalance(player);
        }
        return getCoins(player.getUniqueId());
    }

    public boolean hasEnough(Player player, double amount) {
        if (vaultEnabled) {
            return economy.has(player, amount);
        }
        return getCoins(player.getUniqueId()) >= (long) amount;
    }

    public boolean withdraw(Player player, double amount) {
        if (vaultEnabled) {
            return economy.withdrawPlayer(player, amount).transactionSuccess();
        }
        return removeCoins(player.getUniqueId(), (long) amount);
    }

    public void deposit(Player player, double amount) {
        if (vaultEnabled) {
            economy.depositPlayer(player, amount);
        } else {
            addCoins(player.getUniqueId(), (long) amount);
        }
    }

    public String formatBalance(double balance) {
        if (vaultEnabled) {
            return economy.format(balance);
        }
        return String.format("%,.0f coins", balance);
    }

    public String getCurrencyName() {
        if (vaultEnabled) {
            return economy.currencyNamePlural();
        }
        return "coins";
    }

    // Reward methods for different activities
    public void rewardKill(Player player) {
        long reward = plugin.getConfigManager().getConfig().getLong("rewards.kill", 10);
        addCoins(player.getUniqueId(), reward);
    }

    public void rewardWin(Player player, String kit) {
        long baseReward = plugin.getConfigManager().getConfig().getLong("rewards.duel-win", 25);
        long kitMultiplier = plugin.getConfigManager().getConfig().getLong("rewards.kit-multipliers." + kit, 1);
        long totalReward = baseReward * kitMultiplier;

        addCoins(player.getUniqueId(), totalReward);
    }

    public void rewardKillstreak(Player player, long streak) {
        if (streak % 5 == 0) { // Every 5 kills
            long reward = plugin.getConfigManager().getConfig().getLong("rewards.killstreak-bonus", 50);
            addCoins(player.getUniqueId(), reward);
        }
    }
}