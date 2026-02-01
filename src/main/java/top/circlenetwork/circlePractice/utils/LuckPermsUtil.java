package top.circlenetwork.circlePractice.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

public class LuckPermsUtil {
    private static final LuckPerms luckPerms = LuckPermsProvider.get();

    public static String getPrefix(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return "";
        String prefix = user.getCachedData().getMetaData().getPrefix();
        return (prefix != null ? prefix : "");
    }
}
