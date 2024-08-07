package dev.revivalo.playerwarps.hook.register;

import com.earth2me.essentials.Essentials;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hook.IHook;
import org.jetbrains.annotations.Nullable;

public class EssentialsHook implements IHook<Essentials> {

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
