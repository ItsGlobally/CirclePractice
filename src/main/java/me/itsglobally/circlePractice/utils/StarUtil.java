package me.itsglobally.circlePractice.utils;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;

public final class StarUtil {

    private static final ChatColor[] RAINBOW = new ChatColor[]{
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.BLUE,
            ChatColor.LIGHT_PURPLE
    };

    private StarUtil() {}

    public static String getColoredStars(long stars) {
        String number = formatNumber(stars);

        if (stars >= 1000) {
            return rainbowize( number + "✫" );
        } else {
            // 取得對應顏色
            ChatColor color = prestigeColor(stars);
            return color + number + color + "✫" + ChatColor.RESET;
        }
    }

    public static String formatNumber(long num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    /**
     * 根據星數決定對應的 Prestige 顏色
     */
    private static ChatColor prestigeColor(long stars) {
        if (stars < 100) {
            return ChatColor.GRAY;          // Default (未滿 Iron)
        } else if (stars < 200) {
            return ChatColor.WHITE;         // Iron
        } else if (stars < 300) {
            return ChatColor.GOLD;          // Gold
        } else if (stars < 400) {
            return ChatColor.AQUA;          // Diamond
        } else if (stars < 500) {
            return ChatColor.DARK_GREEN;    // Emerald
        } else if (stars < 600) {
            return ChatColor.DARK_AQUA;     // Sapphire
        } else if (stars < 700) {
            return ChatColor.DARK_RED;      // Ruby
        } else if (stars < 800) {
            return ChatColor.LIGHT_PURPLE;  // Crystal
        } else if (stars < 900) {
            return ChatColor.BLUE;          // Opal
        } else {
            return ChatColor.DARK_PURPLE;
        }
    }

    /**
     * 彩虹著色
     */
    private static String rainbowize(String input) {
        StringBuilder sb = new StringBuilder();
        int colorIndex = 0;
        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                sb.append(c);
                continue;
            }
            ChatColor color = RAINBOW[colorIndex % RAINBOW.length];
            sb.append(color).append(c);
            colorIndex++;
        }
        sb.append(ChatColor.RESET);
        return sb.toString();
    }
}
