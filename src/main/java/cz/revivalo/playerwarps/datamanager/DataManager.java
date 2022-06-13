package cz.revivalo.playerwarps.datamanager;

import cz.revivalo.playerwarps.PlayerWarps;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DataManager {

    private File dataFile;
    private FileConfiguration data;

    public void setup(){
        dataFile = new File(PlayerWarps.getPlugin(PlayerWarps.class).getDataFolder(), "data.yml");

        if (!dataFile.exists()){
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("[PlayerWarps] Error while creating data.yml file");
            }
        }

        data = YamlConfiguration.loadConfiguration(dataFile);
        data.options().header("Don't edit this file!");
    }

    public void saveData(){
        try {
            data.save(dataFile);
        } catch (IOException e) {
            Bukkit.getLogger().info("[PlayerWarps] Error while saving data.yml file");
        }
    }

    public FileConfiguration getData(){return data;}
}
