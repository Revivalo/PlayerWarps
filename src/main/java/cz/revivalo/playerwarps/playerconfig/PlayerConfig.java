package cz.revivalo.playerwarps.playerconfig;

import cz.revivalo.playerwarps.PlayerWarps;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class PlayerConfig extends YamlConfiguration {
    private static final Map<UUID, PlayerConfig> configs = new HashMap<>();

    public static PlayerConfig getConfig(Player player) {
        return getConfig(player.getUniqueId());
    }

    public static PlayerConfig getConfig(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        if (!new File(PlayerWarps.getPlugin(PlayerWarps.class).getDataFolder(), "userdata" + File.separator + uuid + ".yml").exists()) {
            return null;
        }
        return getConfig(uuid);
    }

    public static PlayerConfig getConfig(UUID uuid) {
        synchronized (configs) {
            if (configs.containsKey(uuid)) {
                return configs.get(uuid);
            }
            PlayerConfig config = new PlayerConfig(uuid);
            configs.put(uuid, config);
            return config;
        }
    }

    public static void removeConfigs() {
        Collection<PlayerConfig> oldConfs = new ArrayList<>(configs.values());
        synchronized (configs) {
            for (PlayerConfig config : oldConfs) {
                config.discard();
            }
        }
    }

    private File file = null;
    private final Object saveLock = new Object();
    private final UUID uuid;

    public PlayerConfig(UUID uuid) {
        super();
        file = new File(PlayerWarps.getPlugin(PlayerWarps.class).getDataFolder(), "userdata" + File.separator + uuid.toString() + ".yml");
        this.uuid = uuid;
        reload();
    }

    private PlayerConfig() {
        uuid = null;
    }

    private void reload() {
        synchronized (saveLock) {
            try {
                load(file);
            } catch (Exception ignore) {
            }
        }
    }

    public void save() {
        synchronized (saveLock) {
            try {
                save(file);
            } catch (Exception ignore) {
            }
        }
    }

    public void discard() {
        discard(false);
    }

    public void discard(boolean save) {
        if (save) {
            save();
        }
        synchronized (configs) {
            configs.remove(uuid);
        }
    }
}