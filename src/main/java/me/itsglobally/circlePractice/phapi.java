package me.itsglobally.circlePractice;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.itsglobally.circlePractice.utils.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class phapi extends PlaceholderExpansion {

    @Override
    public @NotNull String getAuthor() {
        return "ItsGlobally";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "CircleFFA";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        return switch (params) {
            case "sb1" -> MessageUtil.formatMessage("&fYour ping: 0ms");
            case "sb2" -> MessageUtil.formatMessage("&fOnline players: 1");
            case "sb3" -> MessageUtil.formatMessage("&7itsglobally.top");
            default -> "";
        };
    }
}
