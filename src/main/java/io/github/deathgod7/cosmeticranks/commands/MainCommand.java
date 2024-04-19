// This file is part of CosmeticRanks, created on 14/04/2024 (18:43 PM)
// Name : MainCommand
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.command.RootCommand;
import dev.triumphteam.cmd.core.annotations.*;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.component.Table;
import io.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import io.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import io.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.PrefixNode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

import java.util.*;

@Command(value = "cosmeticranks", alias = {"cr", "cranks"})
@Permission("cosmeticranks.use")
public class MainCommand{
	private final CosmeticRanks instance;
	private final LuckPerms lp;
	private final BukkitAudiences audiences;
	private final MiniMessage mm;
	private final DatabaseManager dbm;
	private final String pluginPrefix;
	private final Properties lang;

	public MainCommand() {
		this.instance = CosmeticRanks.getInstance();
		this.dbm = instance.getDBM();
		this.lp = this.instance.getLuckPerms();
		this.audiences = CosmeticRanks.getInstance().adventure();
		this.mm = instance.getMiniMessage();
		this.lang = instance.getLanguageFile();
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


	public List<Column> getPlayerDatas(OfflinePlayer player, String table) {
		Column uuid = new Column("uuid", player.getUniqueId().toString(), DatabaseManager.DataType.VARCHAR);
		List<Column> allCols = null;

		if (dbm.getDatabase() instanceof SQLite) {
			allCols = dbm.getSQLite().getExactData(table, uuid);
		}
		else if (dbm.getDatabase() instanceof MySQL) {
			allCols = dbm.getMySQL().getExactData(table, uuid);
		}
		else if (dbm.getDatabase() instanceof MongoDB) {
			allCols = dbm.getMongoDB().getExactData(table, uuid);
		}

		return allCols;
	}

	public boolean updatePlayerData(String table, OfflinePlayer player, List<Column> columns) {
		Column pk = new Column("uuid", player.getUniqueId().toString(), DatabaseManager.DataType.VARCHAR);

		if (dbm.getDatabase() instanceof SQLite) {
			return dbm.getSQLite().updateData(table, pk, columns);
		}
		else if (dbm.getDatabase() instanceof MySQL) {
			return dbm.getMySQL().updateData(table, pk, columns);
		}
		else if (dbm.getDatabase() instanceof MongoDB) {
			return dbm.getMongoDB().updateData(table, pk, columns);
		}
		return false;
	}

	public OfflinePlayer getPlayer(CommandSender sender, String player) {
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			if (p.getName() != null && p.getName().equals(player)) {
				return p;
			}
		}

		Component error = Helper.deserializeString(lang.getProperty("player.notfound")
				.replace("<player>", player)
		);
		Logger.log(error, sender);

		return  null;

	}

	public boolean isTrackInConfig(CommandSender sender, String track) {
		boolean res = instance.getMainConfig().getLptracks().containsKey(track);

		if (!res) {
			Component error = Helper.deserializeString(lang.getProperty("track.notinconfig")
					.replace("<track>", track)
			);
			Logger.log(error, sender);
		}

		return res;
	}

	public String getGroupDisplayName(String group) {
		Group rankgroup = lp.getGroupManager().getGroup(group);

		if (rankgroup == null) {
			Logger.log(Component.text(lang.getProperty("group.notfound").replace("<group>", group)), Logger.LogTypes.debug);
			return "???";
		}

		String out = rankgroup.getDisplayName();
		if (out == null) {
			out = StringUtils.capitalize(group);
		}

		return out;
	}
	public String getGroupPrefix(String group) {
		Group rankgroup = lp.getGroupManager().getGroup(group);

		if (rankgroup == null) {
			Logger.log(Component.text(lang.getProperty("group.notfound").replace("<group>", group)), Logger.LogTypes.debug);
			return "???";
		}

		String out = rankgroup.getNodes().stream()
				.filter(node -> node instanceof PrefixNode)
				.map(node -> ((PrefixNode) node).getMetaValue())
				.findFirst().orElse(null);

		if (out == null) {
			out = StringUtils.capitalize(group);
		}

		return out;
	}

	@Command("rank")
	@Permission("cosmeticranks.rank")
	public class RankCommand {
		@Command(value = "add")
		@Permission("cosmeticranks.rank.add")
		public void addRank(CommandSender sender, @Suggestion("allplayers") String player, @Suggestion("lptracks") String track, @Suggestion("ranks") String rank) {
			OfflinePlayer pl = getPlayer(sender, player);

			if (pl == null || pl.getName() == null) return; // frick you for providing unknown player

			if (!isTrackInConfig(sender,track)) {
				return;
			}

			// check if track or rank exists
			if (lp.getTrackManager().getTrack(track) == null) {
				Component errorNoTrack = Helper.deserializeString(lang.getProperty("rank.add.notrack")
						.replace("<track>", track)
				);
				Logger.log(errorNoTrack, sender);
				return;
			}
			else if (!Objects.requireNonNull(lp.getTrackManager().getTrack(track)).getGroups().contains(rank)) {
					Component errorNoRank = Helper.deserializeString(lang.getProperty("rank.add.norank")
							.replace("<track>", track)
							.replace("<rank>", rank)
					);
					Logger.log(errorNoRank, sender);
					return;
			}

			// Add rank to player
			List<Column> allCols = getPlayerDatas(pl, track);

			// Check if player is found
			if (allCols == null || allCols.isEmpty()) {
				Component noPlayer = Helper.deserializeString(lang.getProperty("database.playernotfound")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
				);

				Logger.log(noPlayer, sender);

				return;
			}

			Column colObtainedranks = Helper.findColumn(allCols, "obtainedranks");
			assert colObtainedranks != null;

			List<String> temp = new ArrayList<>(Arrays.asList(colObtainedranks.getValue().toString().split(",")));

			String rankPrefix = getGroupPrefix(rank);

			if (temp.contains(rank)) {
				Component playermsg = Helper.deserializeString(lang.getProperty("rank.add.exists")
						.replace("<rank>", rankPrefix)
				);

				Component consolemsg = Helper.deserializeString(lang.getProperty("rank.add.exists.console")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
						.replace("<rank>", rankPrefix)
				);

				if (pl.isOnline()) { Logger.sendToPlayer(pl.getPlayer(), playermsg); }
				Logger.log(consolemsg, sender);

				return;
			}

			// remove default empty string
			temp.remove("");
			temp.add(rank);

			colObtainedranks.setValue(String.join(",", temp));

			List<Column> out = new ArrayList<Column>() {{
				add(colObtainedranks);
			}};

			boolean res = updatePlayerData(track, pl, out);

			if (!res) {
				Component error = Helper.deserializeString(lang.getProperty("rank.add.failed")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
						.replace("<rank>", rankPrefix)
				);
				Logger.log(error, sender);
				return;
			}

			Component consolemsg = Helper.deserializeString(lang.getProperty("rank.add.console")
					.replace("<player>", pl.getName())
					.replace("<track>", track)
					.replace("<rank>", rankPrefix)
			);

			Component playermsg = Helper.deserializeString(lang.getProperty("rank.add")
					.replace("<rank>", rankPrefix)
			);

			if (pl.isOnline()) { Logger.sendToPlayer(pl.getPlayer(), playermsg); }
			Logger.log(consolemsg, sender);

		}

		@Command(value = "give")
		@Permission("cosmeticranks.rank.add")
		public void giveRank(CommandSender sender, @Suggestion("allplayers") String player, @Suggestion("lptracks") String track, @Suggestion("ranks") String rank) {
			addRank(sender, player, track, rank);
		}

		@Command(value = "remove")
		@Permission("cosmeticranks.rank.remove")
		public void removeRank(CommandSender sender, @Suggestion("allplayers") String player, @Suggestion("lptracks") String track, @Suggestion("obtainedranks") String rank) {
			OfflinePlayer pl = getPlayer(sender, player);

			if (pl == null || pl.getName() == null) return; // frick you for providing unknown player

			// check if the track is in config
			if (!isTrackInConfig(sender,track)) {
				return;
			}

			// Remove rank from player
			List<Column> allData = getPlayerDatas(pl, track);

			// Check if player is found
			if (allData == null || allData.isEmpty()) {
				Component noPlayer = Helper.deserializeString(lang.getProperty("database.playernotfound")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
				);

				Logger.log(noPlayer, sender);

				return;
			}

			Column colObtainedranks = Helper.findColumn(allData, "obtainedranks");
			assert colObtainedranks != null;

			List<String> temp = new ArrayList<>(Arrays.asList(colObtainedranks.getValue().toString().split(",")));

			String rankPrefix = getGroupPrefix(rank);

			if (!temp.contains(rank)) {
				Component consolemsg = Helper.deserializeString(lang.getProperty("rank.remove.doesntexist")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
						.replace("<rank>", rankPrefix)
				);

				Logger.log(consolemsg, sender);
				return;
			}

			// remove the rank
			temp.remove(rank);

			// add default empty string to prevent problems
			if (temp.isEmpty()) {
				temp.add("");
			}

			colObtainedranks.setValue(String.join(",", temp));

			List<Column> out = new ArrayList<Column>() {{
				add(colObtainedranks);
			}};

			boolean res = updatePlayerData(track, pl, out);

			if (!res) {
				Component error = Helper.deserializeString(lang.getProperty("rank.remove.failed")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
						.replace("<rank>", rankPrefix)
				);
				Logger.log(error, sender);
				return;
			}

			Column setRank = Helper.findColumn(allData, "selectedrank");
			assert setRank != null;

			if (setRank.getValue().toString().equalsIgnoreCase(rank)) {
				setRank.setValue("");
				out.clear();
				out.add(setRank);
				updatePlayerData(track, pl, out);
			}

			Component playermsg = Helper.deserializeString(lang.getProperty("rank.remove")
					.replace("<rank>", rankPrefix)
			);

			Component consolemsg = Helper.deserializeString(lang.getProperty("rank.remove.console")
					.replace("<player>", pl.getName())
					.replace("<track>", track)
					.replace("<rank>", rankPrefix)
			);

			if (pl.isOnline()) { Logger.sendToPlayer(pl.getPlayer(), playermsg); }
			Logger.log(consolemsg, sender);
		}

		@Command("set")
		@Permission("cosmeticranks.rank.set")
		public class RankSetCommand {
			@Command(value = "self")
			@Permission("cosmeticranks.rank.set")
			public void setRank(Player sender, @Suggestion("lptracks") String track, @Suggestion("obtainedranks") String rank) {
				// check if the track is in config
				if (!isTrackInConfig(sender,track)) {
					return;
				}

				String rankPrefix = getGroupPrefix(rank);

				Logger.log(Component.text("Set rank for self"), sender);
				Logger.log(Component.text("Track : " + track), sender);
				Logger.log(Component.text("Rank : " + rankPrefix), sender);
			}

			@Command(value = "other")
			@Permission("cosmeticranks.rank.set.other")
			public void setRankOther(CommandSender sender, @Suggestion("allplayers") String player, @Suggestion("lptracks") String track, @Suggestion("obtainedranks") String rank) {
				OfflinePlayer pl = getPlayer(sender, player);

				if (pl == null || pl.getName() == null) return; // frick you for providing unknown player

				// check if the track is in config
				if (!isTrackInConfig(sender,track)) {
					return;
				}

				String rankPrefix = getGroupPrefix(rank);


				Logger.log(Component.text("Set rank for other player"), sender);
				Logger.log(Component.text("Player : " + pl.getName()), sender);
				Logger.log(Component.text("Track : " + track), sender);
				Logger.log(Component.text("Rank : " + rankPrefix), sender);
			}
		}


		@Command("clear")
		@Permission("cosmeticranks.rank.clear")
		public void clearRank(CommandSender sender, @Suggestion("lptracks") String track, @Optional @Suggestion("allplayers") String player) {
			OfflinePlayer pl;

			if (player == null) {
				if (sender instanceof Player) {
					pl = (Player) sender;
					if (pl.getName() == null) return; // frick you for providing unknown player
				}
				else {
					Logger.log(Helper.deserializeString(lang.getProperty("rank.clear.needplayer")), sender);
					return;
				}
			}
			else {
				pl = getPlayer(sender, player);
				if (pl == null || pl.getName() == null) return; // frick you for providing unknown player
			}

			// check if the track is in config
			if (!isTrackInConfig(sender,track)) {
				return;
			}

			// Clear the current set rank
			List<Column> allData = getPlayerDatas(pl, track);

			// Check if player is found
			if (allData == null || allData.isEmpty()) {
				Component noPlayer = Helper.deserializeString(lang.getProperty("database.playernotfound")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
				);

				Logger.log(noPlayer, sender);

				return;
			}

			Column selRank = Helper.findColumn(allData, "selectedrank");
			assert selRank != null;

			String newSel = "";
			// linked hash map add based on rank weight...then clear based on perms rank
			List<String> allPermRanks = new ArrayList<>(instance.getMainConfig().getLptracks().get(track).getPermanentranks());
			if (!allPermRanks.isEmpty()) {
				Column obtainedRanks = Helper.findColumn(allData, "obtainedranks");
				assert obtainedRanks != null;
				for (int i = allPermRanks.size()-1; i>=0; i--) {
					String temp = allPermRanks.get(i);

					if (obtainedRanks.getValue().toString().contains(temp)) {
						newSel = temp;
						break;
					}
				}
			}

			if (selRank.getValue().toString().isEmpty() && newSel.isEmpty()) {
				Logger.log(Component.text("The selected rank is already empty"), Logger.LogTypes.debug);
				return;
			}

			selRank.setValue(newSel);
			List<Column> out = new ArrayList<Column>() {{
				add(selRank);
			}};
			boolean res = updatePlayerData(track, pl, out);

			if (!res) {
				Component error = Helper.deserializeString(lang.getProperty("rank.clear.failed")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
				);
				Logger.log(error, sender);
				return;
			}

			Component playermsg = Helper.deserializeString(lang.getProperty("rank.clear"));

			Component consolemsg = Helper.deserializeString(lang.getProperty("rank.clear.console")
					.replace("<player>", pl.getName())
					.replace("<track>", track)
			);

			if (pl.isOnline()) { Logger.sendToPlayer(pl.getPlayer(), playermsg); }
			Logger.log(consolemsg, sender);

		}


	}

}
