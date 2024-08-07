package dev.revivalo.playerwarps.hook.register;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hook.IHook;
import org.jetbrains.annotations.Nullable;

public class OraxenHook implements IHook<Void> {

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
