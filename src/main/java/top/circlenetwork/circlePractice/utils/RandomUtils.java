package top.circlenetwork.circlePractice.utils;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public final class RandomUtils {

    private static final Random RANDOM = new SecureRandom();

    private RandomUtils() {
    }


    /**
     * [0, bound)
     */
    public static int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * [min, max]
     */
    public static int nextInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
        return min + RANDOM.nextInt(max - min + 1);
    }

    /**
     * 0.0 <= value < 1.0
     */
    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    /**
     * true / false
     */
    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }


    /**
     * 從 List 中隨機取一個
     */
    public static <T> T randomElement(List<T> list) {
        if (list == null || list.isEmpty()) return null;
        return list.get(nextInt(list.size()));
    }

    /**
     * 從 Collection 中隨機取一個
     */
    public static <T> T randomElement(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) return null;

        int index = nextInt(collection.size());
        int i = 0;
        for (T element : collection) {
            if (i++ == index) {
                return element;
            }
        }
        return null;
    }

    /**
     * 機率判定（0.0 ~ 1.0）
     * chance = 0.25 → 25%
     */
    public static boolean chance(double chance) {
        if (chance <= 0) return false;
        if (chance >= 1) return true;
        return RANDOM.nextDouble() < chance;
    }

    /**
     * 以百分比判定
     * percent = 25 → 25%
     */
    public static boolean percent(int percent) {
        return percent > 0 && RANDOM.nextInt(100) < percent;
    }

    /**
     * 隨機延遲（tick）
     */
    public static int randomTicks(int minTicks, int maxTicks) {
        return nextInt(minTicks, maxTicks);
    }
}