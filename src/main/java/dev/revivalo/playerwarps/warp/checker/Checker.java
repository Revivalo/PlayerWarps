package dev.revivalo.playerwarps.warp.checker;

import org.bukkit.entity.Player;

public interface Checker {
    default boolean check(Player player) {
        if (player.isOp()) {
            return true;
        }

        return validate(player);
    }

    boolean validate(Player player);
}
