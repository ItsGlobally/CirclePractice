package me.itsglobally.circlePractice.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Duel {

    private final UUID id;
    private final PracticePlayer player1;
    private final PracticePlayer player2;
    private final String kit;
    private final Arena arena;
    private final long startTime;
    private final List<UUID> spectators;
    private DuelState state;
    private int countdown;

    public Duel(PracticePlayer player1, PracticePlayer player2, String kit, Arena arena) {
        this.id = UUID.randomUUID();
        this.player1 = player1;
        this.player2 = player2;
        this.kit = kit;
        this.arena = arena;
        this.startTime = System.currentTimeMillis();
        this.spectators = new ArrayList<>();
        this.state = DuelState.STARTING;
        this.countdown = 5;
    }

    public UUID getId() {
        return id;
    }

    public PracticePlayer getPlayer1() {
        return player1;
    }

    public PracticePlayer getPlayer2() {
        return player2;
    }

    public String getKit() {
        return kit;
    }

    public Arena getArena() {
        return arena;
    }

    public long getStartTime() {
        return startTime;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public DuelState getState() {
        return state;
    }

    public void setState(DuelState state) {
        this.state = state;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public PracticePlayer getOpponent(PracticePlayer player) {
        return player.equals(player1) ? player2 : player1;
    }
    public int getPlayer1OrPlayer2(PracticePlayer player) {return player.equals(player1) ? 1 : 2; }
    public boolean containsPlayer(UUID uuid) {
        return player1.getUuid().equals(uuid) || player2.getUuid().equals(uuid);
    }

    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
    }

    public void removeSpectator(UUID uuid) {
        spectators.remove(uuid);
    }

    public enum DuelState {
        STARTING, ACTIVE, FINISHED
    }
}