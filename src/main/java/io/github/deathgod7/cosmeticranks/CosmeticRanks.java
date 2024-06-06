package io.github.deathgod7.cosmeticranks;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.cosmeticranks.commands.CommandsHandler;
import io.github.deathgod7.cosmeticranks.config.ConfigHandler;
import io.github.deathgod7.cosmeticranks.config.GUIRankHandler;
import io.github.deathgod7.cosmeticranks.config.MainConfig;
import io.github.deathgod7.cosmeticranks.database.DatabaseHandler;
import io.github.deathgod7.cosmeticranks.events.EventsHandler;
import io.github.deathgod7.cosmeticranks.hooks.PlaceholderAPIHook;
import io.github.deathgod7.cosmeticranks.ranks.RankManager;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class CosmeticRanks extends JavaPlugin {
	private static CosmeticRanks _instance;
	public static CosmeticRanks getInstance() {
		return _instance;
	}

	private LuckPerms _luckPermsApi;
	public LuckPerms getLuckPerms(){
		return _luckPermsApi;
	}

	private BukkitAudiences adventure;
	public @Nonnull BukkitAudiences adventure() {
		if(this.adventure == null) {
			throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
		}
		return this.adventure;
	}

	private MiniMessage miniMessage;

	public MiniMessage getMiniMessage() {
		return this.miniMessage;
	}

	public static PluginDescriptionFile getPDFile() {
		return _instance.getDescription();
	}
	private MainConfig _mainConfig;
	public MainConfig getMainConfig() {
		return _mainConfig;
	}

	private Properties _languageFile;
	public Properties getLanguageFile() {
		return _languageFile;
	}

	private DatabaseManager _dbm;
	public DatabaseManager getDBM() {
		return _dbm;
	}

	private ConfigHandler _configHandler;
	public ConfigHandler getConfigHandler() {
		return _configHandler;
	}

	private GUIRankHandler _guiRankHandler;
	public GUIRankHandler getGuiRankHandler() {
		return _guiRankHandler;
	}

	private RankManager _rankManager;
	public RankManager getRankManager() {
		return _rankManager;
	}

	private PlaceholderAPIHook _placeholderAPIHook;
	public PlaceholderAPIHook getPlaceholderAPIHook() {
		return _placeholderAPIHook;
	}

	private boolean _isPAPIAvailable;
	public boolean isPAPIAvailable() {
		return _isPAPIAvailable;
	}

	@Override
	public void onEnable() {
		_instance = this;

		// Initialize an audiences instance for the plugin
		this.adventure = BukkitAudiences.create(this);
		// Initialize MiniMessage API
		this.miniMessage = MiniMessage.miniMessage();

		if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
			getLogger().info("Required dependent plugin was not found : LuckPerms");
			getLogger().info("Disabling " + this.getName());
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		_luckPermsApi = LuckPermsProvider.get();


		// plugin startup logic test
		// load main config
		this._configHandler = new ConfigHandler("config.yml");
		this._mainConfig = this._configHandler.getConfig();

		// load ranks config
		this._guiRankHandler = new GUIRankHandler( Paths.get(this.getDataFolder().getPath(),"gui","ranks.yml").toFile());

		// Update the config file if version changed
		String pVer = CosmeticRanks.getInstance().getDescription().getVersion();
		if (!this._mainConfig.getPluginversion().equals(pVer)) {
			this._configHandler.updateVersion(pVer);
		}


		// load language files
		InputStream messagefile;
		this._languageFile = new Properties();

		Path langFilePath = Paths.get (this.getDataFolder().getPath() + "/lang/" + this._mainConfig.getLanguage() +".properties");

		if (!Files.exists(langFilePath)) {
			messagefile = this.getResource("lang/" + this._mainConfig.getLanguage() +".properties");
		} else {
			try {
				messagefile = Files.newInputStream(langFilePath);
			} catch (IOException ex) {
				System.out.println("Error loading the language file from the plugin folder.\n" + ex.getMessage());
				messagefile = this.getResource("lang/" + this._mainConfig.getLanguage() +".properties");
			}
		}

		// load a properties file
		try {
			_languageFile.load(messagefile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Logger.log(Component.text("Main config file loaded").color(NamedTextColor.GREEN), Logger.LogTypes.log);
		Logger.log(Component.text("Language file loaded (" + _mainConfig.getLanguage() + ")").color(NamedTextColor.GREEN), Logger.LogTypes.log);
		Logger.log(Component.text("Hooked to Luckperms successfully").color(NamedTextColor.GREEN), Logger.LogTypes.log);

		// databse loading
		DatabaseHandler databaseHandler = new DatabaseHandler();
		this._dbm= databaseHandler.getDBM();
		Logger.log(Component.text("Database loaded successfully (" + _mainConfig.getDatabase().getType() + ")").color(NamedTextColor.GREEN), Logger.LogTypes.log);

		// register placehoder api
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			_isPAPIAvailable = true;
			_placeholderAPIHook = new PlaceholderAPIHook();
			getPlaceholderAPIHook().register();
			Logger.log(Component.text("Hooked to PlaceholderAPI successfully").color(NamedTextColor.BLUE), Logger.LogTypes.log);
		}

		// rank loading
		_rankManager = new RankManager(this);

		// register commands
		CommandsHandler commandsHandler = new CommandsHandler();

		// register events?
		this.getServer().getPluginManager().registerEvents(new EventsHandler(), this);

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
