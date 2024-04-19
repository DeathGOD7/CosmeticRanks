// This file is part of CosmeticRanks, created on 16/04/2024 (01:30 AM)
// Name : EventsHandler
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.events;

import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.component.Table;
import io.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import io.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import io.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.ranks.RankManager;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.List;

public class EventsHandler implements Listener {

	CosmeticRanks instance;
	private final RankManager rankManager;
	private final DatabaseManager dbm;
	public EventsHandler() {
		// Register events
		instance = CosmeticRanks.getInstance();
		rankManager = instance.getRankManager();
		dbm = instance.getDBM();
	}

	private boolean doesPlayerExist(Player player, Table table) {
		// Check if player exists in the database
		Column uuid = new Column("uuid", player.getUniqueId().toString(), DatabaseManager.DataType.VARCHAR);

		if (dbm.getDatabase() instanceof SQLite) {
			// SQLite
			List<Column> all = dbm.getSQLite().getExactData(table.getName(), uuid);
			return !all.isEmpty();
		}
		else if (dbm.getDatabase() instanceof MySQL) {
			// MySQL
			List<Column> all = dbm.getMySQL().getExactData(table.getName(), uuid);
			return !all.isEmpty();
		}
		else if (dbm.getDatabase() instanceof MongoDB) {
			// MongoDB
			List<Column> all = dbm.getMongoDB().getExactData(table.getName(), uuid);
			return !all.isEmpty();
		}
		else {
			Component temp = Component.text("[Player Check] Database not recognized.").color(NamedTextColor.RED);
			Logger.log(temp, Logger.LogTypes.debug);

			return false;
		}


	}

	private boolean addPlayer(Player player, Table table) {
		// Add player to the database
		Column uuid = new Column("uuid", player.getUniqueId().toString(), DatabaseManager.DataType.VARCHAR);
		Column playername = new Column("playername", player.getName(), DatabaseManager.DataType.VARCHAR);
		Column selectedrank = new Column("selectedrank", "", DatabaseManager.DataType.VARCHAR);
		Column obtainedranks = new Column("obtainedranks", "", DatabaseManager.DataType.VARCHAR);

		List<Column> columns = Arrays.asList(uuid, playername, selectedrank, obtainedranks);

		if (dbm.getDatabase() instanceof SQLite) {
			// SQLite
			return dbm.getSQLite().insertData(table.getName(), columns);
		}
		else if (dbm.getDatabase() instanceof MySQL) {
			// MySQL
			return dbm.getMySQL().insertData(table.getName(), columns);
		}
		else if (dbm.getDatabase() instanceof MongoDB) {
			// MongoDB
			return dbm.getMongoDB().insertData(table.getName(), columns);
		}
		else {
			Component temp = Component.text("[Player Add] Database not recognized.").color(NamedTextColor.RED);
			Logger.log(temp, Logger.LogTypes.debug);

			return false;
		}
	}

	private boolean removePlayer(Player player, Table table) {
		// Remove player from the database
		Column uuid = new Column("uuid", player.getUniqueId().toString(), DatabaseManager.DataType.VARCHAR);

		if (dbm.getDatabase() instanceof SQLite) {
			// SQLite
			return dbm.getSQLite().deleteData(table.getName(), uuid);
		}
		else if (dbm.getDatabase() instanceof MySQL) {
			// MySQL
			return dbm.getMySQL().deleteData(table.getName(), uuid);
		}
		else if (dbm.getDatabase() instanceof MongoDB) {
			// MongoDB
			return dbm.getMongoDB().deleteData(table.getName(), uuid);
		}
		else {
			Component temp = Component.text("[Player Remove] Database not recognized.").color(NamedTextColor.RED);
			Logger.log(temp, Logger.LogTypes.debug);

			return false;
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		// Player join event
		Player p = e.getPlayer();

		for(String s : rankManager.getRanksTable().keySet()) {
			// Check if player is in the database
			Table table = rankManager.getRanksTable().get(s);
			if (!doesPlayerExist(p, table)) {
				// Add player to the database
				boolean x = addPlayer(p, table);
				Component temp;
				if (x) {
					temp = Component.text("[Player Join] Player " + p.getName() + " added to the database table (" + table.getName() + ")").color(NamedTextColor.GREEN);
				}
				else {
					temp = Component.text("[Player Join] Player " + p.getName() + " could not be added to the database table (" + table.getName() + ")").color(NamedTextColor.RED);
				}
				Logger.log(temp, Logger.LogTypes.debug);
			}
			else {
				// Player already exists
				Component temp = Component.text("[Player Join] Player " + p.getName() + " already exists in the database table (" + table.getName() + ")").color(NamedTextColor.GRAY);
				Logger.log(temp, Logger.LogTypes.debug);
			}

			if (!p.hasPlayedBefore()) {
				List<Column> newdata = Helper.getPlayerDatas(p, s);
				instance.getRankManager().updatePlayerData(p.getUniqueId(), s, newdata);
			}

		}


	}

	@EventHandler
	public void onPlayerBanned(PlayerQuitEvent e) {
		// Player banned event
		boolean isBanned = e.getPlayer().isBanned();
		if (isBanned) {
			// Remove player from the database
			Player p = e.getPlayer();

			for(String s : rankManager.getRanksTable().keySet()) {
				// Check if player is in the database
				Table table = rankManager.getRanksTable().get(s);
				if (doesPlayerExist(p, table)) {
					// Remove player from the database
					boolean x = removePlayer(p, table);
					Component temp;
					if (x) {
						temp = Component.text("[Player Banned] Player " + p.getName() + " removed from the database table (" + table.getName() + ")").color(NamedTextColor.GREEN);
					}
					else {
						temp = Component.text("[Player Banned] Player " + p.getName() + " could not be removed from the database table (" + table.getName() + ")").color(NamedTextColor.RED);
					}
					Logger.log(temp, Logger.LogTypes.debug);
				}
				else {
					// Player does not exist
					Component temp = Component.text("[Player Banned] Player " + p.getName() + " does not exist in the database table (" + table.getName() + ")").color(NamedTextColor.GRAY);
					Logger.log(temp, Logger.LogTypes.debug);
				}
			}
		}
	}

}
