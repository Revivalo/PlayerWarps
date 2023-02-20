package cz.revivalo.playerwarps.utils;

import cz.revivalo.playerwarps.PlayerWarps;
import cz.revivalo.playerwarps.warp.Warp;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class TextUtils {
    private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f\\d]){6}>");

    public static String applyColor(String message) {
        if (PlayerWarps.isHexSupported()) {
            Matcher matcher = hexPattern.matcher(message);
            while (matcher.find()) {
                message = String.format("%s%s%s", message.substring(0, matcher.start()),
                        ChatColor.valueOf(matcher.group().substring(1, matcher.group().length() - 1)),
                        message.substring(matcher.end()));
                matcher = hexPattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> applyColor(List<String> list){
        return list.stream().map(TextUtils::applyColor).collect(Collectors.toList());
    }

    public static String stripColor(String input){
        return ChatColor.stripColor(input);
    }

    public static String replaceString(String messageToReplace, final Map<String, String> definitions){
        final String[] keys = definitions.keySet().toArray(new String[0]);
        final String[] values = definitions.values().toArray(new String[0]);

        return StringUtils.replaceEach(messageToReplace, keys, values);
    }

    public static void sendListToPlayer(final Player player, final List<String> list) {
        list.forEach(player::sendMessage);
    }

    public static String createRatingFormat(final Warp warp) {
        StringBuilder ratings = new StringBuilder();
        Collection<UUID> reviewers = warp.getReviewers ();
        double numberOfStars = (double) warp.getRating () / reviewers.size ();
        for (int i = 0; i < 5; i++) {
            ratings.append (numberOfStars - 1 >= 0 ? "★" : "☆");
            numberOfStars--;
        }
        return ratings.toString ();
    }
}
