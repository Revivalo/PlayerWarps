package dev.revivalo.playerwarps.warp.checker;

import org.bukkit.entity.Player;

public interface Checker {
    boolean validate(Player player);
}
