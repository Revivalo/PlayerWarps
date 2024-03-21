package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.hooks.Hooks;
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
        return MenuType.MANAGE_MENU;
    }

    @Override
    public void open(Player player) {
        gui.setItem(4, ItemBuilder.from(warp.getMenuItem())
                .name(Component.text(Lang.OWN_WARP_ITEM_NAME.asColoredString().replace("%warp%", warp.getName())))
                .setLore(Lang.OWN_WARP_LORE.asReplacedList(player, new HashMap<String, String>() {{
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

        if (Hooks.isHookEnabled(Hooks.getVaultHook()) && Config.MAX_WARP_ADMISSION.asInt() > 0) gui.setItem(Config.SET_PRICE_POSITION.asInt(), ItemBuilder.from(Material.SUNFLOWER).name(Component.text(Lang.SET_PRICE.asColoredString().replace("%warp%", warp.getName()))).setLore(Lang.SET_PRICE_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.SET_ADMISSION)));
        if (Config.ENABLE_CATEGORIES.asBoolean()) gui.setItem(Config.SET_CATEGORY_POSITION.asInt(), ItemBuilder.from(warp.getCategory() == null ? new ItemStack(Material.WHITE_BANNER) : warp.getCategory().getItem()).name(Component.text(Lang.CHANGE_TYPE.asColoredString())).setLore(Lang.CHANGE_TYPE_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> new ChangeTypeMenu(warp).open(player)));
        gui.setItem(Config.CHANGE_DISPLAY_NAME_POSITION.asInt(), ItemBuilder.from(Material.WRITABLE_BOOK).name(Component.text(Lang.CHANGE_DISPLAY_NAME.asColoredString())).setLore(Lang.CHANGE_DISPLAY_NAME_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.CHANGE_DISPLAY_NAME)));
        gui.setItem(Config.CHANGE_PREVIEW_ITEM_POSITION.asInt(), ItemBuilder.from(Material.ITEM_FRAME).name(Component.text(Lang.CHANGE_ITEM.asColoredString())).setLore(Lang.CHANGE_ITEM_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.SET_GUI_ITEM)));
        gui.setItem(Config.CHANGE_DESCRIPTION_POSITION.asInt(), ItemBuilder.from(Material.NAME_TAG).name(Component.text(Lang.CHANGE_DESCRIPTION.asColoredString())).setLore(Lang.CHANGE_DESCRIPTION_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.SET_DESCRIPTION)));
        gui.setItem(Config.CHANGE_ACCESSIBILITY_POSITION.asInt(), ItemBuilder.from(Material.IRON_DOOR).name(Component.text(Lang.PWARP_ACCESSIBILITY.asColoredString())).setLore(Lang.PWARP_ACCESSIBILITY_LORE.asReplacedList(player, new HashMap<String, String>() {{
            put("%status%", warp.getStatus().getText());
        }})).asGuiItem(event -> new SetStatusMenu(warp).open(player))); //openSetStatusMenu(player, warp)));
        gui.setItem(Config.RENAME_WARP_POSITION.asInt(), ItemBuilder.from(Material.OAK_SIGN).name(Component.text(Lang.RENAME_WARP.asColoredString())).setLore(Lang.RENAME_WARP_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.RENAME)));
        gui.setItem(Config.REMOVE_WARP_POSITION.asInt(), ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.REMOVE_WARP.asColoredString())).setLore(Lang.REMOVE_WARP_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> new ConfirmationMenu(warp).open(player, WarpAction.REMOVE))); //openAcceptMenu(player, warp, WarpAction.REMOVE)));
        gui.setItem(Config.RELOCATE_WARP_POSITION.asInt(), ItemBuilder.from(Material.WHITE_BANNER).name(Component.text(Lang.WARP_RELOCATION.asColoredString())).setLore(Lang.WARP_RELOCATION_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> new RelocateAction().preExecute(player, warp, null, MenuType.MANAGE_MENU, 1)));//PlayerWarpsPlugin.getWarpHandler().relocateWarp(player, warp)));
        gui.setItem(Config.CHANGE_OWNER_POSITION.asInt(), ItemBuilder.from(Material.PLAYER_HEAD).name(Component.text(Lang.CHANGE_OWNER.asColoredString())).setLore(Lang.CHANGE_OWNER_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.CHANGE_OWNERSHIP)));

        setDefaultItems(player, gui);
        //createGuiItems(player, gui, GUIManager.WarpMenuType.OWNED);

        gui.open(player);
    }
}
