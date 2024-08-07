package dev.revivalo.playerwarps.util;

import io.github.g00fy2.versioncompare.Version;
import org.bukkit.Bukkit;

public final class VersionUtil {
    private static boolean legacyVersion;
    private static boolean hexSupport;
    public static boolean latestVersion;

    static {
        final String serverVersionFull = Bukkit.getBukkitVersion();
        String serverVersion = serverVersionFull.split("-", 2)[0];

        Version version = new Version(serverVersion);

        setHexSupport(version.isAtLeast("1.16"));
        setLegacyVersion(version.isLowerThan("1.13"));

    }

    private static void setHexSupport(boolean hexSupport){
        VersionUtil.hexSupport = hexSupport;
    }

    public static boolean isHexSupport() {
        return hexSupport;
    }

    public static void setLegacyVersion(boolean legacyVersion) {
        VersionUtil.legacyVersion = legacyVersion;
    }

    public static boolean isLegacyVersion() {
        return legacyVersion;
    }

    public static boolean isLatestVersion() {
        return latestVersion;
    }

    public static void setLatestVersion(boolean latestVersion) {
        VersionUtil.latestVersion = latestVersion;
    }
}
