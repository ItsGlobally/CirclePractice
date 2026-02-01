package top.circlenetwork.circlePractice.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import top.circlenetwork.circlePractice.handlers.GameHandler;
import top.circlenetwork.circlePractice.utils.Msg;

import java.util.HashMap;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Game {
    private final Kit kit;
    private final GameArena gameArena;
    private final GameHandler gameHandler = new GameHandler(this);
    private final HashMap<UUID, PracticePlayer> red, blue;
    private final HashMap<UUID, PracticePlayer> allRed;
    private final HashMap<UUID, PracticePlayer> allBlue;
    private final HashMap<UUID, UUID> lasthit = new HashMap<>();
    private final HashMap<UUID, Boolean> respawnable = new HashMap<>();


    public void broadcast(String msg) {
        for (PracticePlayer player : red.values())
            Msg.send(player.getPlayer(), msg);
        for (PracticePlayer player : blue.values())
            Msg.send(player.getPlayer(), msg);
    }

}
