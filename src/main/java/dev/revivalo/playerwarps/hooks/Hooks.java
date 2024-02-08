package dev.revivalo.playerwarps.hooks;

import dev.revivalo.playerwarps.hooks.registers.*;

public class Hooks {
    private static PlaceholderApiHook PLACEHOLDER_API_HOOK;
    private static OraxenHook ORAXEN_HOOK;
    private static ItemsAdderHook ITEMS_ADDER_HOOK;
    private static VaultHook VAULT_HOOK;
    private static EssentialsHook ESSENTIALS_HOOK;

    public static void hook(){
        PLACEHOLDER_API_HOOK = new PlaceholderApiHook();
        ORAXEN_HOOK = new OraxenHook();
        ITEMS_ADDER_HOOK = new ItemsAdderHook();
        VAULT_HOOK = new VaultHook();
        ESSENTIALS_HOOK = new EssentialsHook();
    }

    private Hooks() {
        throw new RuntimeException("This class cannot be instantiated");
    }

    public static <T> boolean isHookEnabled(Hook<T> hook) {
        return hook != null && hook.isOn();
    }

    public static PlaceholderApiHook getPlaceholderApiHook() {
        return Hooks.PLACEHOLDER_API_HOOK;
    }

    public static OraxenHook getOraxenHook() {
        return Hooks.ORAXEN_HOOK;
    }

    public static ItemsAdderHook getItemsAdderHook() {
        return Hooks.ITEMS_ADDER_HOOK;
    }

    public static VaultHook getVaultHook() {
        return VAULT_HOOK;
    }

    public static EssentialsHook getEssentialsHook() {
        return ESSENTIALS_HOOK;
    }
}
