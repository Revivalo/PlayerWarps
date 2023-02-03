package cz.revivalo.playerwarps;

import com.tchristofferson.configupdater.ConfigUpdater;
import cz.revivalo.playerwarps.categories.CategoryManager;
import cz.revivalo.playerwarps.configuration.enums.Config;
import cz.revivalo.playerwarps.datamanager.DataManager;
import cz.revivalo.playerwarps.guimanager.GUIManager;
import cz.revivalo.playerwarps.listeners.ChatSendListener;
import cz.revivalo.playerwarps.updatechecker.Notification;
import cz.revivalo.playerwarps.updatechecker.UpdateChecker;
import cz.revivalo.playerwarps.warp.PWarpCommand;
import cz.revivalo.playerwarps.warp.Warp;
import cz.revivalo.playerwarps.warp.WarpHandler;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Logger;

public final class PlayerWarps extends JavaPlugin {
    /*
    NEXT UPDATE:
     */

    @Getter @Setter
    private static boolean hexSupported;

    public static boolean newestVersion;


    private GUIManager guiManager;
    private WarpHandler warpHandler;

    private static Economy econ = null;
    @Getter @Setter
    private static PlayerWarps plugin;

    @Override
    public void onEnable() {
        setPlugin(this);
        final String bukkitVersion = Bukkit.getBukkitVersion();
        setHexSupported(bukkitVersion.contains("6")
                || bukkitVersion.contains("7")
                || bukkitVersion.contains("8")
                || bukkitVersion.contains("9")
        );

        new Metrics(this, 12061);

        final Logger logger = this.getLogger();

        new UpdateChecker(this, 79089).getVersion(version -> {
            if (Config.UPDATE_CHECKER.asBoolean()) {
                String actualVersion = this.getDescription().getVersion();
                if (actualVersion.equalsIgnoreCase(version)) {
                    logger.info("You are running latest release (" + version + ")");
                    newestVersion = true;
                } else {
                    logger.info("There is a new v" + version + " update available (You are running v" + actualVersion + ")." + "\n" + "Outdated versions are no longer supported, get new one here https://www.spigotmc.org/resources/%E2%9A%A1-playerwarps-easy-warping-system-now-with-favorite-warps-categories-1-13-1-17.79089/");
                    newestVersion = false;
                }
            }
        });

        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(this, "config.yml", configFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        CategoryManager.loadCategories();

        reloadConfig();
        ConfigurationSerialization.registerClass(Warp.class);
        DataManager dataManager = new DataManager();
        dataManager.setup();
        dataManager.saveData();
        setupEconomy();

        warpHandler = new WarpHandler(this, dataManager, econ);
        warpHandler.loadWarps();
        guiManager = new GUIManager(warpHandler);

        if (Config.AUTO_SAVE_ENABLED.asBoolean()) {
            long intervalInTicks = (Config.AUTO_SAVE_INTERVAL.asLong() * 60) * 20;
            new BukkitRunnable() {
                @Override
                public void run() {
                    warpHandler.saveWarps();
                }
            }.runTaskTimerAsynchronously(this, intervalInTicks, intervalInTicks);
        }

        registerCommands();
        implementEvents();
    }

    @Override
    public void onDisable() {warpHandler.saveWarps();}

    private void registerCommands(){
        Objects.requireNonNull(getCommand("pwarp")).setExecutor(new PWarpCommand(warpHandler, guiManager));
    }

    private void implementEvents(){
        final PluginManager pm = getServer().getPluginManager();
        //pm.registerEvents(new InventoryClickListener(warpHandler, guiManager), this);
        pm.registerEvents(new ChatSendListener(warpHandler), this);
        pm.registerEvents(new Notification(), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }
}
