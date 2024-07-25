package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
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
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.SET_ADMISSION;
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
        } catch (Throwable e) {
            return false;
        }
        return true;
    }
}
