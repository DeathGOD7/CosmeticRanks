// This file is part of CosmeticRanks, created on 26/10/2023 (16:34 PM)
// Name : Logger
// Author : Death GOD 7

package com.github.deathgod7.cosmeticranks.utils;

import com.github.deathgod7.cosmeticranks.CosmeticRanks;
import org.bukkit.Bukkit;

public final class Logger {

	static String logPrefix = CosmeticRanks.getInstance().getLanguageFile().get("plugin.logprefix").toString()
			.replace("<prefix>", CosmeticRanks.getInstance().getMainConfig().prefix);
	public static boolean debugMode = CosmeticRanks.getInstance().getMainConfig().debug;

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
}
