package top.circlenetwork.circlePractice.commands.sub;

import org.bukkit.entity.Player;
import top.circlenetwork.circlePractice.annotation.CommandInfo;
import top.circlenetwork.circlePractice.commands.CommandBase;
import top.circlenetwork.circlePractice.data.PracticePlayer;


@CommandInfo(name = "duel")
public class DuelCommand extends CommandBase {
    @Override
    public void playerExecute(Player player, String label, String[] args) {
        PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());
    }
}
