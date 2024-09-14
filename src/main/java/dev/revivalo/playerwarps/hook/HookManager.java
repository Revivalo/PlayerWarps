package dev.revivalo.playerwarps.hook;

import dev.revivalo.playerwarps.hook.register.*;

import java.util.HashMap;
import java.util.Map;

public final class HookManager {
    private static final Map<HookName, Hook<?>> hooks = new HashMap<>();

    public static void hook() {
        hooks.put(HookName.PLACEHOLDER_API, new PlaceholderApiHook());
        hooks.put(HookName.ORAXEN, new OraxenHook());
        hooks.put(HookName.ITEMS_ADDER, new ItemsAdderHook());
        hooks.put(HookName.VAULT, new VaultHook());
        hooks.put(HookName.ESSENTIALS, new EssentialsHook());
        hooks.put(HookName.DYNMAP, new DynmapHook());
        hooks.put(HookName.BENTO_BOX, new BentoBoxHook());
        hooks.put(HookName.RESIDENCE, new ResidenceHook());

        for (Hook<?> hook : hooks.values()) {
            hook.preRegister();
        }
    }

    private HookManager() {
        throw new RuntimeException("This class cannot be instantiated");
    }

    public static <T> boolean isHookEnabled(Hook<T> hook) {
        return hook != null && hook.isOn();
    }

    public static PlaceholderApiHook getPlaceholderApiHook() {
        return (PlaceholderApiHook) hooks.get(HookName.PLACEHOLDER_API);
    }

    public static OraxenHook getOraxenHook() {
        return (OraxenHook) hooks.get(HookName.ORAXEN);
    }

    public static ItemsAdderHook getItemsAdderHook() {
        return (ItemsAdderHook) hooks.get(HookName.ITEMS_ADDER);
    }

    public static VaultHook getVaultHook() {
        return (VaultHook) hooks.get(HookName.VAULT);
    }

    public static EssentialsHook getEssentialsHook() {
        return (EssentialsHook) hooks.get(HookName.ESSENTIALS);
    }

    public static DynmapHook getDynmapHook() {
        return (DynmapHook) hooks.get(HookName.DYNMAP);
    }

    public static BentoBoxHook getBentoBoxHook() {
        return (BentoBoxHook) hooks.get(HookName.BENTO_BOX);
    }

    public static ResidenceHook getResidenceHook() {
        return (ResidenceHook) hooks.get(HookName.RESIDENCE);
    }

    private enum HookName {
        PLACEHOLDER_API,
        VAULT,
        ORAXEN,
        ITEMS_ADDER,
        ESSENTIALS,
        BENTO_BOX,
        RESIDENCE,
        DYNMAP
    }
}
