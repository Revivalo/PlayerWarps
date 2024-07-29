package dev.revivalo.playerwarps.warp.actions;

import de.rapha149.signgui.SignGUI;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.categories.Category;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.hooks.Hooks;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.PlayerUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;

public class PreTeleportToWarpAction implements WarpAction<String> {
    @Override
    public boolean execute(Player player, Warp warp, String data) {
        final Category category = warp.getCategory();
        if (!category.hasPermission(player)) {
            player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString().replace("%permission%", category.getPermission()));
            return false;
        }

        if (!warp.canManage(player)) {
            if (warp.getAdmission() != 0) {
                if (Hooks.getVaultHook().getApi() != null) {
                    Economy economy = Hooks.getVaultHook().getApi();
                    if (!economy.has(player, warp.getAdmission())) {
                        player.sendMessage(Lang.INSUFFICIENT_BALANCE_TO_TELEPORT.asColoredString().replace("%warp%", warp.getName()));
                        return false;
                    }
                }
            }
        }

        if (warp.isPasswordProtected() && !warp.canManage(player)) {
            SignGUI gui = SignGUI.builder()
                    .setType(Material.OAK_SIGN)
                    .setColor(DyeColor.BLACK)
                    .setLine(1, Lang.ENTER_PASSWORD.asColoredString())
                    .setHandler((p, result) -> {
                        String input = result.getLineWithoutColor(0);

                        if (input.isEmpty()) {
                            return Collections.emptyList();
                        }

                        if (input.length() < 3 || input.length() > 15) {
                            return Collections.emptyList();
                        }

                        if (warp.isPasswordProtected()) {
                            if (!warp.validatePassword(input)) {
                                player.sendMessage(Lang.ENTERED_WRONG_PASSWORD.asColoredString());
                                return Collections.emptyList();
                            }
                        }

                        PlayerWarpsPlugin.get().runDelayed(() -> new TeleportToWarpAction().preExecute(player, warp, input, null), 2);

                        return Collections.emptyList();
                    })

                    .build();

            gui.open(player);

        } else new TeleportToWarpAction().preExecute(player, warp, null, null);
        return false;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.USE;
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
