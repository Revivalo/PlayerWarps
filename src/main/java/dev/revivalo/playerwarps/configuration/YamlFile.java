package dev.revivalo.playerwarps.configuration;

import com.tchristofferson.configupdater.ConfigUpdater;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class YamlFile {

	private final File file;
	private final UpdateMethod updateMethod;
	private final String filePath;
	private YamlConfiguration configuration;

	public YamlFile(final String filePath, final File folder, final UpdateMethod updateMethod) {
		file = new File(folder, filePath);
		this.updateMethod = updateMethod;
		this.filePath = filePath;

		boolean update = true;
		switch (this.updateMethod){
			case NEVER:
				update = false;
				break;
			case ON_LOAD:
				update = !file.exists();
				break;
		}

		try {
			configuration = YamlConfiguration.loadConfiguration(file);
		} catch (ParserException exception) {
			//Bukkit.getLogger().info(String.format("Format exception in %s file.", file.getName()));
			return;
		}

		try {
			configuration.save(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		configuration.options().copyDefaults(true);

		if (update) {
			try {
				ConfigUpdater.update(PlayerWarpsPlugin.get(), filePath, file, Collections.emptyList());
				reload();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public YamlConfiguration getConfiguration() {
		return configuration;
	}

	public void reload() {
		try {
			configuration.load(file);
		} catch (InvalidConfigurationException | IOException ex) {
			ex.printStackTrace();
		}
	}

	public File getFile() {
		return this.file;
	}

	public UpdateMethod getUpdateMethod() {
		return this.updateMethod;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public enum UpdateMethod {
		EVERYTIME, ON_LOAD, NEVER
	}
}
