package top.circlenetwork.circlePractice.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import top.circlenetwork.circlePractice.CirclePractice;

public final class NoteBlockUtil {

    private NoteBlockUtil() {
    }

    public static float noteToPitch(int note) {
        if (note < 0) note = 0;
        if (note > 24) note = 24;
        return (float) Math.pow(2.0, (note - 12) / 12.0);
    }

    public static void play(Player player, Sound sound, int note) {
        player.playSound(
                player.getLocation(),
                sound,
                1.0f,
                noteToPitch(note)
        );
    }

    public static void wood(Player player, int note) {
        play(player, Sound.NOTE_BASS, note);
    }

    public static void stone(Player player, int note) {
        play(player, Sound.NOTE_BASS_DRUM, note);
    }

    public static void sand(Player player, int note) {
        play(player, Sound.NOTE_SNARE_DRUM, note);
    }

    public static void glass(Player player, int note) {
        play(player, Sound.NOTE_STICKS, note);
    }

    public static void gold(Player player, int note) {
        play(player, Sound.NOTE_PLING, note);
    }
}