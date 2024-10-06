package dev.revivalo.playerwarps.util;

import java.text.DecimalFormat;

public final class NumberUtil {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,###,###");

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String formatNumber(int number) {
        return DECIMAL_FORMAT.format(number);
    }
}
