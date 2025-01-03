// This file is part of CosmeticRanks, created on 14/04/2024 (18:43 PM)
// Name : MainCommand
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.*;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import io.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import io.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.ranks.RankManager;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.*;

@Command(value = "cosmeticranks", alias = {"cr", "cranks"})
@Permission("cosmeticranks.use")
public class MainCommand{
	private final CosmeticRanks instance;
	private final LuckPerms lp;
	private final BukkitAudiences audiences;
	private final DatabaseManager dbm;
	private Properties lang() {
		return CosmeticRanks.getInstance().getLanguageFile();
	}
	private final RankManager rankManager;

	public MainCommand() {
		this.instance = CosmeticRanks.getInstance();
		this.dbm = instance.getDBM();
		this.lp = this.instance.getLuckPerms();
		this.audiences = CosmeticRanks.getInstance().adventure();
		this.rankManager = instance.getRankManager();
	}

	// -------------------------------------------------------------------
	// --------------------[ PLUGIN SECTION ]-----------------------------
	// -------------------------------------------------------------------

	@Command
	public void maincmdExecutor(CommandSender sender) {
		info(sender);
	}

	@Command(value = "info")
	@Permission("info")
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

	@Command(value = "reload")
	@Permission("reload")
	public void reload(CommandSender commandSender){
		Component msg = Component.text(instance.getLanguageFile().getProperty("plugin.reload"));
		Component success = Component.text(instance.getLanguageFile().getProperty("plugin.reload.success"));
		Component failure = Component.text(instance.getLanguageFile().getProperty("plugin.reload.failed"));

		Logger.sendToBoth(msg, commandSender);

		// reload
		boolean res = instance.reloadPlugin();

		if (res) Logger.sendToBoth(success, commandSender);
		else Logger.sendToBoth(failure, commandSender);
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

		Component error = Helper.deserializeString(lang().getProperty("player.notfound")
				.replace("<player>", player)
		);
		Logger.sendToBoth(error, sender);

		return  null;

	}

	public boolean isTrackInConfig(CommandSender sender, String track) {
		boolean res = instance.getMainConfig().getLptracks().containsKey(track);

		if (!res) {
			Component error = Helper.deserializeString(lang().getProperty("track.notinconfig")
					.replace("<track>", track)
			);
			Logger.sendToBoth(error, sender);
		}

		return res;
	}

	@Command(value = "rank")
	@Permission("rank")
	public class RankCommand {
		@Command(value = "add")
		@Permission("add")
		public void addRank(CommandSender sender, @Suggestion("allplayers") String player, @Suggestion("lptracks") String track, @Suggestion("ranks") String rank) {
			OfflinePlayer pl = getPlayer(sender, player);

			if (pl == null || pl.getName() == null) return; // frick you for providing unknown player

			if (!isTrackInConfig(sender,track)) {
				return;
			}

			// check if track or rank exists
			if (lp.getTrackManager().getTrack(track) == null) {
				Component errorNoTrack = Helper.deserializeString(lang().getProperty("rank.add.notrack")
						.replace("<track>", track)
				);
				Logger.sendToBoth(errorNoTrack, sender);
				return;
			}
			else if (!Objects.requireNonNull(lp.getTrackManager().getTrack(track)).getGroups().contains(rank)) {
					Component errorNoRank = Helper.deserializeString(lang().getProperty("rank.add.norank")
							.replace("<track>", track)
							.replace("<rank>", rank)
					);
					Logger.sendToBoth(errorNoRank, sender);
					return;
			}

			// Add rank to player
			String tablename = instance.getRankManager().getRanksTable().get(track).getName();
			List<Column> allCols = Helper.getPlayerDatas(pl, tablename);

			// Check if player is found
			if (allCols == null || allCols.isEmpty()) {
				Component noPlayer = Helper.deserializeString(lang().getProperty("database.playernotfound")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
				);
				Logger.sendToBoth(noPlayer, sender);
				return;
			}

			Column colObtainedranks = Helper.findColumn(allCols, "obtainedranks");
			assert colObtainedranks != null;

			List<String> temp = new ArrayList<>(Arrays.asList(colObtainedranks.getValue().toString().split(",")));

			String rankPrefix = Helper.getGroupPrefix(rank);

			if (temp.contains(rank)) {
				String pp = Helper.parsePlaceholders(pl, lang().getProperty("rank.add.exists").replace("<rank>", rankPrefix));
				Component playermsg = Helper.deserializeString(pp);

				String cc = lang().getProperty("rank.add.exists.console")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
						.replace("<rank>", rankPrefix);
				cc = Helper.parsePlaceholders(pl, cc);
				Component consolemsg = Helper.deserializeString(cc);

				if (pl.isOnline()) { Logger.sendToPlayer(pl.getPlayer(), playermsg); }
				Logger.sendToBoth(consolemsg, sender);

				return;
			}

			// remove default empty string
			temp.remove("");
			temp.add(rank);

			colObtainedranks.setValue(String.join(",", temp));

			List<Column> out = new ArrayList<Column>() {{
				add(colObtainedranks);
			}};

			boolean res = updatePlayerData(tablename, pl, out);

			if (!res) {
				String rAddFail = lang().getProperty("rank.add.failed")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
						.replace("<rank>", rankPrefix);
				rAddFail = Helper.parsePlaceholders(pl, rAddFail);
				Component error = Helper.deserializeString(rAddFail);
				Logger.sendToBoth(error, sender);
				return;
			}

			// after success update in cache
			rankManager.updatePlayerData(pl.getUniqueId(), track, allCols);

			// also add group permission node
			Group group = lp.getGroupManager().getGroup(rank);
			if (group != null) {
				InheritanceNode node = InheritanceNode.builder(rank).build();
				lp.getUserManager().modifyUser(pl.getUniqueId(), user -> user.data().add(node));
			}

			String rAddS = lang().getProperty("rank.add.console")
					.replace("<player>", pl.getName())
					.replace("<track>", track)
					.replace("<rank>", rankPrefix);
			rAddS = Helper.parsePlaceholders(pl, rAddS);
			Component consolemsg = Helper.deserializeString(rAddS);

			String tt1 = lang().getProperty("rank.add")
					.replace("<rank>", rankPrefix);
			tt1 = Helper.parsePlaceholders(pl, tt1);
			Component playermsg = Helper.deserializeString(tt1);

			if (pl.isOnline()) { Logger.sendToPlayer(pl.getPlayer(), playermsg); }
			Logger.sendToBoth(consolemsg, sender);

		}

		@Command(value = "give")
		@Permission("add")
		public void giveRank(CommandSender sender, @Suggestion("allplayers") String player, @Suggestion("lptracks") String track, @Suggestion("ranks") String rank) {
			addRank(sender, player, track, rank);
		}

		@Command(value = "remove")
		@Permission("remove")
		public void removeRank(CommandSender sender, @Suggestion("allplayers") String player, @Suggestion("lptracks") String track, @Suggestion("obtainedranks") String rank) {
			OfflinePlayer pl = getPlayer(sender, player);

			if (pl == null || pl.getName() == null) return; // frick you for providing unknown player

			// check if the track is in config
			if (!isTrackInConfig(sender,track)) {
				return;
			}

			// Remove rank from player
			String tablename = instance.getRankManager().getRanksTable().get(track).getName();
			List<Column> allData = Helper.getPlayerDatas(pl, tablename);

			// Check if player is found
			if (allData == null || allData.isEmpty()) {
				Component noPlayer = Helper.deserializeString(lang().getProperty("database.playernotfound")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
				);

				Logger.sendToBoth(noPlayer, sender);

				return;
			}

			Column colObtainedranks = Helper.findColumn(allData, "obtainedranks");
			assert colObtainedranks != null;

			List<String> temp = new ArrayList<>(Arrays.asList(colObtainedranks.getValue().toString().split(",")));

			String rankPrefix = Helper.getGroupPrefix(rank);

			if (!temp.contains(rank)) {
				String tmp1 = lang().getProperty("rank.remove.doesntexist")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
						.replace("<rank>", rankPrefix);
				tmp1 = Helper.parsePlaceholders(pl, tmp1);
				Component consolemsg = Helper.deserializeString(tmp1);

				Logger.sendToBoth(consolemsg, sender);
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

			boolean res = updatePlayerData(tablename, pl, out);

			if (!res) {
				String tmp2 = lang().getProperty("rank.remove.failed")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
						.replace("<rank>", rankPrefix);
				tmp2 = Helper.parsePlaceholders(pl,tmp2);
				Component error = Helper.deserializeString(tmp2);
				Logger.sendToBoth(error, sender);
				return;
			}

			Column selRank = Helper.findColumn(allData, "selectedrank");
			assert selRank != null;

			if (selRank.getValue().toString().equalsIgnoreCase(rank)) {
				selRank.setValue("");
				out.clear();
				out.add(selRank);
				updatePlayerData(tablename, pl, out);
			}

			// after success update in cache
			rankManager.updatePlayerData(pl.getUniqueId(), track, allData);

			// also add group permission node
			Group group = lp.getGroupManager().getGroup(rank);
			if (group != null) {
				InheritanceNode node = InheritanceNode.builder(rank).build();
				lp.getUserManager().modifyUser(pl.getUniqueId(), user -> user.data().remove(node));
			}

			String tmp3 = lang().getProperty("rank.remove")
					.replace("<rank>", rankPrefix);
			tmp3 = Helper.parsePlaceholders(pl, tmp3);
			Component playermsg = Helper.deserializeString(tmp3);

			String tmp4 = lang().getProperty("rank.remove.console")
					.replace("<player>", pl.getName())
					.replace("<track>", track)
					.replace("<rank>", rankPrefix);
			tmp4 = Helper.parsePlaceholders(pl, tmp4);
			Component consolemsg = Helper.deserializeString(tmp4);

			if (pl.isOnline()) { Logger.sendToPlayer(pl.getPlayer(), playermsg); }
			Logger.sendToBoth(consolemsg, sender);
		}

		@Command(value = "set")
		@Permission("set")
		public class RankSetCommand {
			@Command(value = "self")
			@Permission("self")
			public void setRank(Player sender, @Suggestion("lptracks") String track, @Suggestion("obtainedranks") String rank) {
				// check if the track is in config
				if (!isTrackInConfig(sender,track)) {
					return;
				}

				String tablename = instance.getRankManager().getRanksTable().get(track).getName();
				List<Column> allData = Helper.getPlayerDatas(sender, tablename);

				// Check if player is found
				if (allData == null || allData.isEmpty()) {
					Component noPlayer = Helper.deserializeString(lang().getProperty("database.playernotfound")
							.replace("<player>", sender.getName())
							.replace("<track>", track)
					);

					Logger.sendToBoth(noPlayer, sender);

					return;
				}

				Column colObtainedranks = Helper.findColumn(allData, "obtainedranks");
				assert colObtainedranks != null;

				List<String> temp = new ArrayList<>(Arrays.asList(colObtainedranks.getValue().toString().split(",")));

				String rankPrefix = Helper.getGroupPrefix(rank);

				if (!temp.contains(rank)) {
					String tmp1 = lang().getProperty("rank.set.doesntexist")
							.replace("<player>", sender.getName())
							.replace("<track>", track)
							.replace("<rank>", rankPrefix);
					tmp1 = Helper.parsePlaceholders(sender, tmp1);
					Component consolemsg = Helper.deserializeString(tmp1);

					audiences.console().sendMessage(consolemsg);
					return;
				}

				Column selRank = Helper.findColumn(allData, "selectedrank");
				assert selRank != null;

				selRank.setValue(rank);

				List<Column> out = new ArrayList<Column>() {{
					add(selRank);
				}};

				boolean res = updatePlayerData(tablename, sender, out);

				if (!res) {
					String tmp2 = lang().getProperty("rank.set.failed")
							.replace("<player>", sender.getName())
							.replace("<track>", track)
							.replace("<rank>", rankPrefix);
					tmp2 = Helper.parsePlaceholders(sender, tmp2);
					Component error = Helper.deserializeString(tmp2);
					Logger.sendToBoth(error, sender);
					return;
				}

				// after success update in cache
				rankManager.updatePlayerData(sender.getUniqueId(), track, allData);

				String tmp3 = lang().getProperty("rank.set")
						.replace("<rank>", rankPrefix);
				tmp3 = Helper.parsePlaceholders(sender, tmp3);
				Component playermsg = Helper.deserializeString(tmp3);

				String tmp4 = lang().getProperty("rank.set.console")
						.replace("<player>", sender.getName())
						.replace("<track>", track)
						.replace("<rank>", rankPrefix);
				tmp4 = Helper.parsePlaceholders(sender, tmp4);
				Component consolemsg = Helper.deserializeString(tmp4);

				if (sender.isOnline()) { Logger.sendToPlayer(sender.getPlayer(), playermsg); }
				Logger.log(consolemsg, Logger.LogTypes.log);

			}

			@Command(value = "other")
			@Permission("other")
			public void setRankOther(CommandSender sender, @Suggestion("allplayers") String player, @Suggestion("lptracks") String track, @Suggestion("obtainedranks") String rank) {
				OfflinePlayer pl = getPlayer(sender, player);

				if (pl == null || pl.getName() == null) return; // frick you for providing unknown player

				// check if the track is in config
				if (!isTrackInConfig(sender,track)) {
					return;
				}

				String tablename = instance.getRankManager().getRanksTable().get(track).getName();
				List<Column> allData = Helper.getPlayerDatas(pl, tablename);

				// Check if player is found
				if (allData == null || allData.isEmpty()) {
					Component noPlayer = Helper.deserializeString(lang().getProperty("database.playernotfound")
							.replace("<player>", sender.getName())
							.replace("<track>", track)
					);

					Logger.sendToBoth(noPlayer, sender);

					return;
				}

				Column colObtainedranks = Helper.findColumn(allData, "obtainedranks");
				assert colObtainedranks != null;

				List<String> temp = new ArrayList<>(Arrays.asList(colObtainedranks.getValue().toString().split(",")));

				String rankPrefix = Helper.getGroupPrefix(rank);

				if (!temp.contains(rank)) {
					String tmp1 = lang().getProperty("rank.set.other.doesntexist")
							.replace("<player>", pl.getName())
							.replace("<track>", track)
							.replace("<rank>", rankPrefix);
					tmp1 = Helper.parsePlaceholders(pl, tmp1);
					Component consolemsg = Helper.deserializeString(tmp1);

					Logger.sendToBoth(consolemsg, sender);
					return;
				}

				Column selRank = Helper.findColumn(allData, "selectedrank");
				assert selRank != null;

				selRank.setValue(rank);

				List<Column> out = new ArrayList<Column>() {{
					add(selRank);
				}};

				boolean res = updatePlayerData(tablename, pl, out);

				if (!res) {
					String tmp2 = lang().getProperty("rank.set.other.failed")
							.replace("<player>", sender.getName())
							.replace("<track>", track)
							.replace("<rank>", rankPrefix);
					tmp2 = Helper.parsePlaceholders(pl, tmp2);
					Component error = Helper.deserializeString(tmp2);
					Logger.sendToBoth(error, sender);
					return;
				}

				// after success update in cache
				rankManager.updatePlayerData(pl.getUniqueId(), track, allData);

				String tmp3 = lang().getProperty("rank.set.other")
						.replace("<player>", pl.getName())
						.replace("<rank>", rankPrefix);
				tmp3 = Helper.parsePlaceholders(pl, tmp3);
				Component playermsg = Helper.deserializeString(tmp3);

				String tmp4 = lang().getProperty("rank.set.other.console")
						.replace("<sender>", sender.getName())
						.replace("<player>", pl.getName())
						.replace("<track>", track)
						.replace("<rank>", rankPrefix);
				tmp4 = Helper.parsePlaceholders(pl, tmp4);
				Component consolemsg = Helper.deserializeString(tmp4);

				if (pl.isOnline()) { Logger.sendToPlayer(pl.getPlayer(), playermsg); }
				Logger.sendToBoth(consolemsg, sender);
			}
		}

		@Command(value = "clear")
		@Permission("clear")
		public void clearRank(CommandSender sender, @Suggestion("lptracks") String track, @Optional @Suggestion("allplayers") String player) {
			OfflinePlayer pl;

			if (player == null) {
				if (sender instanceof Player) {
					pl = (Player) sender;
					if (pl.getName() == null) return; // frick you for providing unknown player
				}
				else {
					Logger.sendToBoth(Helper.deserializeString(lang().getProperty("rank.clear.needplayer")), sender);
					return;
				}
			}
			else {
				if (sender instanceof Player && !sender.hasPermission("cosmeticranks.rank.clear.other")) {
					audiences.sender(sender).sendMessage(Component.text("You don't have permission to clear other player's rank!!").color(NamedTextColor.DARK_RED));
					return;
				}

				pl = getPlayer(sender, player);
				if (pl == null || pl.getName() == null) return; // frick you for providing unknown player
			}

			// check if the track is in config
			if (!isTrackInConfig(sender,track)) {
				return;
			}

			// Clear the current set rank
			String tablename = instance.getRankManager().getRanksTable().get(track).getName();
			List<Column> allData = Helper.getPlayerDatas(pl, tablename);

			// Check if player is found
			if (allData == null || allData.isEmpty()) {
				Component noPlayer = Helper.deserializeString(lang().getProperty("database.playernotfound")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
				);

				Logger.sendToBoth(noPlayer, sender);

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
			boolean res = updatePlayerData(tablename, pl, out);

			if (!res) {
				Component error = Helper.deserializeString(lang().getProperty("rank.clear.failed")
						.replace("<player>", pl.getName())
						.replace("<track>", track)
				);
				Logger.sendToBoth(error, sender);
				return;
			}

			// after success update in cache
			rankManager.updatePlayerData(pl.getUniqueId(), track, allData);

			Component playermsg = Helper.deserializeString(lang().getProperty("rank.clear"));

			Component consolemsg = Helper.deserializeString(lang().getProperty("rank.clear.console")
					.replace("<player>", pl.getName())
					.replace("<track>", track)
			);

			if (pl.isOnline()) { Logger.sendToPlayer(pl.getPlayer(), playermsg); }
			Logger.sendToBoth(consolemsg, sender);

		}


	}

}
