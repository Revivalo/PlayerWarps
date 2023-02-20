package cz.revivalo.playerwarps.warp;

import cz.revivalo.playerwarps.configuration.enums.Lang;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WarpStatus {
    OPENED(Lang.OPENED_STATUS.asColoredString()),
    CLOSED(Lang.CLOSED_STATUS.asColoredString()),
    PASSWORD_PROTECTED(Lang.PASSWORD_PROTECTED_STATUS.asColoredString());

    @Getter
    private final String text;
}
