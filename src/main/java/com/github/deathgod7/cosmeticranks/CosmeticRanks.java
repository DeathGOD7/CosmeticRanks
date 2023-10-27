package com.github.deathgod7.cosmeticranks;

import com.github.deathgod7.cosmeticranks.commands.CommandHandler;
import com.github.deathgod7.cosmeticranks.config.MainConfig;
import com.github.deathgod7.cosmeticranks.utils.Logger;
import com.github.deathgod7.cosmeticranks.utils.Logger.LogLevels;
import com.github.deathgod7.cosmeticranks.utils.Logger.LogTypes;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.config.ConfigManager;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public final class CosmeticRanks extends JavaPlugin {
	private static CosmeticRanks _instance;
	public static CosmeticRanks getInstance() {
		return _instance;
	}

	private static LuckPerms _luckPermsApi;
	public static LuckPerms getLuckPerms(){
		return _luckPermsApi;
	}

	private BukkitAudiences adventure;
	public @Nonnull BukkitAudiences adventure() {
		if(this.adventure == null) {
			throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
		}
		return this.adventure;
	}

	public static PluginDescriptionFile getPDFile() {
		return _instance.getDescription();
	}

	private ConfigManager _mainConfigManager;
	public ConfigManager getMainConfigManager() { return  _mainConfigManager; }
	private MainConfig _mainConfig;
	public MainConfig getMainConfig() {
		return _mainConfig;
	}

	private Properties _languageFile;
	public Properties getLanguageFile() {
		return _languageFile;
	}
	@Override
	public void onEnable() {
		_instance = this;

		// Initialize an audiences instance for the plugin
		this.adventure = BukkitAudiences.create(this);

		if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
			getLogger().info("Required dependent plugin was not found : LuckPerms");
			getLogger().info("Disabling " + this.getName());
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		_luckPermsApi = LuckPermsProvider.get();

		// main config
		if (_mainConfig == null){
			_mainConfig = new MainConfig();
		}
		this._mainConfigManager = ConfigManager.create(CosmeticRanks.getInstance()).target(_mainConfig).saveDefaults().load();

		String configVer = this._mainConfig.version;
		String pluginVer = CosmeticRanks.getPDFile().getVersion();
		if (!Objects.equals(configVer, pluginVer)){
			this._mainConfig.previousversion =  this._mainConfig.version;
			this._mainConfig.version = CosmeticRanks.getPDFile().getVersion();
			getMainConfigManager().save();
		}


		// load language files
		InputStream messagefile;
		this._languageFile = new Properties();

		Path langFilePath = Paths.get (this.getDataFolder().getPath() + "/lang/" + _mainConfig.language +".properties");

		if (!Files.exists(langFilePath)) {
			messagefile = this.getResource("lang/" + _mainConfig.language +".properties");
		} else {
			try {
				messagefile = Files.newInputStream(langFilePath);
			} catch (IOException ex) {
				messagefile = this.getResource("lang/" + _mainConfig.language +".properties");
				ex.printStackTrace();
			}
		}

		// load a properties file
		try {
			_languageFile.load(messagefile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		Logger.log("Hooked to Luckperms successfully", LogLevels.info, LogTypes.log);
		Logger.log("Main config file loaded", LogLevels.info, LogTypes.log);
		Logger.log("Language file loaded (" + _mainConfig.language + ")", LogLevels.info, LogTypes.log);
		Logger.log(langFilePath.toString(), LogLevels.warning, LogTypes.debug);

		// register commands
		new CommandParser(this.getResource("commands.rdcml"))
//				.setArgTypes
//						(
//								//ArgType.of("CurrencyType", currencyTypeManager.getAllCurrencyTypes()),
//								currencyType,
//								offlinePlayerType
//						)
				.parse()
				.register("cosmeticranks",
						new CommandHandler()
				);

		// register events?

		TextComponent message = Component.text(_languageFile.getProperty("plugin.test").replace("<prefix>", _mainConfig.prefix));
		Logger.log(message, LogTypes.debug);
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic... like used for your brain.

		if(this.adventure != null) {
			this.adventure.close();
			this.adventure = null;
		}
	}
}
