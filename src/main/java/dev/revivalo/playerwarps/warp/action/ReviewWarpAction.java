package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.util.TextUtil;
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
            warp.setStars(TextUtil.createRatingFormat(warp));
            player.sendMessage(Lang.WARP_REVIEWED.asColoredString().
                    replace("%warp%", warp.getName()).
                    replace("%stars%", String.valueOf(stars)));
        }

        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.REVIEW_WARP;
    }

    @Override
    public int getFee() {
        return 0;
    }

    @Override
    public Lang getInputText() {
        return null;
    }

    @Override
    public boolean isPublicAction() {
        return true;
    }
}
