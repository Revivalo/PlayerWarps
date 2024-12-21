package dev.revivalo.playerwarps.hook.register;

import dev.revivalo.playerwarps.hook.Hook;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.jetbrains.annotations.Nullable;

public class GriefPreventionHook implements Hook<GriefPrevention> {
    private GriefPrevention griefPrevention;
    private boolean isHooked;
    @Override
    public void register() {
        isHooked = isPluginEnabled("GriefPrevention");
        if (isHooked) {
            griefPrevention = GriefPrevention.instance;
        }
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Override
    public @Nullable GriefPrevention getApi() {
        return griefPrevention;
    }
}
