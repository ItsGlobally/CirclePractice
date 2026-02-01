package top.circlenetwork.circlePractice.commands.sub;

import org.bukkit.entity.Player;
import top.circlenetwork.circlePractice.annotation.CommandInfo;
import top.circlenetwork.circlePractice.commands.CommandBase;
import top.circlenetwork.circlePractice.data.Kit;
import top.circlenetwork.circlePractice.data.PracticePlayer;
import top.circlenetwork.circlePractice.utils.Msg;

@CommandInfo(name = "layout")
public class LayoutCommand extends CommandBase {
    @Override
    public void playerExecute(Player player, String label, String[] args) {
        if (args.length < 2) {
            Msg.send(player, "&c用法: /layout <副指令> <值>");
        }
        String cmd = args[0];
        String value = args[1];

        switch (cmd) {
            case "edit" -> {
                Kit kit = Kit.getKit(value);
                if (kit == null) {
                    Msg.send(player, "&c該模式不存在!");
                    return;
                }
                PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());
                if (practicePlayer.getCurrentGame() != null || practicePlayer.getState() != PracticePlayer.SpawnState.SPAWN) {
                    Msg.send(player, "&c你不在出生點");
                    return;
                }
                practicePlayer.setQueuedKit(kit.getName());
                practicePlayer.setState(PracticePlayer.SpawnState.EDITING);

                player.getInventory().setContents(kit.getInventory());
                player.getInventory().setArmorContents(kit.getArmor());

                Msg.send(player, "&c你現在正在編輯模式" + kit.getName() + "的排版, 使用/layout save來儲存");
            }

            case "save" -> {
                PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());

                if (practicePlayer.getCurrentGame() != null) {
                    Msg.send(player, "&c你正在遊戲中");
                    return;
                }

                if (practicePlayer.getState() != PracticePlayer.SpawnState.EDITING) {
                    Msg.send(player, "&c你沒有在編輯排版");
                    return;
                }

                Kit kit = Kit.getKit(practicePlayer.getQueuedKit());
                if (kit == null) {
                    Msg.send(player, "&c該模式不存在!");
                    practicePlayer.setQueuedKit(null);
                    practicePlayer.setState(PracticePlayer.SpawnState.SPAWN);
                    return;
                }

                practicePlayer.getPlayerData().getKitInventories().put(kit.getName(), player.getInventory().getContents());
                practicePlayer.getPlayerData().getKitArmors().put(kit.getName(), player.getInventory().getArmorContents());
                practicePlayer.setQueuedKit(null);

                practicePlayer.getPlayerData().save();
                practicePlayer.setState(PracticePlayer.SpawnState.SPAWN);

                Msg.send(player, "&a已儲存模式" + kit.getName() + "的排版");
            }
        }
    }
}
