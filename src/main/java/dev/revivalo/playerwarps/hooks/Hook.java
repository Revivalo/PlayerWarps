package dev.revivalo.playerwarps.hooks;

import org.jetbrains.annotations.Nullable;

public interface Hook<T> {
    boolean isOn();

    @Nullable
    T getApi();
}
