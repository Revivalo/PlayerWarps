package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.categories.Category;
import dev.revivalo.playerwarps.categories.CategoryManager;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class SetTypeAction implements WarpAction<Category> {
    @Override
    public boolean execute(Player player, Warp warp, @Nullable Category category) {
        if (!Optional.ofNullable(category).isPresent()) {
            player.sendMessage(Lang.ENTERED_INVALID_TYPE.asReplacedString(player, new HashMap<String, String>() {{
                put("%types%", CategoryManager.getCategories().stream().map(Category::getType).collect(Collectors.joining(", ")));
            }}));
            return false;
        }

        warp.setCategory(category);
        player.sendMessage(Lang.WARP_TYPE_CHANGED.asColoredString().
                replace("%warp%", warp.getName()).
                replace("%type%", category.getType())
        );

        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.SET_WARP_TYPE;
    }

    @Override
    public int getFee() {
        return Config.SET_TYPE_FEE.asInteger();
    }
}
