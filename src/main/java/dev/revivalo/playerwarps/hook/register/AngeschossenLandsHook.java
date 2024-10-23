package dev.revivalo.playerwarps.hook.register;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hook.Hook;
import me.angeschossen.lands.api.LandsIntegration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class AngeschossenLandsHook implements Hook<LandsIntegration> {
    private LandsIntegration landsIntegration = null;
    @Override
    public void register() {
        if (isPluginEnabled("Lands")) {
            landsIntegration = LandsIntegration.of(PlayerWarpsPlugin.get());
        }
    }

    @Override
    public boolean isOn() {
        return landsIntegration != null;
    }

    @Override
    public @Nullable LandsIntegration getApi() {
        return landsIntegration;
    }
}
