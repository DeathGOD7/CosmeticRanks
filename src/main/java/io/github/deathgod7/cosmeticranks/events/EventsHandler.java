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
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

	private boolean doesPlayerExist(Player player, String rank) {
		// Check if player exists in the database
		UUID uuid = player.getUniqueId();

		RankManager rmg = instance.getRankManager();

		if (!rmg.getCachedPlayerData().get(uuid).get(rank).isEmpty()) {
			Component temp = Component.text(String.format("[Player Check] Player '%s' found in database. (%s)", player.getName(), rank)).color(NamedTextColor.GREEN);
			Logger.log(temp, Logger.LogTypes.debug);
			return true;
		}
		else {
			Component temp = Component.text(String.format("[Player Check] Player '%s' not found in database. (%s)", player.getName(), rank)).color(NamedTextColor.RED);
			Logger.log(temp, Logger.LogTypes.debug);
			return false;
		}


	}

	private List<Column> addPlayer(Player player, Table table) {
		// Add player to the database
		Column uuid = new Column("uuid", player.getUniqueId().toString(), DatabaseManager.DataType.VARCHAR);
		Column playername = new Column("playername", player.getName(), DatabaseManager.DataType.VARCHAR);
		Column selectedrank = new Column("selectedrank", "", DatabaseManager.DataType.VARCHAR);
		Column obtainedranks = new Column("obtainedranks", "", DatabaseManager.DataType.VARCHAR);

		List<Column> columns = Arrays.asList(uuid, playername, selectedrank, obtainedranks);

		boolean res = false;
		if (dbm.getDatabase() instanceof SQLite) {
			// SQLite
			res = dbm.getSQLite().insertData(table.getName(), columns);
		}
		else if (dbm.getDatabase() instanceof MySQL) {
			// MySQL
			res = dbm.getMySQL().insertData(table.getName(), columns);
		}
		else if (dbm.getDatabase() instanceof MongoDB) {
			// MongoDB
			res = dbm.getMongoDB().insertData(table.getName(), columns);
		}
		else {
			Component temp = Component.text("[Player Add] Database not recognized.").color(NamedTextColor.RED);
			Logger.log(temp, Logger.LogTypes.debug);

			return new ArrayList<>();
		}

		if (res) return columns;
		else return new ArrayList<>();
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

		for(String rank : rankManager.getRanksTable().keySet()) {
			// Check if player is in the database
			Table table = rankManager.getRanksTable().get(rank);
			instance.getRankManager().loadPlayerData(p, rank);

			if (!doesPlayerExist(p, rank)) {
				// Add player to the database
				List<Column> x = addPlayer(p, table);
				Component temp;
				if (!x.isEmpty()) {
					temp = Component.text("[Player Join] Player " + p.getName() + " added to the database table (" + table.getName() + ")").color(NamedTextColor.GREEN);
					instance.getRankManager().updatePlayerData(p.getUniqueId(), rank, x);
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
		}
	}

	@EventHandler
	public void onPlayerBanned(PlayerQuitEvent e) {
		// Player banned event
		boolean isBanned = e.getPlayer().isBanned();
		Logger.log(Component.text("[Player Banned] Player : " + e.getPlayer().getName() + " || Ban Status : " + isBanned).color(NamedTextColor.RED)
					, Logger.LogTypes.debug);
		if (isBanned) {
			// Remove player from the database
			Player p = e.getPlayer();

			for(String rank : rankManager.getRanksTable().keySet()) {
				// Check if player is in the database
				Table table = rankManager.getRanksTable().get(rank);
				instance.getRankManager().loadPlayerData(p, rank);
				if (doesPlayerExist(p, rank)) {
					// Remove player from the database
					boolean x = removePlayer(p, table);
					Component temp;
					if (x) {
						temp = Component.text("[Player Banned] Player " + p.getName() + " removed from the database table (" + table.getName() + ")").color(NamedTextColor.GREEN);
						// remove temporary data
						instance.getRankManager().getCachedPlayerData().get(p.getUniqueId()).remove(rank);
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
			instance.getRankManager().getCachedPlayerData().remove(p.getUniqueId());
		}
	}

}
