package dev.revivalo.playerwarps.warp.action;

import de.rapha149.signgui.SignGUI;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.category.Category;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.guimanager.menu.ConfirmationMenu;
import dev.revivalo.playerwarps.guimanager.menu.Menu;
import dev.revivalo.playerwarps.hook.HookManager;
import dev.revivalo.playerwarps.util.NumberUtil;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;

public class PreTeleportToWarpAction implements WarpAction<String> {
    private Menu menuToOpen = null;
    @Override
    public boolean execute(Player player, Warp warp, String data) {
        final Category category = warp.getCategory();
        if (!category.hasPermission(player)) {
            player.sendMessage(Lang.INSUFFICIENT_PERMISSIONS.asColoredString().replace("%permission%", category.getPermission()));
            return false;
        }

        if (!warp.canManage(player)) {
            if (warp.hasAdmission()) {
                if (HookManager.getVaultHook().getApi() != null) {
                    Economy economy = HookManager.getVaultHook().getApi();
                    if (!economy.has(player, warp.getAdmission())) {
                        player.sendMessage(Lang.INSUFFICIENT_BALANCE_TO_TELEPORT.asColoredString()
                                .replace("%warp%", warp.getName())
                                .replace("%price%", NumberUtil.formatNumber(warp.getAdmission())));
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

                        PlayerWarpsPlugin.get().runDelayed(() -> {
                            if (warp.hasAdmission() && !warp.canManage(player)) {
                                new ConfirmationMenu(warp)
                                        .setMenuToOpen(menuToOpen)
                                        .open(player, new TeleportToWarpAction());
                            } else new TeleportToWarpAction().preExecute(player, warp, input, null);
                        }, 2);

                        return Collections.emptyList();
                    })

                    .build();

            gui.open(player);

        } else {
            if (warp.hasAdmission() && !warp.canManage(player))
                new ConfirmationMenu(warp)
                        .setMenuToOpen(menuToOpen)
                        .open(player, new TeleportToWarpAction());
            else new TeleportToWarpAction().preExecute(player, warp, null, null);
        }
        return false;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.USE;
    }

    @Override
    public boolean isPublicAction() {
        return true;
    }

    public PreTeleportToWarpAction setMenuToOpen(Menu menu) {
        this.menuToOpen = menu;
        return this;
    }
}
