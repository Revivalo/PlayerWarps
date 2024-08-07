package dev.revivalo.playerwarps.hook;

import org.jetbrains.annotations.Nullable;

public interface IHook<T> {
    boolean isOn();

    @Nullable
    T getApi();
}
