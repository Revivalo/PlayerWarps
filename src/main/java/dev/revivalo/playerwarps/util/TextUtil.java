package dev.revivalo.playerwarps.util;

import com.google.common.base.Splitter;
import dev.revivalo.playerwarps.configuration.ColorReplacer;
import dev.revivalo.playerwarps.configuration.PlaceholderAwareReplacer;
import dev.revivalo.playerwarps.configuration.StringReplacer;
import dev.revivalo.playerwarps.hook.HookManager;
import dev.revivalo.playerwarps.warp.Warp;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,###,###");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<(#[A-Fa-f0-9]{6})>(.*?)</(#[A-Fa-f0-9]{6})>");
    private static final Pattern LEGACY_GRADIENT_PATTERN = Pattern.compile("<(&[A-Za-z0-9])>(.*?)</(&[A-Za-z0-9])>");
    private static final Pattern RGB_PATTERN = Pattern.compile("<(#......)>");

    private static final StringReplacer colorReplacer;
    static {
        if (HookManager.isHookEnabled(HookManager.getPlaceholderApiHook())) {
            colorReplacer = new PlaceholderAwareReplacer();
        } else {
            colorReplacer = new ColorReplacer();
        }
    }

    public static String getColorizedString(Player player, String text) {
        return colorReplacer.replace(player, text);
    }

    public static List<String> getColorizedList(Player player, List<String> list) {
        final List<String> coloredList = new ArrayList<>();
        for (String line : list) {
            coloredList.add(getColorizedString(player, line));
        }
        return coloredList;
    }

    public static String color(String text) {
        if (text == null) {
            return "Not found";
        }

        if (VersionUtil.isHexSupport()) {
            text = processGradientColors(text);
            text = processLegacyGradientColors(text);
            text = processRGBColors(text);
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String processGradientColors(String text) {
        Matcher matcher = TextUtil.GRADIENT_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            Color startColor = Color.decode(matcher.group(1));
            String between = matcher.group(2);
            Color endColor = Color.decode(matcher.group(3));
            BeforeType[] types = BeforeType.detect(between);
            between = BeforeType.replaceColors(between);
            String gradient = rgbGradient(between, startColor, endColor, types);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(gradient));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String processLegacyGradientColors(String text) {
        Matcher matcher = TextUtil.LEGACY_GRADIENT_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            char first = matcher.group(1).charAt(1);
            String between = matcher.group(2);
            char second = matcher.group(3).charAt(1);
            ChatColor firstColor = ChatColor.getByChar(first);
            ChatColor secondColor = ChatColor.getByChar(second);
            BeforeType[] types = BeforeType.detect(between);
            between = BeforeType.replaceColors(between);
            if (firstColor == null) {
                firstColor = ChatColor.WHITE;
            }
            if (secondColor == null) {
                secondColor = ChatColor.WHITE;
            }
            String gradient = rgbGradient(between, firstColor.getColor(), secondColor.getColor(), types);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(gradient));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String processRGBColors(String text) {
        Matcher matcher = TextUtil.RGB_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            ChatColor color = ChatColor.of(Color.decode(matcher.group(1)));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(color.toString()));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String rgbGradient(String str, Color from, Color to, BeforeType[] types) {
        final double[] red = linear(from.getRed(), to.getRed(), str.length());
        final double[] green = linear(from.getGreen(), to.getGreen(), str.length());
        final double[] blue = linear(from.getBlue(), to.getBlue(), str.length());
        StringBuilder before = new StringBuilder();
        for (BeforeType type : types) {
            before.append(ChatColor.getByChar(type.getCode()));
        }
        final StringBuilder builder = new StringBuilder();
        if (str.length() == 1) {
            return ChatColor.of(to) + before.toString() + str;
        }
        for (int i = 0; i < str.length(); i++) {
            builder.append(ChatColor.of(new Color((int) Math.round(red[i]), (int) Math.round(green[i]), (int) Math.round(blue[i])))).append(before).append(str.charAt(i));
        }
        return builder.toString();
    }

    private static double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }

    public enum BeforeType {
        MIXED('k'),
        BOLD('l'),
        CROSSED('m'),
        UNDERLINED('n'),
        CURSIVE('o');

        private final char code;

        BeforeType(char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }

        public static BeforeType[] detect(String text) {
            List<BeforeType> values = new ArrayList<>();
            if (text.contains("&k")) {
                values.add(MIXED);
            }
            if (text.contains("&l")) {
                values.add(BOLD);
            }
            if (text.contains("&m")) {
                values.add(CROSSED);
            }
            if (text.contains("&n")) {
                values.add(UNDERLINED);
            }
            if (text.contains("&o")) {
                values.add(CURSIVE);
            }
            return values.toArray(new BeforeType[0]);
        }

        public static String replaceColors(String text) {
            return text.replaceAll("&[kmno]", "");
        }
    }

    public static String stripColor(String input){
        return ChatColor.stripColor(input);
    }

    public static String replaceString(String messageToReplace, final Map<String, String> definitions){
        final String[] keys = definitions.keySet().toArray(new String[0]);
        final String[] values = definitions.values().toArray(new String[0]);

        return StringUtils.replaceEach(messageToReplace, keys, values);
    }

    public static String createRatingFormat(final Warp warp) {
        StringBuilder ratings = new StringBuilder();
        int reviewers = warp.getReviewers().size();
        double numberOfStars = (double) warp.getRating() / reviewers;

        for (int i = 0; i < 5; i++) {
            if (numberOfStars >= 1) {
                ratings.append("★");
            } else if (numberOfStars >= 0.6) {
                ratings.append("☆");
            } else {
                ratings.append("☆");
            }
            numberOfStars--;
        }

        return ratings.toString();
    }

    public static String formatNumber(int number) {
        return DECIMAL_FORMAT.format(number);
    }

    public static List<String> replaceListAsString(String listAsStringToReplace, final Map<String, String> definitions) {
        return Splitter.on("ᴪ").splitToList(replaceString(listAsStringToReplace, definitions));
    }
}
