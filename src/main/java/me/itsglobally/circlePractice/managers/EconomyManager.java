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
            plugin.getLogger().warning("Vault not found! Economy features will not work.");
            return;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("No economy plugin found! Economy features will not work.");
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

    // ---------------------- 保留原有 API ----------------------

    public long getCoins(UUID uuid) {
        if (!vaultEnabled) return 0;
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return 0;
        return (long) economy.getBalance(player);
    }

    public void setCoins(UUID uuid, long amount) {
        if (!vaultEnabled) return;
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        double balance = economy.getBalance(player);
        if (balance > amount) {
            economy.withdrawPlayer(player, balance - amount);
        } else if (balance < amount) {
            economy.depositPlayer(player, amount - balance);
        }
    }

    public void addCoins(UUID uuid, long amount) {
        if (!vaultEnabled) return;
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        economy.depositPlayer(player, amount);
    }

    public boolean removeCoins(UUID uuid, long amount) {
        if (!vaultEnabled) return false;
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    // ---------------------- 新增 Player 參數 API ----------------------

    public double getBalance(Player player) {
        if (!vaultEnabled) return 0;
        return economy.getBalance(player);
    }

    public boolean hasEnough(Player player, double amount) {
        if (!vaultEnabled) return false;
        return economy.has(player, amount);
    }

    public boolean withdraw(Player player, double amount) {
        if (!vaultEnabled) return false;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public void deposit(Player player, double amount) {
        if (!vaultEnabled) return;
        economy.depositPlayer(player, amount);
    }

    public String formatBalance(double balance) {
        if (!vaultEnabled) return String.format("%,.0f coins", balance);
        return economy.format(balance);
    }

    public String getCurrencyName() {
        if (!vaultEnabled) return "coins";
        return economy.currencyNamePlural();
    }

    // ---------------------- 獎勵系統 ----------------------

    public void rewardKill(Player player) {
        long reward = plugin.getConfigManager().getConfig().getLong("rewards.kill", 10);
        deposit(player, reward);
    }

    public void rewardWin(Player player, String kit) {
        long baseReward = plugin.getConfigManager().getConfig().getLong("rewards.duel-win", 25);
        long kitMultiplier = plugin.getConfigManager().getConfig().getLong("rewards.kit-multipliers." + kit, 1);
        long totalReward = baseReward * kitMultiplier;
        deposit(player, totalReward);
    }

    public void rewardKillstreak(Player player, long streak) {
        if (streak % 5 == 0) {
            long reward = plugin.getConfigManager().getConfig().getLong("rewards.killstreak-bonus", 50);
            deposit(player, reward);
        }
    }
}
