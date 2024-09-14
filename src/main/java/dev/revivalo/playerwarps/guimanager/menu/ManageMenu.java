package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.hook.HookManager;
import dev.revivalo.playerwarps.user.WarpAction;
import dev.revivalo.playerwarps.util.DateUtil;
import dev.revivalo.playerwarps.util.ItemUtil;
import dev.revivalo.playerwarps.util.NumberUtil;
import dev.revivalo.playerwarps.util.TextUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.action.RelocateAction;
import dev.revivalo.playerwarps.warp.action.RemoveWarpAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        gui.setItem(4, ItemBuilder.from((warp.getMenuItem() == null ? ItemUtil.getItem(Config.DEFAULT_WARP_ITEM.asString(), player) : warp.getMenuItem().clone()))
                .setName(Lang.OWN_WARP_ITEM_NAME.asColoredString().replace("%warp%", warp.getName()))
                .setLore(Lang.OWN_WARP_LORE.asReplacedList(player, new HashMap<String, String>() {{
                                                            put("%creationDate%", DateUtil.getFormatter().format(warp.getDateCreated()));
                                                            put("%world%", warp.getLocation().getWorld().getName());
                                                            put("%voters%", String.valueOf(warp.getReviewers().size()));
                                                            put("%price%", warp.getAdmission() == 0
                                                                    ? Lang.FREE_OF_CHARGE.asColoredString()
                                                                    : TextUtil.formatNumber(warp.getAdmission()) + " " + Config.CURRENCY_SYMBOL.asString());
                                                            put("%today%", String.valueOf(warp.getTodayVisits()));
                                                            put("%status%", warp.getStatus().getText());
                                                            put("%ratings%", String.valueOf(NumberUtil.round(warp.getConvertedRating(), 1)));
                                                            put("%stars%", TextUtil.createRatingFormat(warp));
                                                            put("%lore%", warp.getDescription() == null
                                                                    ? Lang.NO_DESCRIPTION.asColoredString()
                                                                    : warp.getDescription());
                                                            put("%visits%", String.valueOf(warp.getVisits()));
                                                            put("%owner-name%", Objects.requireNonNull(Bukkit.getOfflinePlayer(warp.getOwner()).getName()));
                                                        }}
                )).asGuiItem());

        if (Config.SET_PRICE_POSITION.asInteger() > 0) if (Hook.isHookEnabled(Hook.getVaultHook()) && Config.MAX_WARP_ADMISSION.asInteger() > 0) gui.setItem(Config.SET_PRICE_POSITION.asInteger(), ItemBuilder.from(ItemUtil.getItem(Config.SET_PRICE_ITEM.asUppercase())).setName(Lang.SET_PRICE.asColoredString().replace("%warp%", warp.getName())).setLore(Lang.SET_PRICE_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.SET_ADMISSION)));
        if (Config.SET_CATEGORY_POSITION.asInteger() > 0) if (Config.ENABLE_CATEGORIES.asBoolean()) gui.setItem(Config.SET_CATEGORY_POSITION.asInteger(), ItemBuilder.from(warp.getCategory() == null ? ItemUtil.getItem(Config.SET_CATEGORY_ITEM.asUppercase()) : warp.getCategory().getItem()).setName((Lang.CHANGE_TYPE.asColoredString())).setLore(Lang.CHANGE_TYPE_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> new ChangeTypeMenu(warp).open(player)));

        if (Config.CHANGE_DISPLAY_NAME_POSITION.asInteger() > 0) gui.setItem(Config.CHANGE_DISPLAY_NAME_POSITION.asInteger(), ItemBuilder.from(ItemUtil.getItem(Config.CHANGE_DISPLAY_NAME_ITEM.asUppercase())).setName(Lang.CHANGE_DISPLAY_NAME.asColoredString()).setLore(Lang.CHANGE_DISPLAY_NAME_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.CHANGE_DISPLAY_NAME)));
        if (Config.CHANGE_PREVIEW_ITEM_POSITION.asInteger() > 0) gui.setItem(Config.CHANGE_PREVIEW_ITEM_POSITION.asInteger(), ItemBuilder.from(ItemUtil.getItem(Config.CHANGE_PREVIEW_ITEM.asUppercase())).setName(Lang.CHANGE_ITEM.asColoredString()).setLore(Lang.CHANGE_ITEM_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.SET_GUI_ITEM)));
        if (Config.CHANGE_DESCRIPTION_POSITION.asInteger() > 0) gui.setItem(Config.CHANGE_DESCRIPTION_POSITION.asInteger(), ItemBuilder.from(ItemUtil.getItem(Config.CHANGE_DESCRIPTION_ITEM.asUppercase())).setName(Lang.CHANGE_LABEL.asColoredString()).setLore(Lang.CHANGE_LABEL_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.SET_DESCRIPTION)));
        if (Config.CHANGE_ACCESSIBILITY_POSITION.asInteger() > 0) gui.setItem(Config.CHANGE_ACCESSIBILITY_POSITION.asInteger(), ItemBuilder.from(ItemUtil.getItem(Config.CHANGE_ACCESSIBILITY_ITEM.asUppercase())).setName(Lang.PWARP_ACCESSIBILITY.asColoredString()).setLore(Lang.PWARP_ACCESSIBILITY_LORE.asReplacedList(player, new HashMap<String, String>() {{
            put("%status%", warp.getStatus().getText());
        }})).asGuiItem(event -> new SetStatusMenu(warp).open(player)));
        if (Config.RENAME_WARP_POSITION.asInteger() > 0) gui.setItem(Config.RENAME_WARP_POSITION.asInteger(), ItemBuilder.from(ItemUtil.getItem(Config.RENAME_WARP_ITEM.asUppercase())).setName(Lang.RENAME_WARP.asColoredString()).setLore(Lang.RENAME_WARP_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.RENAME)));
        if (Config.REMOVE_WARP_POSITION.asInteger() > 0) gui.setItem(Config.REMOVE_WARP_POSITION.asInteger(), ItemBuilder.from(ItemUtil.getItem(Config.REMOVE_WARP_ITEM.asUppercase())).setName(Lang.REMOVE_WARP.asColoredString()).setLore(Lang.REMOVE_WARP_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> new ConfirmationMenu(warp).open(player, new RemoveWarpAction())));
        if (Config.RELOCATE_WARP_POSITION.asInteger() > 0) gui.setItem(Config.RELOCATE_WARP_POSITION.asInteger(), ItemBuilder.from(ItemUtil.getItem(Config.RELOCATE_WARP_ITEM.asUppercase())).setName(Lang.WARP_RELOCATION.asColoredString()).setLore(Lang.WARP_RELOCATION_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> new RelocateAction().preExecute(player, warp, null, MenuType.MANAGE_MENU, 1)));
        if (Config.CHANGE_OWNER_POSITION.asInteger() > 0) gui.setItem(Config.CHANGE_OWNER_POSITION.asInteger(), ItemBuilder.from(ItemUtil.getItem(Config.CHANGE_OWNER_ITEM.asUppercase())).setName(Lang.CHANGE_OWNER.asColoredString()).setLore(Lang.CHANGE_OWNER_LORE.asReplacedList(player, Collections.emptyMap())).asGuiItem(event -> PlayerWarpsPlugin.getWarpHandler().markPlayerForChatInput(player, warp, WarpAction.CHANGE_OWNERSHIP)));

        setDefaultItems(player, gui);

        gui.open(player);
    }
}