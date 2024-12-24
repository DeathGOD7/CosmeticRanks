// This file is part of CosmeticRanks, created on 13/04/2024 (21:26 PM)
// Name : RankManager
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.ranks;

import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.component.Table;
import io.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import io.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import io.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.config.TrackConfig;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class RankManager {
	CosmeticRanks instance;
	LuckPerms luckPermsApi;

	DatabaseManager dbm;

	LinkedHashMap<String, Table> ranksTable = new LinkedHashMap<>();
	public LinkedHashMap<String, Table> getRanksTable() {
		return ranksTable;
	}

	LinkedHashMap<UUID, LinkedHashMap<String, List<Column>>> cachedPlayerData;
	public LinkedHashMap<UUID, LinkedHashMap<String, List<Column>>> getCachedPlayerData() {
		return cachedPlayerData;
	}

	public RankManager(CosmeticRanks ins) {
		this.instance = ins;
		this.luckPermsApi = instance.getLuckPerms();
		this.dbm = instance.getDBM();
		this.createRanksTable();
		this.loadRanksTable();
		this.loadPlayerData();

	}

	public void createRanksTable() {
		LinkedHashMap<String, TrackConfig> lptracksHM = this.instance.getMainConfig().getLptracks();
		for (String track : lptracksHM.keySet())
		{
			Logger.log(Component.text("Creating table for " + track + " track").color(NamedTextColor.GOLD), Logger.LogTypes.debug);
			if (luckPermsApi.getTrackManager().getTrack(track) == null) {
				Logger.log(Component.text("[CREATE Track] Track " + track + " not found in LuckPerms").color(NamedTextColor.GOLD), Logger.LogTypes.debug);
				continue;
			}

			Column uuid = new Column("uuid", DataType.VARCHAR, 40);
			Column playername = new Column("playername", DataType.VARCHAR, 40);
			Column selectedrank = new Column("selectedrank", DataType.VARCHAR, 40);
			Column obtainedranks = new Column("obtainedranks", DataType.TEXT, 65535);

			List<Column> columns = Arrays.asList(playername, selectedrank, obtainedranks);

			Table newtable = new Table(Helper.getTableName(track), uuid, columns);

			DatabaseType dbtype = dbm.getDbInfo().getDbType();
			if (dbtype == DatabaseType.MySQL) {
				dbm.getMySQL().createTable(newtable, dbtype);
			} else if (dbtype == DatabaseType.SQLite) {
				dbm.getSQLite().createTable(newtable, dbtype);
			} else if (dbtype == DatabaseType.MongoDB) {
				dbm.getMongoDB().createTable(newtable, dbtype);
			}
		}
	}

	public void loadRanksTable() {
		LinkedHashMap<String, TrackConfig> lptracksHM = this.instance.getMainConfig().getLptracks();
		for (String track : lptracksHM.keySet())
		{
			Logger.log(Component.text("Loading table of " + track + " track").color(NamedTextColor.GOLD), Logger.LogTypes.debug);
			if (luckPermsApi.getTrackManager().getTrack(track) == null) {
				Logger.log(Component.text("[LOAD Track] Track " + track + " not found in LuckPerms").color(NamedTextColor.GOLD), Logger.LogTypes.debug);
				continue;
			}

			DatabaseType dbtype = dbm.getDbInfo().getDbType();
			if (dbtype == DatabaseType.MySQL) {
				ranksTable.put(track, dbm.getMySQL().getTables().get(Helper.getTableName(track)));
			} else if (dbtype == DatabaseType.SQLite) {
				ranksTable.put(track, dbm.getSQLite().getTables().get(Helper.getTableName(track)));
			} else if (dbtype == DatabaseType.MongoDB) {
				ranksTable.put(track, dbm.getMongoDB().getTables().get(Helper.getTableName(track)));
			}
		}
	}

	public void reloadRanksTable() {
		ranksTable.clear();
		//cachedPlayerData.clear();
		this.loadRanksTable();
		//this.loadPlayerData();
	}
	public void loadPlayerData() {
		cachedPlayerData = new LinkedHashMap<>();

		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			LinkedHashMap<String, List<Column>> playerData = new LinkedHashMap<>();
			for (String track : ranksTable.keySet()) {
				List<Column> allCols = Helper.getPlayerDatas(player, ranksTable.get(track).getName());
				playerData.put(track, allCols);
			}
			cachedPlayerData.put(player.getUniqueId(), playerData);
		}
	}

	public void updatePlayerData(UUID uuid, String track, List<Column> data) {
		LinkedHashMap<String, List<Column>> playerData;
		if (!cachedPlayerData.containsKey(uuid)) {
			playerData = new LinkedHashMap<>();
		}else {
			playerData = cachedPlayerData.get(uuid);
		}
		playerData.put(track, data);
		cachedPlayerData.put(uuid, playerData);
	}

}
