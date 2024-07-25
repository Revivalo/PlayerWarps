package dev.revivalo.playerwarps.configuration;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class Data {
    private final YamlFile yamlFile;
    private final YamlConfiguration configuration;
    public Data() {
        this.yamlFile = new YamlFile(
                "data.yml",
                PlayerWarpsPlugin.get().getDataFolder(),
                YamlFile.UpdateMethod.NEVER
        );

        this.configuration = yamlFile.getConfiguration();
    }

    public YamlFile getYamlFile() {
        return yamlFile;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }
}
