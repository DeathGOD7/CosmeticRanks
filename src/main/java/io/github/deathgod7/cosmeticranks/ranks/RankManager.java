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
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class RankManager {
	CosmeticRanks instance;
	LuckPerms luckPermsApi;

	LinkedHashMap<String, Table> ranksTable = new LinkedHashMap<>();
	public LinkedHashMap<String, Table> getRanksTable() {
		return ranksTable;
	}
	public RankManager(CosmeticRanks ins) {
		this.instance = ins;
		this.luckPermsApi = instance.getLuckPerms();
		this.createRanksTable();
		this.loadRanksTable();
	}

	public void createRanksTable() {
		LinkedHashMap<String, TrackConfig> lptracksHM = this.instance.getMainConfig().getLptracks();
		for (String k : lptracksHM.keySet())
		{
			System.out.println("Creating table for " + k);
			if (luckPermsApi.getTrackManager().getTrack(k) == null) {
				Logger.log(Component.text("[CREATE Track] Track " + k + " not found in LuckPerms").color(NamedTextColor.GOLD), Logger.LogTypes.debug);
				continue;
			}

			Column uuid = new Column("uuid", DataType.VARCHAR, 40);
			Column playername = new Column("playername", DataType.VARCHAR, 40);
			Column selectedrank = new Column("selectedrank", DataType.VARCHAR, 40);
			Column obtainedranks = new Column("obtainedranks", DataType.TEXT, 65535);

			List<Column> columns = Arrays.asList(playername, selectedrank, obtainedranks);

			Table newtable = new Table(k, uuid, columns);

			////////////////////////////////////

			// REMOVE THE PRINT IN SE7ENLIB  //

			///////////////////////////////////

			DatabaseManager dbm = this.instance.getDBM();
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
		DatabaseManager dbm = this.instance.getDBM();
		LinkedHashMap<String, TrackConfig> lptracksHM = this.instance.getMainConfig().getLptracks();
		for (String k : lptracksHM.keySet())
		{
			if (luckPermsApi.getTrackManager().getTrack(k) == null) {
				Logger.log(Component.text("[LOAD Track] Track " + k + " not found in LuckPerms").color(NamedTextColor.GOLD), Logger.LogTypes.debug);
				continue;
			}

			DatabaseType dbtype = dbm.getDbInfo().getDbType();
			if (dbtype == DatabaseType.MySQL) {
				ranksTable.put(k, dbm.getMySQL().getTables().get(k));
			} else if (dbtype == DatabaseType.SQLite) {
				ranksTable.put(k, dbm.getSQLite().getTables().get(k));
			} else if (dbtype == DatabaseType.MongoDB) {
				ranksTable.put(k, dbm.getMongoDB().getTables().get(k));
			}
		}
	}

	public void reloadRanksTable() {
		ranksTable.clear();
		this.loadRanksTable();
	}
}
