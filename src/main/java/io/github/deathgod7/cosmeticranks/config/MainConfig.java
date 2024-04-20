// This file is part of CosmeticRanks, created on 26/10/2023 (16:40 PM)
// Name : MainConfig
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.config;

import io.github.deathgod7.cosmeticranks.CosmeticRanks;

import java.util.LinkedHashMap;

public class MainConfig {
	String pluginversion;
	boolean debug;
	String language;
	String prefix;
	LinkedHashMap<String, TrackConfig> lptracks;
	DatabaseConfig database;

	public MainConfig() {
		this.pluginversion = CosmeticRanks.getInstance().getDescription().getVersion();
		this.debug = false;
		this.language = "en_US";
		this.prefix = "&7[&6CosmeticRanks&7]";
		this.lptracks = new LinkedHashMap<>();
		this.database = new DatabaseConfig();
	}

	public MainConfig(String ver, boolean debug, String language, String prefix, LinkedHashMap<String, TrackConfig> lptracks, DatabaseConfig database) {
		this.pluginversion = ver;
		this.debug = debug;
		this.language = language;
		this.prefix = prefix;
		this.lptracks = lptracks;
		this.database = database;
	}

	public void setPluginversion(String pluginversion) {
		this.pluginversion = pluginversion;
	}

	public String getPluginversion() {
		return pluginversion;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public boolean getDebug() {
		return debug;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setLptracks(LinkedHashMap<String, TrackConfig> lptracks) {
		this.lptracks = lptracks;
	}

	public LinkedHashMap<String, TrackConfig> getLptracks() {
		return lptracks;
	}

	public void setDatabase(DatabaseConfig database) {
		this.database = database;
	}

	public DatabaseConfig getDatabase() {
		return database;
	}


}







