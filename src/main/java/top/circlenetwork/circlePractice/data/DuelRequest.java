package top.circlenetwork.circlePractice.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@RequiredArgsConstructor
public class DuelRequest implements Global{
    private final Player from;
    private final Player to;
    @Setter
    private boolean expired = false;

    public void start() {
        PracticePlayer fromPracticePlayer = PracticePlayer.get(from.getUniqueId());
        PracticePlayer toPracticePlayer = PracticePlayer.get(to.getUniqueId());
        fromPracticePlayer.getSentDuelRequests().put(to.getUniqueId(), DuelRequest.this);
        toPracticePlayer.getSentDuelRequests().put(from.getUniqueId(), DuelRequest.this);
        new BukkitRunnable() {
            @Override
            public void run() {
                fromPracticePlayer.getSentDuelRequests().put(to.getUniqueId(), null);
                toPracticePlayer.getSentDuelRequests().put(from.getUniqueId(), null);
                expired = true;
            }
        }.runTaskLater(plugin, 20L * 60);
    }
}
