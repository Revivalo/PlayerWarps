package dev.revivalo.playerwarps.hook.register;

import dev.revivalo.playerwarps.hook.IHook;
import org.jetbrains.annotations.Nullable;

public class OraxenHook implements IHook<Void> {

    private boolean isHooked;

    @Override
    public void register() {
        isHooked = isPluginEnabled("Oraxen");
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
