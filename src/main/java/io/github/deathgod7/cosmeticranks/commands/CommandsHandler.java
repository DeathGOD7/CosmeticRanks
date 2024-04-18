// This file is part of CosmeticRanks, created on 14/04/2024 (23:26 PM)
// Name : CommandsHandler
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.commands;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.extention.meta.MetaKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.component.Table;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.track.Track;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class CommandsHandler {
	CosmeticRanks instance;
	private final BukkitCommandManager<CommandSender> commandManager;

	private final LuckPerms luckPerms;
	public BukkitCommandManager<CommandSender> getCommandManager() {
		return commandManager;
	}

	public CommandsHandler() {
		this.instance = CosmeticRanks.getInstance();
		luckPerms = instance.getLuckPerms();
		commandManager = BukkitCommandManager.create(instance);
		registerSuggestions();
		registerCommands();
		registerMessages();

	}

	void registerMessages() {
		commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> {
			String msg = instance.getLanguageFile().getProperty("error.invalidargs");
			String msgConsole = instance.getLanguageFile().getProperty("error.invalidargs.console")
					.replace("<sender>", sender.getName());

			Component msgTC = instance.getMiniMessage().deserialize(msg);
			Component msgConsoleTC = instance.getMiniMessage().deserialize(msgConsole);

			instance.adventure().sender(sender).sendMessage(msgTC);
			instance.adventure().console().sendMessage(msgConsoleTC);
		});

		commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
			String msg = instance.getLanguageFile().getProperty("error.toomanyargs");
			String msgConsole = instance.getLanguageFile().getProperty("error.toomanyargs.console")
					.replace("<sender>", sender.getName());

			Component msgTC = instance.getMiniMessage().deserialize(msg);
			Component msgConsoleTC = instance.getMiniMessage().deserialize(msgConsole);

			instance.adventure().sender(sender).sendMessage(msgTC);
			instance.adventure().console().sendMessage(msgConsoleTC);
		});

		commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
			String msg = instance.getLanguageFile().getProperty("error.notenoughargs");
			String msgConsole = instance.getLanguageFile().getProperty("error.notenoughargs.console")
					.replace("<sender>", sender.getName());

			Component msgTC = instance.getMiniMessage().deserialize(msg);
			Component msgConsoleTC = instance.getMiniMessage().deserialize(msgConsole);

			instance.adventure().sender(sender).sendMessage(msgTC);
			instance.adventure().console().sendMessage(msgConsoleTC);
		});

		commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> {
			String msg = instance.getLanguageFile().getProperty("error.unknowncmd")
					.replace("<command>", context.getInvalidInput());
			String msgConsole = instance.getLanguageFile().getProperty("error.unknowncmd.console")
					.replace("<sender>", sender.getName())
					.replace("<command>", context.getInvalidInput());

			Component msgTC = instance.getMiniMessage().deserialize(msg);
			Component msgConsoleTC = instance.getMiniMessage().deserialize(msgConsole);

			instance.adventure().sender(sender).sendMessage(msgTC);
			instance.adventure().console().sendMessage(msgConsoleTC);
		});

		commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> {
			String msg = instance.getLanguageFile().getProperty("error.nopermission");
			String cmd = "";
			if (context.getMeta().get(MetaKey.NAME).isPresent()) {
				cmd = context.getMeta().get(MetaKey.NAME).get();
			}
			String msgConsole = instance.getLanguageFile().getProperty("error.nopermission.console")
					.replace("<sender>", sender.getName())
					.replace("<command>", cmd);

			Component msgTC = instance.getMiniMessage().deserialize(msg);
			Component msgConsoleTC = instance.getMiniMessage().deserialize(msgConsole);

			instance.adventure().sender(sender).sendMessage(msgTC);
			instance.adventure().console().sendMessage(msgConsoleTC);
		});

		commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> {
			String msg = instance.getLanguageFile().getProperty("error.playeronly");
			String cmd = "";
			if (context.getMeta().get(MetaKey.NAME).isPresent()) {
				cmd = context.getMeta().get(MetaKey.NAME).get();
			}
			String msgConsole = instance.getLanguageFile().getProperty("error.playeronly.console")
					.replace("<command>", cmd);

			Component msgTC = instance.getMiniMessage().deserialize(msg);
			Component msgConsoleTC = instance.getMiniMessage().deserialize(msgConsole);

			instance.adventure().sender(sender).sendMessage(msgTC);
			instance.adventure().console().sendMessage(msgConsoleTC);
		});

		commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> {
			String msg = instance.getLanguageFile().getProperty("error.consoleonly");

			String cmd = "";
			if (context.getMeta().get(MetaKey.NAME).isPresent()) {
				cmd = context.getMeta().get(MetaKey.NAME).get();
			}

			String msgConsole = instance.getLanguageFile().getProperty("error.consoleonly.console")
					.replace("<command>", cmd);

			Component msgTC = instance.getMiniMessage().deserialize(msg);
			Component msgConsoleTC = instance.getMiniMessage().deserialize(msgConsole);

			instance.adventure().sender(sender).sendMessage(msgTC);
			instance.adventure().console().sendMessage(msgConsoleTC);
		});
	}

	void registerSuggestions() {
		// Register suggestions here

		commandManager.registerSuggestion(SuggestionKey.of("allplayers"), (sender, arguments) -> {
			List<String> players = new ArrayList<>();
			for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
				players.add(p.getName());
			}
			return players;
		});

		// For the lptracks
		commandManager.registerSuggestion(SuggestionKey.of("lptracks"), (sender, arguments) -> {
			return (List<String>) new ArrayList<String>(instance.getRankManager().getRanksTable().keySet());
		});

		// For the ranks (all)
		commandManager.registerSuggestion(SuggestionKey.of("ranks"), (sender, arguments) -> {
			Set<String> allTracks = instance.getRankManager().getRanksTable().keySet();
			String lastArg = arguments.get(1);

			if (allTracks.contains(lastArg)) {
				Track track = luckPerms.getTrackManager().getTrack(lastArg);
				assert track != null;
				return (List<String>) new ArrayList<String>(track.getGroups());

			}
			return (List<String>) new ArrayList<String>();
		});

		// For the ranks (obtained)
		commandManager.registerSuggestion(SuggestionKey.of("obtainedranks"), (sender, arguments) -> {
			Set<String> allTracks = instance.getRankManager().getRanksTable().keySet();
			String trackname = "";

			if (arguments.size() == 3) {
				trackname = arguments.get(1);
			}
			else if (arguments.size() == 2){
				trackname = arguments.get(0);
			}

			if (allTracks.contains(trackname)) {
				Table table = instance.getRankManager().getRanksTable().get(trackname);

				Player p = null;
				if (arguments.size() == 3) {
					p = Bukkit.getPlayer(arguments.get(0));
				}
				else if (arguments.size() == 2){
					if (sender instanceof ConsoleCommandSender) {
						return (List<String>) new ArrayList<String>();
					}
					p = Bukkit.getPlayer(sender.getName());
				}

				if (p == null) { return (List<String>) new ArrayList<String>(); }

				List<Column> datas = getPlayerDatas(p, table);

				if (datas == null) { return (List<String>) new ArrayList<String>(); }
				Column colObtainedranks = Helper.findColumn(datas, "obtainedranks");

				if (colObtainedranks != null) {
					String[] obtainedRanks = colObtainedranks.getValue().toString().split(",");
					return (List<String>) Arrays.asList(obtainedRanks);
				}

				return (List<String>) new ArrayList<String>();
			}
			return (List<String>) new ArrayList<String>();
		});

	}


	List<Column> getPlayerDatas(Player player, Table table) {
		DatabaseManager dbm = this.instance.getDBM();
		DatabaseType dbtype = dbm.getDbInfo().getDbType();

		Column pk = table.getPrimaryKey();
		pk.setValue(player.getUniqueId().toString());

		if (dbtype == DatabaseType.MySQL) {
			return dbm.getMySQL().getExactData(table.getName(), pk);
		} else if (dbtype == DatabaseType.SQLite) {
			return dbm.getSQLite().getExactData(table.getName(), pk);
		} else if (dbtype == DatabaseType.MongoDB) {
			return dbm.getMongoDB().getExactData(table.getName(), pk);
		}
		else return null;
	}

	void registerCommands() {
		// Register commands here

		// main commands
		commandManager.registerCommand(new MainCommand());
	}
}
