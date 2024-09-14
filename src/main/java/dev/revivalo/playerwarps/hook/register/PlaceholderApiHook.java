package dev.revivalo.playerwarps.hook.register;

import dev.revivalo.playerwarps.hook.Hook;
import dev.revivalo.playerwarps.hook.papiresolver.PAPIRegister;
import org.jetbrains.annotations.Nullable;

public class PlaceholderApiHook implements Hook<PlaceholderApiHook> {
    private boolean isHooked = false;

    @Override
    public void register() {
        if (isPluginEnabled("PlaceholderAPI")) {
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