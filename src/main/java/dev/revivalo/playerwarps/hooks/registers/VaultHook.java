package dev.revivalo.playerwarps.hooks.registers;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hooks.Hook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public class VaultHook implements Hook<Economy> {
    private Economy economy;
    private final boolean isHooked;

    public VaultHook() {
        this.isHooked = load();
    }

    public boolean load() {
        if (!PlayerWarpsPlugin.get().isPluginLoaded("Vault")) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = PlayerWarpsPlugin.get().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Override
    public @Nullable Economy getApi() {
        return isHooked ? economy : null;
    }
}
