package dev.revivalo.playerwarps.hooks.registers;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hooks.Hook;
import org.jetbrains.annotations.Nullable;

public class OraxenHook implements Hook<Void> {

    private final boolean isHooked;
    public OraxenHook(){
        isHooked = PlayerWarpsPlugin.get().isPluginEnabled("Oraxen");
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Override
    public @Nullable Void getApi() {
        return null;
    }
}
