package dev.revivalo.playerwarps.hook.register;

import dev.revivalo.playerwarps.hook.Hook;
import world.bentobox.bentobox.BentoBox;

public class BentoBoxHook implements Hook<BentoBox> {

    private boolean isHooked;
    private BentoBox bentoBox;

    @Override
    public void register() {
        isHooked = isPluginEnabled("BentoBox");
        if (isHooked)
            bentoBox = BentoBox.getInstance();
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Override
    public BentoBox getApi() {
        return bentoBox;
    }
}
