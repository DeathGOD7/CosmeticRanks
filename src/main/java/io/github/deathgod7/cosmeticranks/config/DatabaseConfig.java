// This file is part of CosmeticRanks, created on 26/10/2023 (16:40 PM)
// Name : MainConfig
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.config;

import io.github.deathgod7.SE7ENLib.database.PoolSettings;

public class DatabaseConfig {
	// Database Settings
	String type; //mysql and sqlite and mongodb
	String host;
	String username;
	String password;
	String dbname;

	String table_preifx;

	PoolSettings poolSettings;

	public DatabaseConfig() {
		this.type = "sqlite";
		this.host = "localhost";
		this.username = "root";
		this.password = "toor";
		this.dbname = "cosmeticranks";
		this.table_preifx = "cranks_";
		this.poolSettings = new PoolSettings();
	}

	public DatabaseConfig(String type, String host, String username, String password, String dbname, String table_preifx, PoolSettings poolSettings) {
		this.type = type;
		this.host = host;
		this.username = username;
		this.password = password;
		this.dbname = dbname;
		this.table_preifx = table_preifx;
		this.poolSettings = poolSettings;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getDbname() {
		return dbname;
	}

	public void setTablepreifx(String table_preifx) {
		this.table_preifx = table_preifx;
	}

	public String getTablepreifx() {
		return table_preifx;
	}

	public void setPoolSettings(PoolSettings poolSettings) {
		this.poolSettings = poolSettings;
	}

	public PoolSettings getPoolSettings() {
		return poolSettings;
	}



}







