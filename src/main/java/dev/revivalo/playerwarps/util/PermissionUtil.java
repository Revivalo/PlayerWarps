package dev.revivalo.playerwarps.util;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public final class PermissionUtil {
    public static boolean hasPermission(CommandSender commandSender, Permission permission){
        if (commandSender.isOp())
            return true;

        else if (commandSender.hasPermission(Permission.ADMIN_PERMISSION.asString()))
            return true;

        else
            return commandSender.hasPermission(permission.asString());
    }

    public static int getLimit(Player player, int defaultValue) {
        String permissionPrefix = "playerwarps.limit.";
        int maxLimit = defaultValue;

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getPermission().startsWith(permissionPrefix)) {
                try {
                    // Získání čísla na konci oprávnění
                    int limit = Integer.parseInt(attachmentInfo.getPermission().substring(permissionPrefix.length()));
                    // Aktualizace maxLimit, pokud je nalezen vyšší limit
                    maxLimit = Math.max(maxLimit, limit);
                } catch (NumberFormatException e) {
                    // Ignoruje neplatné oprávnění, které není celé číslo
                    PlayerWarpsPlugin.get().getLogger().warning("Invalid limit permission: " + attachmentInfo.getPermission());
                }
            }
        }

        return maxLimit;
    }


    public enum Permission {
        ADMIN_PERMISSION("playerwarps.admin"),
        RELOAD_PLUGIN("playerwarps.reload"),
        VERIFY("playerwarps.verify"),
        USE("playerwarps.use"),
        MANAGE("playerwarps.manage"),
        HELP("playerwarps.help"),
        FAVORITE_WARP("playerwarps.favorite"),
        REVIEW_WARP("playerwarps.review"),
        CREATE_WARP("playerwarps.create"),
        REMOVE_WARP("playerwarps.remove"),
        TRANSFER_WARP("playerwarps.transfer"),
        RENAME_WARP("playerwarps.rename"),
        RELOCATE_WARP("playerwarps.relocate"),
        SET_WARP_TYPE("playerwarps.settings.settype"),
        SET_PREVIEW_ITEM("playerwarps.settings.setitem"),
        SET_DESCRIPTION("playerwarps.settings.setdescription"),
        SET_ADMISSION("playerwarps.settings.setadmission"),
        SET_STATUS("playerwarps.settings.setstatus"),
        BLOCK_PLAYER("playerwarps.settings.blockPlayer"),
        CHANGE_DISPLAY_NAME("playerwarps.settings.changedisplayname"),
        BYPASS_TELEPORT_DELAY("playerwarps.delay.bypass"),
        MANAGE_OTHERS("playerwarps.manage.others");

        private final String permission;
        Permission(String permission) {
            this.permission = permission;
        }

        public String asString() {
            return permission;
        }
    }
}
