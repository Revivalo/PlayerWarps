package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.TextUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class ReviewWarpAction implements WarpAction<Integer> {
    @Override
    public boolean execute(Player player, Warp warp, Integer stars) {
        final UUID id = player.getUniqueId();
        if (stars <= 5 && stars >= 1) {
            if (Objects.equals(id, warp.getOwner())) {
                player.sendMessage(Lang.SELF_REVIEW.asColoredString());
                return false;
            }
            if (warp.getReviewers().contains(id)) {
                player.sendMessage(Lang.ALREADY_REVIEWED.asColoredString());
                return false;
            }
            warp.getReviewers().add(id);
            warp.setRating(warp.getRating() + stars);
            warp.setStars(TextUtils.createRatingFormat(warp));
            player.sendMessage(Lang.WARP_REVIEWED.asColoredString().
                    replace("%warp%", warp.getName()).
                    replace("%stars%", String.valueOf(stars)));
        }

        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.REVIEW_WARP;
    }

    @Override
    public int getFee() {
        return 0;
    }
}
