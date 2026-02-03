package top.circlenetwork.circlePractice.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import top.circlenetwork.circlePractice.handlers.GameHandler;
import top.circlenetwork.circlePractice.utils.Msg;

import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final HashMap<UUID, List<Location>> blocks = new HashMap<>();
    @Setter
    private boolean started = false;
    @Setter
    private boolean ended = false;



    public void broadcast(String msg) {
        for (PracticePlayer player : red.values())
            Msg.send(player.getPlayer(), msg);
        for (PracticePlayer player : blue.values())
            Msg.send(player.getPlayer(), msg);
    }

}
