// This file is part of CosmeticRanks, created on 01/11/2023 (03:33 AM)
// Name : ConfigHandler
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.config;

import com.amihaiemil.eoyaml.*;
import com.amihaiemil.eoyaml.extensions.MergedYamlMapping;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.SE7ENLib.database.PoolSettings;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.text.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class ConfigHandler {
	CosmeticRanks instance = CosmeticRanks.getInstance();
	File configFile;
	MainConfig config;

	public MainConfig getConfig() {
		return config;
	}

	public ConfigHandler(File file) {
		this.configFile = file;
		this.checkConfig();
		this.load();

	}

	public ConfigHandler(String filename) {
		this.configFile = Paths.get(instance.getDataFolder().getPath(), filename).toFile();
		this.checkConfig();
		this.load();
	}

	private void checkConfig() {
		if (!configFile.exists()) {
			this.saveDefaults(
					instance.getResource(configFile.getName())
					);
		}
	}

	public void updateVersion(String ver) {
		try {
			YamlMapping yamlMapping = Yaml.createYamlInput(configFile).readYamlMapping();

			String temp = yamlMapping.string("version");

			YamlMapping edited = new MergedYamlMapping(
					yamlMapping,
					() -> Yaml.createYamlMappingBuilder()
							.add("version", ver)
							.add("old-version", temp)
							.build(),
					true
			);

			final YamlPrinter printer = Yaml.createYamlPrinter(
					new FileWriter(Paths.get(instance.getDataFolder().getPath(), "new.yml").toFile())
			);
			printer.print(edited);
		}
		catch (IOException ex) {
			System.out.println("Error saving the config file.\n" + ex.getMessage());
		}
	}

	private void saveDefaults(InputStream inputStream) {
		Path configpath = configFile.toPath();
		if (Files.notExists(configpath)) {
			try {
				// create plugin dir
				Files.createDirectory(configpath.getParent());
				// put the default config file in plugin dir
				Files.copy(Objects.requireNonNull(inputStream), configpath);
			}
			catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	public void load() {
		try {
			config = new MainConfig();
			YamlMapping yamlMapping = Yaml.createYamlInput(configFile).readYamlMapping();
			config.setPluginversion(yamlMapping.string("version"));
			config.setDebug(Boolean.parseBoolean(yamlMapping.string("debug")));
			config.setLanguage(yamlMapping.string("language"));
			config.setPrefix(yamlMapping.string("prefix"));

			DatabaseConfig databaseConfig = new DatabaseConfig();
			YamlMapping db = yamlMapping.yamlMapping("database");
			databaseConfig.setType(db.string("type"));
			databaseConfig.setHost(db.string("host"));
			databaseConfig.setUsername(db.string("username"));
			databaseConfig.setPassword(db.string("password"));
			databaseConfig.setDbname(db.string("db-name"));
			databaseConfig.setTablepreifx(db.string("table-prefix"));

			PoolSettings poolSettings = new PoolSettings();
			YamlMapping db_pool = db.yamlMapping("pool-settings");
			poolSettings.setMinIdleConnections(db_pool.integer("min-idle-connections"));
			poolSettings.setMaxPoolSize(db_pool.integer("max-pool-size"));
			poolSettings.setConnectionTimeout(db_pool.longNumber("connection-timeout"));
			poolSettings.setIdleTimeout(db_pool.longNumber("idle-timeout"));
			poolSettings.setMaxLifetime(db_pool.longNumber("max-lifetime"));

			databaseConfig.setPoolSettings(poolSettings);

			config.setDatabase(databaseConfig);

			LinkedHashMap<String, TrackConfig> lptracksHM = new LinkedHashMap<>();
			YamlMapping lptracks = yamlMapping.yamlMapping("lp-tracks");

			for (YamlNode key : lptracks.keys()) {
				TrackConfig _trackConfig = new TrackConfig();

				YamlMapping _lptrack = lptracks.yamlMapping(key);
				_trackConfig.setName(_lptrack.string("name"));
				_trackConfig.setIconItem(_lptrack.string("icon-item"));
				_trackConfig.setHidelocked(Boolean.parseBoolean(_lptrack.string("hide-locked")));

				List<String> permaRanks = new ArrayList<>();
				YamlSequence permRanks =  _lptrack.yamlSequence("permanent-ranks");
				for (int i = 0; i < permRanks.size(); i++) {
					permaRanks.add(permRanks.string(i));
				}
				_trackConfig.setPermanentranks(permaRanks);

				YamlMapping gui = _lptrack.yamlMapping("gui");
				LinkedHashMap<String, Integer> guiMap = new LinkedHashMap<>();
				guiMap.put("row", Integer.parseInt(gui.string("row")));
				guiMap.put("col", Integer.parseInt(gui.string("col")));
				_trackConfig.setGui(guiMap);

				lptracksHM.put(key.asScalar().value(), _trackConfig);
			}

			config.setLptracks(lptracksHM);

		} catch (IOException ex) {
			Component msg = Component.text("Error loading the config file.\n" + ex.getMessage());
			Logger.log(msg, Logger.LogTypes.debug);
		}
	}

	public void reload() {
		this.load();
	}


}
