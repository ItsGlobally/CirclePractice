package top.circlenetwork.circlePractice.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.circlenetwork.circlePractice.annotation.AutoListener;
import top.circlenetwork.circlePractice.data.Arena;
import top.circlenetwork.circlePractice.data.Game;
import top.circlenetwork.circlePractice.data.Kit;
import top.circlenetwork.circlePractice.data.PracticePlayer;

@AutoListener
public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PracticePlayer pp = PracticePlayer.of(event.getPlayer());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        PracticePlayer.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void EntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());
        PracticePlayer damagerPracticePlayer = PracticePlayer.get(damager.getUniqueId());

        if (practicePlayer.getCurrentGame() == null || damagerPracticePlayer.getCurrentGame() == null || practicePlayer.getCurrentGame() != damagerPracticePlayer.getCurrentGame()) return;

        if (player.getHealth() - event.getFinalDamage() <= 0) {
            Game game = practicePlayer.getCurrentGame();

            game.getGameHandler().death(practicePlayer, damagerPracticePlayer, false);
        }
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());

        if (practicePlayer.getCurrentGame() == null) return;

        Game game = practicePlayer.getCurrentGame();

        if (player.getLocation().getY() <= game.getGameArena().arena().getInt(Arena.ArenaOption.VOID)) {
            if (game.getLasthit().get(practicePlayer.getUuid()) != null) {
                game.getGameHandler().death(practicePlayer, PracticePlayer.get(game.getLasthit().get(practicePlayer.getUuid())), true);
            } else {
                game.getGameHandler().death(practicePlayer, null, true);
            }
        }

    }


}
