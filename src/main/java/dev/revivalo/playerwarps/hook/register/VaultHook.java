package dev.revivalo.playerwarps.hook.register;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hook.IHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public class VaultHook implements IHook<Economy> {
    private Economy economy;
    private boolean isHooked;

    @Override
    public void register() {
        if (!isPluginEnabled("Vault")) {
            isHooked = false;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = PlayerWarpsPlugin.get().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            isHooked = false;
            return;
        }

        economy = rsp.getProvider();
        isHooked = true;
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
