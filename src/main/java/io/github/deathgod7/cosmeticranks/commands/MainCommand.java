// This file is part of CosmeticRanks, created on 14/04/2024 (18:43 PM)
// Name : MainCommand
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.command.RootCommand;
import dev.triumphteam.cmd.core.annotations.*;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import io.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import io.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(value = "cosmeticranks", alias = {"cr", "cranks"})
@Permission("cosmeticranks.use")
public class MainCommand{
	private final CosmeticRanks instance;
	private final LuckPerms lp;
	private final BukkitAudiences audiences;
	private final DatabaseManager dbm;
	private final String pluginPrefix;

	public MainCommand() {
		this.instance = CosmeticRanks.getInstance();
		this.dbm = instance.getDBM();
		this.lp = this.instance.getLuckPerms();
		this.audiences = CosmeticRanks.getInstance().adventure();
		this.pluginPrefix = instance.getLanguageFile().getProperty("plugin.msgprefix")
				.replace("<prefix>", instance.getMainConfig().getPrefix());
	}

	// -------------------------------------------------------------------
	// --------------------[ PLUGIN SECTION ]-----------------------------
	// -------------------------------------------------------------------

	@Command
	public void maincmdExecutor(CommandSender sender) {
		info(sender);
	}

	@Command("info")
	@Permission("cosmeticranks.use")
	public void info(CommandSender commandSender){
		TextComponent databaseType;
		TextComponent isConnected;
		TextComponent version =  Component.text(CosmeticRanks.getPDFile().getVersion()).color(NamedTextColor.YELLOW);
		TextComponent apiversion;

		try {
			PluginDescriptionFile.class.getMethod("getAPIVersion");

			// If the method exists, try to get the API version
			String apiver = CosmeticRanks.getPDFile().getAPIVersion();
			if (apiver != null) {
				apiversion = Component.text(apiver).color(NamedTextColor.GOLD);
			} else {
				apiversion = Component.text("???").color(NamedTextColor.DARK_RED);
			}
		} catch (NoSuchMethodException e) {
			apiversion = Component.text("???").color(NamedTextColor.DARK_RED);
		}

		TextComponent developer = Component.text(CosmeticRanks.getPDFile().getAuthors().toString()).color(NamedTextColor.DARK_PURPLE);

		String temp1 = instance.getMainConfig().getDatabase().getType();
		switch (temp1) {
			case "mysql":
				databaseType = Component.text("MySQL").color(NamedTextColor.DARK_AQUA);
				break;
			case "mongodb":
				databaseType = Component.text("MongoDB").color(NamedTextColor.DARK_AQUA);
				break;
			case "sqlite":
				databaseType = Component.text("SQLite").color(NamedTextColor.DARK_AQUA);
				break;
			default:
				databaseType = Component.text("???").color(NamedTextColor.DARK_RED);
				break;
		}

		if (dbm.isConnected()){
			isConnected = Component.text("ONLINE").color(NamedTextColor.DARK_GREEN);
		}
		else{
			isConnected = Component.text("OFFLINE").color(NamedTextColor.DARK_RED);
		}

		audiences.sender(commandSender).sendMessage(Component.text("Cosmetic Ranks").color(NamedTextColor.GOLD));
		audiences.sender(commandSender).sendMessage(Component.text("Version : ").color(NamedTextColor.GRAY).append(version));
		audiences.sender(commandSender).sendMessage(Component.text("Developer(s) : ").color(NamedTextColor.GRAY).append(developer));
		audiences.sender(commandSender).sendMessage(Component.text("API Version : ").color(NamedTextColor.GRAY).append(apiversion));
		audiences.sender(commandSender).sendMessage(Component.text("Database : ").color(NamedTextColor.GRAY).append(databaseType));
		audiences.sender(commandSender).sendMessage(Component.text("Status : ").color(NamedTextColor.GRAY).append(isConnected));
	}


	@Command("reload")
	@Permission("cosmeticranks.use.reload")
	public void reload(CommandSender commandSender){
		Component msg = Component.text(instance.getLanguageFile().getProperty("plugin.reload"));
		Component success = Component.text(instance.getLanguageFile().getProperty("plugin.reload.success"));

		audiences.sender(commandSender).sendMessage(Component.text(pluginPrefix).append(msg));

		// reload

		audiences.sender(commandSender).sendMessage(Component.text(pluginPrefix).append(success));
	}


	// rank
	// /cr rank add <player> <track> <rank>

	@Command("rank")
	@Permission("cosmeticranks.rank")
	public class RankCommand {
		@Command(value = "add")
		@Permission("cosmeticranks.rank.add")
		public void addRank(CommandSender sender, Player player, @Suggestion("lptracks") String track, @Suggestion("ranks") String rank) {
			sender.sendMessage("Adding rank to player");
			sender.sendMessage("Player: " + player.getName());
			sender.sendMessage("Track: " + track);
			sender.sendMessage("Rank: " + rank);
			// Add rank to player
			List<Column> allCols = instance.getRankManager().getRanksTable().get(track).getColumns();

			Column colUUID = Helper.findColumn(allCols, "uuid");
			assert colUUID != null;
			colUUID.setValue(player.getUniqueId().toString());

			Column colObtainedranks = Helper.findColumn(allCols, "obtainedranks");
			assert colObtainedranks != null;

			List<String> obtainedRanks = new java.util.ArrayList<>(List.of(colObtainedranks.getValue().toString().split(",")));
			obtainedRanks.add(rank);

			colObtainedranks.setValue(String.join(",", obtainedRanks));

			if (dbm.getDatabase() instanceof SQLite) {
				dbm.getSQLite().updateData(track, colUUID, List.of(colObtainedranks));
			}
			else if (dbm.getDatabase() instanceof MySQL) {
				dbm.getMySQL().updateData(track, colUUID, List.of(colObtainedranks));
			}
			else if (dbm.getDatabase() instanceof MongoDB) {
				dbm.getMongoDB().updateData(track, colUUID, List.of(colObtainedranks));
			}
		}

		@Command(value = "give")
		@Permission("cosmeticranks.rank.add")
		public void giveRank(CommandSender sender, Player player, @Suggestion("lptracks") String track, @Suggestion("ranks") String rank) {
			addRank(sender, player, track, rank);
		}

		@Command(value = "remove")
		@Permission("cosmeticranks.rank.remove")
		public void removeRank(CommandSender sender, Player player, @Suggestion("lptracks") String track, @Suggestion("obtainedranks") String rank) {
			sender.sendMessage("Adding rank to player");
			sender.sendMessage("Player: " + player.getName());
			sender.sendMessage("Track: " + track);
			sender.sendMessage("Rank: " + rank);
			// Add rank to player
		}
	}

}
