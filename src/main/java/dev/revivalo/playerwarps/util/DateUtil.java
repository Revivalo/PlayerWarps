package dev.revivalo.playerwarps.util;

import dev.revivalo.playerwarps.configuration.file.Config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class DateUtil {
    private static final DateFormat formatter =  new SimpleDateFormat(Config.DATE_FORMAT.asString());

    public static DateFormat getFormatter() {
        return formatter;
    }
}
