package dev.revivalo.playerwarps.hooks.registers;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hooks.Hook;
import dev.revivalo.playerwarps.hooks.papiresolvers.PAPIRegister;
import org.jetbrains.annotations.Nullable;

public class PlaceholderApiHook implements Hook<PlaceholderApiHook> {
    private boolean isHooked = false;

    public PlaceholderApiHook() {
        tryToHook();
    }

    private void tryToHook() {
        if (PlayerWarpsPlugin.get().isPluginLoaded("PlaceholderAPI")) {
            new PAPIRegister().register();
            isHooked = true;
        }
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Nullable
    @Override
    public PlaceholderApiHook getApi() {
        return isHooked ? this : null;
    }
}