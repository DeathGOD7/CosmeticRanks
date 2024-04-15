// This file is part of CosmeticRanks, created on 14/04/2024 (23:26 PM)
// Name : CommandsHandler
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.commands;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.component.Table;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.track.Track;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class CommandsHandler {
	CosmeticRanks instance;
	private final BukkitCommandManager<CommandSender> commandManager;

	private final LuckPerms luckPerms;
	public BukkitCommandManager<CommandSender> getCommandManager() {
		return commandManager;
	}

	public CommandsHandler() {
		this.instance = CosmeticRanks.getInstance();
		luckPerms = instance.getLuckPerms();
		commandManager = BukkitCommandManager.create(instance);
		registerSuggestions();
		registerCommands();
	}

	void registerSuggestions() {
		// Register suggestions here

		// For the lptracks
		commandManager.registerSuggestion(SuggestionKey.of("lptracks"), (sender, arguments) -> {
			return (List<String>) new ArrayList<String>(instance.getRankManager().getRanksTable().keySet());
		});

		// For the ranks (all)
		commandManager.registerSuggestion(SuggestionKey.of("ranks"), (sender, arguments) -> {
			Set<String> allTracks = instance.getRankManager().getRanksTable().keySet();
			String lastArg = arguments.get(1);

			if (allTracks.contains(lastArg)) {
				Track track = luckPerms.getTrackManager().getTrack(lastArg);
				assert track != null;
				return (List<String>) new ArrayList<String>(track.getGroups());

			}
			return (List<String>) new ArrayList<String>();
		});

		// For the ranks (obtained)
		commandManager.registerSuggestion(SuggestionKey.of("obtainedranks"), (sender, arguments) -> {
			Set<String> allTracks = instance.getRankManager().getRanksTable().keySet();
			String lastArg = arguments.get(1);

			if (allTracks.contains(lastArg)) {
				Table table = instance.getRankManager().getRanksTable().get(lastArg);
				Player p = Bukkit.getPlayer(arguments.get(0));
				List<Column> datas = getPlayerDatas(p, table);

				if (datas == null) { return (List<String>) new ArrayList<String>(); }
				Column colObtainedranks = Helper.findColumn(datas, "obtainedranks");

				if (colObtainedranks != null) {
					String[] obtainedRanks = colObtainedranks.getValue().toString().split(",");
					return (List<String>) Arrays.asList(obtainedRanks);
				}

				return (List<String>) new ArrayList<String>();
			}
			return (List<String>) new ArrayList<String>();
		});

	}


	List<Column> getPlayerDatas(Player player, Table table) {
		DatabaseManager dbm = this.instance.getDBM();
		DatabaseType dbtype = dbm.getDbInfo().getDbType();

		Column pk = table.getPrimaryKey();
		pk.setValue(player.getUniqueId().toString());

		if (dbtype == DatabaseType.MySQL) {
			return dbm.getMySQL().getExactData(table.getName(), pk);
		} else if (dbtype == DatabaseType.SQLite) {
			return dbm.getSQLite().getExactData(table.getName(), pk);
		} else if (dbtype == DatabaseType.MongoDB) {
			return dbm.getMongoDB().getExactData(table.getName(), pk);
		}
		else return null;
	}

	void registerCommands() {
		// Register commands here

		// main commands
		commandManager.registerCommand(new MainCommand());
	}
}
