package top.circlenetwork.circlePractice.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class TitleUtil {

    private static IChatBaseComponent toComponent(String msg) {
        return IChatBaseComponent.ChatSerializer.a(
                "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', msg) + "\"}"
        );
    }

    private static void sendPacket(Player p, PacketPlayOutTitle packet) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendTitle(Player p, String msg, int stayTicks) {
        sendPacket(p, new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TIMES,
                null,
                0,          // fadeIn
                stayTicks,  // stay
                0           // fadeOut
        ));

        sendPacket(p, new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE,
                toComponent(msg)
        ));
    }

    public static void sendSubtitle(Player p, String msg, int stayTicks) {

        sendPacket(p, new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TIMES,
                null,
                0,
                stayTicks,
                0
        ));

        sendPacket(p, new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                toComponent(msg)
        ));
    }

    public static void sendTitleAndSubtitle(Player p, String title, String subtitle, int stayTicks) {

        sendPacket(p, new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TIMES,
                null,
                0,
                stayTicks,
                0
        ));

        if (title != null) {
            sendPacket(p, new PacketPlayOutTitle(
                    PacketPlayOutTitle.EnumTitleAction.TITLE,
                    toComponent(title)
            ));
        }

        if (subtitle != null) {
            sendPacket(p, new PacketPlayOutTitle(
                    PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                    toComponent(subtitle)
            ));
        }
    }

    public static void clear(Player p) {
        sendPacket(p, new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.CLEAR,
                null
        ));
    }
}
