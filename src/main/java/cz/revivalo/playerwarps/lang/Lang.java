package cz.revivalo.playerwarps.lang;

import cz.revivalo.playerwarps.PlayerWarps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Lang {
    UPDATECHECKER("update-checker"),
    AUTOSAVEENABLED("autosave-enabled"),
    AUTOSAVEANNOUNCE("autosave-announce"),
    AUTOSAVEINTERVAL("autosave-interval"),
    TELEPORATIONDELAY("teleport-delay"),
    WARPNAMEFORMAT("warp-name-format"),
    WARPPRICE("warp-price"),
    DATEFORMAT("date-format"),
    DELETEWARPREFUND("delete-warp-refund"),
    WARPNAMEMAXLENGTH("warp-name-max-lenght"),
    MAXWARPADMISSION("max-warp-admission"),
    DEFAULTWARPITEM("default-warp-item"),
    DEFAULTLIMITSIZE("default-limit-size"),
    ALLOWACCEPTTELEMPORTMENU("allow-teleport-accept-menu"),
    FAVORITEWARPSITEM("favorite-warps-item"),
    WARPLISTITEM("warp-list-item"),
    MYWARPSITEM("my-warps-item"),
    ENTEREDHIGHERPRICETHATNALLOWED("entered-higher-price-than-allowed"),
    ACCEPTTELEPORTWITHADMISSION("accept-teleport-with-admission"),
    TELEPORTTOWARPWITHADMISSION("teleport-to-warp-with-admission"),
    HELP("help-message-lore"),
    OWNWARPITEMNAME("own-warp-item-name"),
    RELOAD("reload-message"),
    LIMITREACHED("limit-reached"),
    NOWARPS("no-warps"),
    NONEXISTINGWARP("non-existing-warp"),
    WARPALREADYCREATED("warp-already-created"),
    WARPCREATED("warp-created"),
    WARPCREATEDWITHPRICE("warp-created-with-price"),
    CURRENCY("currency"),
    INSUFFICIENTBALANCE("insufficient-balance"),
    INSUFFICIENTBALANCETOTELEPORT("insufficient-balance-to-teleport"),
    WARPREMOVED("warp-removed"),
    WARPREMOVEDWITHREFUND("warp-removed-with-refund"),
    TELEPORTATION("teleportation"),
    TELEPORTATIONCANCELLED("teleportation-cancelled"),
    TELEPORTTOWARP("teleport-to-warp"),
    NAMECANTCONTAINSDOT("name-cant-contains-dot"),
    INSUFFICIENTPERMS("insufficient-perms"),
    ITEMCHANGED("item-changed"),
    TITLEWRITEMSG("title-write-msg"),
    RENAMEMSG("rename-write-msg"),
    ITEMWRITEMSG("item-write-msg"),
    TEXTWRITECANCELED("text-write-canceled"),
    TEXTSIZEERROR("text-size-error"),
    TEXTCHANGED("text-changed"),
    NOTOWNING("not-owning"),
    INVALIDITEM("invalid-item"),
    ALREADYREVIEWED("already-reviewed"),
    INVALIDREVIEW("invalid-review"),
    SELFREVIEW("selfreview"),
    WARPREVIEWED("warp-reviewed"),
    TRIEDTOSETBANNEDITEM("tried-to-set-banned-item"),
    NOTANUMBER("not-a-number"),
    PRICEWRITEMSG("price-write-msg"),
    PRICECHANGED("price-changed"),
    INVALIDENTEREDPRICE("invalid-entered-price"),
    WARPSITEMNAME("warps-item-name"),
    MYWARPSITEMNAME("my-warps-item-name"),
    HELPITEMNAME("help-item-name"),
    FAVORITEWARPSITEMNAME("favorite-warps-item-name"),
    MYWARPSTITLE("my-warp-title"),
    WARPSTITLE("warps-title"),
    HELPTITLE("help-title"),
    FAVORITESTITLE("favorites-title"),
    ACCEPTMENUTITLE("accept-menu-title"),
    ACCEPT("accept"),
    DENY("deny"),
    PAGEITEM("page-item"),
    NEXTPAGE("next-page"),
    PREVIOUSPAGE("previous-page"),
    NODESCRIPTION("no-description"),
    STARREVIEWITEM("star-review-item"),
    BACKITEM("back-item"),
    BACKNAME("back-name"),
    ONESTAR("1star"),
    TWOSTARS("2star"),
    THREESTARS("3star"),
    FOURSTARS("4star"),
    FIVESTARS("5star"),
    WARPENABLED("warp-enabled"),
    WARPDISABLED("warp-disabled"),
    WARPISDISABLED("warp-is-disabled"),
    ENTEREDINVALIDTYPE("entered-invalid-type-of-warp"),
    WARPTYPECHANGED("warp-type-changed"),
    PRIVACYCHANGED("privacy-changed"),
    ENABLECATEGORIES("enable-categories"),
    ENABLECATEGORIESBACKGROUND("categories-background-item"),
    BADCOMMANDSYNTAX("bad-command-syntax"),
    ADDFAVORITE("favorite-warp-added"),
    REMOVEFAVORITE("remove-favorite-warp"),
    FAVNOTCONTAINS("warp-not-contains-favlist"),
    ALREADYFAVORITE("warp-already-contains-favlist"),
    TRANSFERSUCCESFUL("ownership-transfer-successful"),
    TRANSFERINFO("ownership-transfer-info"),
    TRANSFERERROR("ownership-transfer-error"),
    OWNERCHANGEMSG("owner-change-msg"),
    WARPRENAMED("warp-name-changed"),
    TRIEDTOCREATEPWARPINDISABLEDWORLD("tried-to-create-warp-in-disabled-world"),
    WARPNAMEISABOVELETTERLIMIT("warp-name-is-above-letters-limit"),
    CATEGORYTITLE("category-title"),
    FREEOFCHARGE("free-of-charge"),
    WARPACTIVE("warp-active"),
    WARPINACTIVE("warp-inactive"),
    REMOVEWARP("remove-warp"),
    CHANGETYPE("change-type"),
    CHANGEITEM("change-item"),
    CHANGELABEL("change-label"),
    PWARPENABLE("pwarp-enable"),
    PRIVACY("privacy"),
    SETPRICE("set-price"),
    CHANGEOWNER("change-owner"),
    RENAMEWARP("rename-warp"),
    BANNEDITEMS("banned-items"),
    BANNEDWORLDS("disabled-worlds"),
    WARPLORE("warp-lore"),
    OWNWARPLORE("own-warp-lore"),
    HELPLORE("help-lore"),
    REMOVEWARPLORE("remove-warp-lore"),
    CHANGETYPELORE("change-type-lore"),
    CHANGEITEMLORE("change-item-lore"),
    CHANGELABELLORE("change-label-lore"),
    PWARPENABLELORE("pwarp-enabled-lore"),
    PWARPDISABLELORE("pwarp-disabled-lore"),
    PRIVATEENABLELORE("private-enabled-lore"),
    PRIVATEDISABLE("private-disabled-lore"),
    SETPRICELORE("set-price-lore"),
    CHANGEOWNERLORE("change-owner-lore"),
    RENAMEWARPLORE("rename-warp-lore");

    private final String text;
    private static final Map<String, String> messages = new HashMap<>();
    private static final Map<String, List<String>> lores = new HashMap<>();

    Lang(String text) {
        this.text = text;
    }

    public String content() {return applyColor(messages.get(text));}

    public boolean getBoolean(){return Boolean.parseBoolean(messages.get(text));}

    public int getInt(){return Integer.parseInt(messages.get(text));}

    public long getLong(){return Long.parseLong(messages.get(text));}

    public List<String> contentLore() {
        List<String> lore = new ArrayList<>();

        for (String str : lores.get(this.text)) {
            lore.add(applyColor(str));
        }

        return lore;
    }

    private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");
    public static String applyColor(String message){
        if (PlayerWarps.isHexSupport) {
            Matcher matcher = hexPattern.matcher(message);
            while (matcher.find()) {
                final ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
                final String before = message.substring(0, matcher.start());
                final String after = message.substring(matcher.end());
                message = before + hexColor + after;
                matcher = hexPattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void reload() {
        FileConfiguration cfg = PlayerWarps.getPlugin(PlayerWarps.class).getConfig();
        for (String key : cfg.getConfigurationSection("config").getKeys(true)) {
            if (key.endsWith("lore") || key.endsWith("items") || key.endsWith("worlds")) {
                lores.put(key, cfg.getStringList("config." + key));
            } else {
                messages.put(key, cfg.getString("config." + key));
            }
        }
    }

    static {
        reload();
    }
}
