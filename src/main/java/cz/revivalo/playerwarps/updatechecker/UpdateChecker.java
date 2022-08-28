package cz.revivalo.playerwarps.updatechecker;

import cz.revivalo.playerwarps.PlayerWarps;
import org.bukkit.Bukkit;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {
    private final PlayerWarps playerWarps;
    private final int resourceId;

    public UpdateChecker(final PlayerWarps playerWarps, int resourceId) {
        this.playerWarps = playerWarps;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.playerWarps, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.playerWarps.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}
