package dev.revivalo.playerwarps.updatechecker;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {
    private final PlayerWarpsPlugin playerWarpsPlugin;
    private final int RESOURCE_ID;

    public UpdateChecker(final PlayerWarpsPlugin playerWarpsPlugin, int RESOURCE_ID) {
        this.playerWarpsPlugin = playerWarpsPlugin;
        this.RESOURCE_ID = RESOURCE_ID;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.playerWarpsPlugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.RESOURCE_ID).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.playerWarpsPlugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}
