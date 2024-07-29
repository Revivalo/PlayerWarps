package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.hooks.Hooks;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.PlayerUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public class TeleportToWarpAction implements WarpAction<String> {
    @Override
    public boolean execute(Player player, Warp warp, String password) {
        if (warp == null)
            return false;

        final String warpName = warp.getName();
        boolean isOwner = warp.canManage(player);

        if (!warp.isAccessible() && !isOwner) {
            player.sendMessage(Lang.WARP_IS_DISABLED.asColoredString().replace("%warp%", warpName));
            return false;
        }

        Teleport teleport = new Teleport(player, warp.getLocation());
        teleport.proceed();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (teleport.getTask().isResulted()) {
                    cancel();
                    if (teleport.getTask().getStatus() == Teleport.Status.SUCCESS) {
                        if (!warp.canManage(player)) {
                            if (Hooks.isHookEnabled(Hooks.getVaultHook())) {
                                Economy economy = Hooks.getVaultHook().getApi();

                                economy.withdrawPlayer(player, warp.getAdmission());

                                PlayerUtils.getOfflinePlayer(warp.getOwner()).thenAccept(
                                        offlinePlayer -> economy.depositPlayer(offlinePlayer, warp.getAdmission())
                                );
                            }
                        }

                        if (Config.WARP_VISIT_NOTIFICATION.asBoolean()) {
                            PlayerUtils.announce(Lang.WARP_VISIT_NOTIFICATION.asColoredString()
                                    .replace("%warp%", warpName)
                                    .replace("%player%", player.getName())
                            );

                            final UUID ownerID = warp.getOwner();

                            PlayerUtils.getOfflinePlayer(ownerID).thenAccept(offlinePlayer -> {
                                if (warp.getAdmission() != 0 && !isOwner) {
                                    player.sendMessage(Lang.TELEPORT_TO_WARP_WITH_ADMISSION.asColoredString()
                                            .replace("%price%", String.valueOf(warp.getAdmission()))
                                            .replace("%warp%", warpName)
                                            .replace("%player%", Objects.requireNonNull(Bukkit.getOfflinePlayer(ownerID).getName())));
                                } else
                                    player.sendMessage(Lang.TELEPORT_TO_WARP.asColoredString()
                                            .replace("%warp%", warpName)
                                            .replace("%player%", Objects.requireNonNull(Bukkit.getOfflinePlayer(ownerID).getName())));

                            });

                            if (!isOwner) {
                                warp.setVisits(warp.getVisits() + 1);
                                warp.setTodayVisits(warp.getTodayVisits() + 1);
                            }
                        }
                    } else if (teleport.getTask().getStatus() == Teleport.Status.ERROR) {
                        player.sendMessage(Lang.TELEPORTATION_CANCELLED.asColoredString());
                    }
                }
            }
        }.runTaskTimer(PlayerWarpsPlugin.get(), 4, 4);


        return true;
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
