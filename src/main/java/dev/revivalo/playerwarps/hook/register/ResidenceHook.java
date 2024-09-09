package dev.revivalo.playerwarps.hook.register;

import com.bekvon.bukkit.residence.Residence;
import org.jetbrains.annotations.Nullable;

public class ResidenceHook implements dev.revivalo.playerwarps.hook.IHook<Residence> {
    private Residence residence;
    private boolean isHooked;
    @Override
    public void register() {
        isHooked = isPluginEnabled("Residence");
        if (isHooked)
            residence = (Residence) getPlugin("Residence");
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Nullable
    @Override
    public Residence getApi() {
        return residence;
    }
}
