// This file is part of CosmeticRanks, created on 26/10/2023 (16:40 PM)
// Name : MainConfig
// Author : Death GOD 7

package com.github.deathgod7.cosmeticranks.config;

import com.github.deathgod7.cosmeticranks.CosmeticRanks;
import redempt.redlib.config.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigMappable
public class MainConfig {
	public transient String pluginversion = CosmeticRanks.getPDFile().getVersion();

	@Comment("#############################################################################")
	@Comment("||                                                                         ||")
	@Comment("||                   Cosmetic Ranks - by Death GOD 7                       ||")
	@Comment("||                            - Elevate your player experience!            ||")
	@Comment("||                                                                         ||")
	@Comment("||               [ Github : https://github.com/DeathGOD7 ]                 ||")
	@Comment("||       [ Wiki : https://github.com/DeathGOD7/CosmeticRanks/wiki ]        ||")
	@Comment("||                                                                         ||")
	@Comment("#############################################################################")
	@Comment("")
	@Comment("Some settings of the plugin which might come handy for your server and for debugging plugin.")
	@Comment("version = The plugin version you are using")
	@Comment("previousversion = The plugin version that you updated from")
	@Comment("debug = This allows you to get additional plugin information. Really really helpful for debugging")
	@Comment("language = This will allow you to have custom language file")
	@Comment("prefix = The prefix used in logger and messages")

	private String settings;
	// Main Settings

	@ConfigName("settings.version")
	public String version = pluginversion;
	@ConfigName("settings.previousversion")
	public String previousversion = "";
	@ConfigName("settings.debug")
	public boolean debug = false;
	@ConfigName("settings.language")
	public String language = "en_US";
	@ConfigName("settings.prefix")
	public String prefix = "[CosmeticRanks]"; // cosmetic
	@ConfigName("settings.lptrack")
	public String lptrack = "cosmetic";
	@ConfigName("settings.permanentranks")
	public List<String> permanentranks = Arrays.asList("examplerank1", "examplerank2");

	@Comment("Database support for this plugin either for cross server (bungeecord / velocity or any proxy)")
	@Comment("or for save-storage / safe-keeping campaign. We go you covered fam!!")

	private String database;
	// Database Settings

	@Comment("You can choose database type for either of these two type")
	@Comment("type = mysql, sqlite (Default : sqlite)")
	@ConfigName("database.type")
	public String db_type = "sqlite"; //mysql and sqlite
	@Comment("NOTE : No need to touch any of this settings below if you are using sqlite database type in above settings")
	@Comment("Put your database host ip or custom domain address here along with port number if any (MySQL Default Port : 3306)")
	@ConfigName("database.host")
	public String db_host = "database.example.com";
	@Comment("Put your username that you use to access the database")
	@ConfigName("database.username")
	public String db_username = "root";
	@Comment("Put your password that you use to access the database. PS : It is top secret high security classified code right?")
	@ConfigName("database.password")
	public String db_password = "toor";
	@ConfigName("database.name")
	@Comment("Put the name of database that you want the data to be stored at")
	public String db_name = "yourdatabasename";



}







