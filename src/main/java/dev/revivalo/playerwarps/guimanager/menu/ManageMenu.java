package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.user.WarpAction;
import dev.revivalo.playerwarps.utils.DateUtils;
import dev.revivalo.playerwarps.utils.NumberUtils;
import dev.revivalo.playerwarps.utils.TextUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.actions.RelocateAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class ManageMenu implements Menu {
    private final Warp warp;
    private final Gui gui;
    public ManageMenu(Warp warp) {
        this.warp = warp;
        this.gui = Gui.gui()
                .title(Component.text(Lang.EDIT_WARP_MENU_TITLE.asColoredString().replace("%warp%", warp.getName())))
                .rows(6)
                .disableAllInteractions()
                .create();
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SET_UP_MENU;
    }

    @Override
    public void open(Player player) {
        gui.setItem(4, ItemBuilder.from(warp.getMenuItem())
                .name(Component.text(Lang.OWN_WARP_ITEM_NAME.asColoredString().replace("%warp%", warp.getName())))
                .lore(Lang.OWN_WARP_LORE.asReplacedList(new HashMap<String, String>() {{
                                                            put("%creationDate%", DateUtils.getFormatter().format(warp.getDateCreated()));
                                                            put("%world%", warp.getLocation().getWorld().getName());
                                                            put("%voters%", String.valueOf(warp.getReviewers().size()));
                                                            put("%price%", warp.getAdmission() == 0
                                                                    ? Lang.FREE_OF_CHARGE.asColoredString()
                                                                    : TextUtils.formatNumber(warp.getAdmission()) + " " + Config.CURRENCY_SYMBOL.asString());
                                                            put("%today%", String.valueOf(warp.getTodayVisits()));
                                                            put("%status%", warp.getStatus().getText());
                                                            put("%ratings%", String.valueOf(NumberUtils.round(warp.getConvertedRating(), 1)));
                                                            put("%stars%", TextUtils.createRatingFormat(warp));
                                                            put("%lore%", warp.getDescription() == null
                                                                    ? Lang.NO_DESCRIPTION.asColoredString()
                                                                    : warp.getDescription());
                                                            put("%visits%", String.valueOf(warp.getVisits()));
                                                            put("%owner-name%", Objects.requireNonNull(Bukkit.getOfflinePlayer(warp.getOwner()).getName()));
                                                        }}
                )).asGuiItem());

        gui.setItem(11, ItemBuilder.from(Material.SUNFLOWER).name(Component.text(Lang.SET_PRICE.asColoredString().replace("%warp%", warp.getName()))).lore(Lang.SET_PRICE_LORE.asColoredList()).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.SET_ADMISSION, new Object[]{warp.getWarpID(), WarpAction.SET_ADMISSION, true})));
        gui.setItem(12, ItemBuilder.from(warp.getCategory() == null ? new ItemStack(Material.WHITE_BANNER) : warp.getCategory().getItem()).name(Component.text(Lang.CHANGE_TYPE.asColoredString())).lore(Lang.CHANGE_TYPE_LORE.asColoredList()).asGuiItem(event -> new ChangeTypeMenu(warp).open(player))); //openChangeTypeMenu(player, warp)));
        /*gui.setItem(13, ItemBuilder.from(Material.IRON_DOOR).glow(warp.isPrivateState()).name(Component.text(Lang.PRIVACY.asColoredString())).lore(warp.isPrivateState() ? Lang.PRIVATE_ENABLE_LORE.asColoredList() : Lang.PRIVATE_DISABLE_LORE.asColoredList()).asGuiItem(event -> {
            PlayerWarpsPlugin.getWarpHandler().makePrivate(player, warp,false);
            openSetUpMenu(player, warp);
        }));*/
        gui.setItem(13, ItemBuilder.from(Material.WRITABLE_BOOK).name(Component.text(Lang.CHANGE_DISPLAY_NAME.asColoredString())).lore(Lang.CHANGE_DISPLAY_NAME_LORE.asReplacedList(Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.CHANGE_DISPLAY_NAME, new Object[]{warp.getWarpID(), WarpAction.CHANGE_DISPLAY_NAME, true})));
        gui.setItem(14, ItemBuilder.from(Material.ITEM_FRAME).name(Component.text(Lang.CHANGE_ITEM.asColoredString())).lore(Lang.CHANGE_ITEM_LORE.asColoredList()).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.SET_GUI_ITEM, new Object[]{warp.getWarpID(), WarpAction.SET_GUI_ITEM, true})));
        gui.setItem(15, ItemBuilder.from(Material.NAME_TAG).name(Component.text(Lang.CHANGE_DESCRIPTION.asColoredString())).lore(Lang.CHANGE_DESCRIPTION_LORE.asColoredList()).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.SET_DESCRIPTION, new Object[]{warp.getWarpID(), WarpAction.SET_DESCRIPTION, true})));
        gui.setItem(22, ItemBuilder.from(Material.IRON_DOOR).name(Component.text(Lang.PWARP_ACCESSIBILITY.asColoredString())).lore(Lang.PWARP_ACCESSIBILITY_LORE.asReplacedList(new HashMap<String, String>() {{
            put("%status%", warp.getStatus().getText());
        }})).asGuiItem(event -> new SetStatusMenu(warp).open(player))); //openSetStatusMenu(player, warp)));
        gui.setItem(39, ItemBuilder.from(Material.OAK_SIGN).name(Component.text(Lang.RENAME_WARP.asColoredString())).lore(Lang.RENAME_WARP_LORE.asColoredList()).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.RENAME, new Object[]{warp.getWarpID(), WarpAction.RENAME, true})));
        gui.setItem(38, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.REMOVE_WARP.asColoredString())).lore(Lang.REMOVE_WARP_LORE.asColoredList()).asGuiItem(event -> new ConfirmationMenu(warp).open(player, WarpAction.REMOVE))); //openAcceptMenu(player, warp, WarpAction.REMOVE)));
        gui.setItem(41, ItemBuilder.from(Material.WHITE_BANNER).name(Component.text(Lang.WARP_RELOCATION.asColoredString())).lore(Lang.WARP_RELOCATION_LORE.asColoredList()).asGuiItem(event -> new RelocateAction().preExecute(player, warp, null, MenuType.SET_UP_MENU)));//PlayerWarpsPlugin.getWarpHandler().relocateWarp(player, warp)));
        gui.setItem(42, ItemBuilder.from(Material.PLAYER_HEAD).name(Component.text(Lang.CHANGE_OWNER.asColoredString())).lore(Lang.CHANGE_OWNER_LORE.asColoredList()).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.CHANGE_OWNERSHIP, new Object[]{warp.getWarpID(), WarpAction.CHANGE_OWNERSHIP, true})));

        setDefaultItems(player, gui);
        //createGuiItems(player, gui, GUIManager.WarpMenuType.OWNED);

        gui.open(player);
    }
}
