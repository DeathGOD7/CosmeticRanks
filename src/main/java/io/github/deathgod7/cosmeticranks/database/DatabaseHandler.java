// This file is part of CosmeticRanks, created on 30/10/2023 (15:28 PM)
// Name : DatabaseHandler
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.database;

import io.github.deathgod7.SE7ENLib.Logger;
import io.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import io.github.deathgod7.SE7ENLib.database.PoolSettings;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.config.DatabaseConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseHandler {
	private final CosmeticRanks instance;

	private DatabaseManager _dbm;
	public DatabaseManager getDBM() {
		return _dbm;
	}
	public DatabaseHandler() {
		this.instance = CosmeticRanks.getInstance();
		this.ConnectDatabase();
	}

	public void ConnectDatabase() {
		DatabaseInfo dbInfo;
		DatabaseConfig dbConfig = instance.getMainConfig().getDatabase();
		String dbType = dbConfig.getType();
		PoolSettings poolSettings = dbConfig.getPoolSettings();

		if (dbType.equalsIgnoreCase("mysql")) {
			dbInfo = new DatabaseInfo(dbConfig.getDbname(), dbConfig.getHost(),
					dbConfig.getUsername(), dbConfig.getPassword(), DatabaseType.MySQL, poolSettings);
		}
		else if (dbType.equalsIgnoreCase("mongodb")) {
			dbInfo = new DatabaseInfo(dbConfig.getDbname(), dbConfig.getHost(),
					dbConfig.getUsername(), dbConfig.getPassword(), DatabaseType.MongoDB, null);
		}
		else {
			Path dbPath = Paths.get(instance.getDataFolder().getPath(), "db");
			if (Files.notExists(dbPath)) {
				try {
					Files.createDirectory(dbPath);
				} catch (IOException e) {
					Logger.log("[ERROR -> DB CON] " + e.getMessage());
				}
			}
			dbInfo = new DatabaseInfo("databse", dbPath.toString(), poolSettings);
		}

		this._dbm = new DatabaseManager(dbInfo);
		this._dbm.setDebugMode(instance.getMainConfig().getDebug());
	}


}
