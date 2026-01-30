// This file is part of CosmeticRanks, created on 13/04/2024 (21:26 PM)
// Name : RankManager
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.ranks;

import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.component.Table;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.config.TrackConfig;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class RankManager {
	CosmeticRanks instance;
	LuckPerms luckPermsApi;

	DatabaseManager dbm;

	LinkedHashMap<String, Table> ranksTable = new LinkedHashMap<>();
	public LinkedHashMap<String, Table> getRanksTable() {
		return ranksTable;
	}

	LinkedHashMap<UUID, LinkedHashMap<String, List<Column>>> cachedPlayerData;
	public LinkedHashMap<String, List<Column>> getPlayerData(UUID uuid) {
		if (!cachedPlayerData.containsKey(uuid)) { return new LinkedHashMap<>(); }
		else { return cachedPlayerData.get(uuid); }
	}
	public void removePlayerData(UUID uuid) {
		cachedPlayerData.remove(uuid);
	}

	LinkedHashMap<UUID, LinkedHashMap<String, HashSet<String>>> cachedObtainedRanks;
	public LinkedHashMap<String, HashSet<String>> getObtainedRanks(UUID uuid) {
		if (!cachedObtainedRanks.containsKey(uuid)) { return new LinkedHashMap<>(); }
		else { return cachedObtainedRanks.get(uuid); }
	}
	public void removeObtainedRanks(UUID uuid) {
		cachedObtainedRanks.remove(uuid);
	}

	public RankManager(CosmeticRanks ins) {
		this.instance = ins;
		this.luckPermsApi = instance.getLuckPerms();
		this.dbm = instance.getDBM();
		this.createRanksTable();
		this.loadRanksTable();
		cachedPlayerData = new LinkedHashMap<>();
	 	cachedObtainedRanks = new LinkedHashMap<>();
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

			List<Column> columns = Arrays.asList(playername, selectedrank);

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
		this.createRanksTable();
		this.loadRanksTable();
	}

	public void loadPlayerData(@NotNull OfflinePlayer player, String tablename) {
		LinkedHashMap<String, List<Column>> playerData = getPlayerData(player.getUniqueId());
		// get datas of player of track 'x'
		List<Column> allCols = Helper.getPlayerDatas(player, ranksTable.get(tablename).getName());
		playerData.put(tablename, allCols);
		// store track 'x' data of player
		cachedPlayerData.put(player.getUniqueId(), playerData);

		// dynamic luckperms obtained ranks
		LinkedHashMap<String, HashSet<String>> obtainedRanks = getObtainedRanks(player.getUniqueId());
		// get obtained ranks in track 'x' of player
		List<Group> groups = Helper.getAllObtainedRanksOfTrack(player, tablename);
		obtainedRanks.put(tablename,
							groups.stream().map(Group::getName)
							.collect(Collectors.toCollection(HashSet::new))
		);
		// store obtained ranks in track 'x' of player
		cachedObtainedRanks.put(player.getUniqueId(), obtainedRanks);

	}


	public void updatePlayerCacheData(UUID uuid, String tablename, List<Column> data) {
		LinkedHashMap<String, List<Column>> playerData =  getPlayerData(uuid);
		playerData.put(tablename, data);
		cachedPlayerData.put(uuid, playerData);
	}

	public void updateObtainedRanksCacheData(UUID uuid, String tablename, HashSet<String> data) {
		LinkedHashMap<String, HashSet<String>> playerObtainedRanks = getObtainedRanks(uuid);
		playerObtainedRanks.put(tablename, data);
		cachedObtainedRanks.put(uuid, playerObtainedRanks);
	}

}
