package dev.revivalo.playerwarps.updatechecker;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.util.VersionUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateNotificator implements Listener {

	public static final UpdateNotificator instance = new UpdateNotificator();

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void playerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		if (!player.isOp()) return;
		if (!Config.UPDATE_CHECKER.asBoolean()) return;
		if (VersionUtil.isLatestVersion()) return;

		TextComponent download = new TextComponent("§3§lDownload");
		download.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/playerwarps-easy-warping-system-hex-colors-support-custom-categories-1-13-1-20-1.79089/"));

		TextComponent changelog = new TextComponent("§3§lChangelog");
		changelog.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/playerwarps-easy-warping-system-hex-colors-support-custom-categories-1-13-1-20-1.79089/updates"));

		TextComponent upgrade = new TextComponent("§3§lDonate");
		upgrade.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.paypal.me/revivalo"));

		TextComponent donate = new TextComponent("§3§lSupport");
		donate.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/kcxYUQTy6A"));


		new BukkitRunnable() {
			@Override
			public void run() {
				player.sendMessage(" ");
				player.sendMessage("§7There is a new version of §bPlayerWarps§7 available.");
				player.spigot().sendMessage(download, new TextComponent(" §8| "), upgrade, new TextComponent(" §8| "), changelog, new TextComponent(" §8| "), donate);
				player.sendMessage("§8Latest version: §a" + PlayerWarpsPlugin.getLatestVersion() + " §8| Your version: §c" + PlayerWarpsPlugin.get().getDescription().getVersion());
				player.sendMessage(" ");
			}
		}.runTaskLater(PlayerWarpsPlugin.get(), 36);
	}

}
