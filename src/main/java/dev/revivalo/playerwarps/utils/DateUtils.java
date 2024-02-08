package dev.revivalo.playerwarps.utils;

import dev.revivalo.playerwarps.configuration.enums.Config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class DateUtils {
    private static final DateFormat formatter =  new SimpleDateFormat(Config.DATE_FORMAT.asString());

    public static DateFormat getFormatter() {
        return formatter;
    }
}
