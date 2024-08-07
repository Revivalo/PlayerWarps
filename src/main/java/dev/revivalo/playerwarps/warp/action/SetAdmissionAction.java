package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

public class SetAdmissionAction implements WarpAction<String> {
    @Override
    public boolean execute(Player player, Warp warp, String text) {
        if (!isInt(text)) {
            player.sendMessage(Lang.NOT_A_NUMBER.asColoredString().replace("%input%", text));
            return false;
        }

        int price = Integer.parseInt(text);
        if (price < 0) {
            player.sendMessage(Lang.INVALID_ENTERED_PRICE.asColoredString());
            return false;
        }

        if (price > Config.MAX_WARP_ADMISSION.asInteger()) {
            player.sendMessage(Lang.ENTERED_HIGHER_PRICE_THAN_ALLOWED.asColoredString().replace("%max%", Config.MAX_WARP_ADMISSION.asString()));
            return false;
        }

        warp.setAdmission(price);
        player.sendMessage(Lang.PRICE_CHANGED.asColoredString()
                .replace("%warp%", warp.getName())
                .replace("%price%", price == 0
                        ? Lang.FREE_OF_CHARGE.asColoredString()
                        : String.valueOf(price)));

        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.SET_ADMISSION;
    }

    @Override
    public int getFee() {
        return Config.SET_ADMISSION_FEE.asInteger();
    }

    @Override
    public Lang getInputText() {
        return null;
    }

    @Override
    public boolean isPublicAction() {
        return false;
    }

    private boolean isInt(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
