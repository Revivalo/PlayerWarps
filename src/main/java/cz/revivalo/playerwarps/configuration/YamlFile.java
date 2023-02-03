package cz.revivalo.playerwarps.configuration;

import com.tchristofferson.configupdater.ConfigUpdater;
import cz.revivalo.playerwarps.PlayerWarps;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.File;
import java.util.Collections;

public class YamlFile {

	@Getter
	private final File yamlFile;

	@SneakyThrows
	public YamlFile(final String filePath, final File folder) {
		yamlFile = new File(folder, filePath);
		YamlConfiguration configuration;

		try {
			configuration = this.getConfiguration();

		} catch (ParserException exception) {
			Bukkit.getLogger().info(String.format("Format exception in %s file.", yamlFile.getName()));
			return;
		}

		configuration.save(yamlFile);
		configuration.options().copyDefaults(true);
		ConfigUpdater.update(PlayerWarps.getPlugin(), filePath, yamlFile, Collections.emptyList());
	}

	public YamlConfiguration getConfiguration() {
		return YamlConfiguration.loadConfiguration(yamlFile);
	}
}
