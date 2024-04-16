// This file is part of CosmeticRanks, created on 26/10/2023 (16:34 PM)
// Name : Logger
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.utils;

import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public final class Logger {

	static String logPrefix = CosmeticRanks.getInstance().getLanguageFile().get("plugin.logprefix").toString()
			.replace("<prefix>", CosmeticRanks.getInstance().getMainConfig().getPrefix());

	static String msgPrefix = CosmeticRanks.getInstance().getLanguageFile().get("plugin.msgprefix").toString()
			.replace("<prefix>", CosmeticRanks.getInstance().getMainConfig().getPrefix());
	static TextComponent logPrefixTC = Component.text(logPrefix);

	static TextComponent msgPrefixTC = Component.text(msgPrefix);
	public static boolean debugMode = CosmeticRanks.getInstance().getMainConfig().getDebug();

	public enum LogTypes {
		log,
		debug
	}

	public enum LogLevels {
		info,
		warning,
		severe
	}

	public static void log(String msg, LogLevels logLevel, LogTypes logType) {
		switch (logLevel) {
			case info:
				if ((logType == LogTypes.debug && debugMode) || logType == LogTypes.log) {
					Bukkit.getLogger().info(logPrefix + msg);
				}
				break;
			case warning:
				if ((logType == LogTypes.debug && debugMode) || logType == LogTypes.log) {
					Bukkit.getLogger().warning(logPrefix + msg);
				}
				break;
			case severe:
				if ((logType == LogTypes.debug && debugMode) || logType == LogTypes.log) {
					Bukkit.getLogger().severe(logPrefix + msg);
				}
				break;
		}
	}

	public static void log(Component msg, LogTypes logType) {
		if ((logType == LogTypes.debug && debugMode) || logType == LogTypes.log) {
			CosmeticRanks.getInstance().adventure().console().sendMessage(logPrefixTC.color(NamedTextColor.GRAY).append(msg));
		}
	}

	public static void sendToPlayer(CommandSender sender, Component msg) {
		CosmeticRanks.getInstance().adventure().sender(sender).sendMessage(msgPrefixTC.append(msg));
	}

	public static void sendToPlayer(Player player, Component msg) {
		CosmeticRanks.getInstance().adventure().player(player).sendMessage(msgPrefixTC.append(msg));
	}

	public static void log(Component msg, CommandSender sender) {
		if (sender instanceof ConsoleCommandSender) {
			CosmeticRanks.getInstance().adventure().console().sendMessage(logPrefixTC.append(msg));
		}
		else {
			CosmeticRanks.getInstance().adventure().console().sendMessage(logPrefixTC.append(msg));
			CosmeticRanks.getInstance().adventure().sender(sender).sendMessage(msgPrefixTC.append(msg));
		}
	}
}
