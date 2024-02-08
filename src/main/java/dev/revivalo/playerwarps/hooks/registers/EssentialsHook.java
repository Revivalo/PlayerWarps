package dev.revivalo.playerwarps.hooks.registers;

import com.earth2me.essentials.Essentials;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hooks.Hook;
import org.jetbrains.annotations.Nullable;

public class EssentialsHook implements Hook<Essentials> {

    private final boolean isHooked;
    private Essentials essentials;

    public EssentialsHook() {
        isHooked = PlayerWarpsPlugin.get().isPluginEnabled("Essentials");
        if (isHooked)
            essentials = (Essentials) PlayerWarpsPlugin.get().getServer().getPluginManager().getPlugin("Essentials");
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Override
    public @Nullable Essentials getApi() {
        return essentials;
    }
}
