package rpg.util;

import java.util.Random;

public final class RNG {
    private static final Random R = new Random();
    private RNG() {}

    public static double random()              { return R.nextDouble(); }
    public static boolean chance(double p)     { return R.nextDouble() < p; }
    public static int range(int min, int max)  { return min + R.nextInt(max - min + 1); }
}
