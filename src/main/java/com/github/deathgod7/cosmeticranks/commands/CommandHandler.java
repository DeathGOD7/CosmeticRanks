// This file is part of CosmeticRanks, created on 26/10/2023 (18:45 PM)
// Name : CommandHandler
// Author : Death GOD 7

package com.github.deathgod7.cosmeticranks.commands;

import com.github.deathgod7.cosmeticranks.CosmeticRanks;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommandHandler {

	private final CosmeticRanks instance;
	private final LuckPerms lp;
	private final String pluginPrefix;

	public CommandHandler() {
		this.instance = CosmeticRanks.getInstance();
		this.lp = CosmeticRanks.getLuckPerms();
		this.pluginPrefix = instance.getLanguageFile().getProperty("plugin.msgprefix")
				.replace("<prefix>", instance.getMainConfig().prefix);
	}

	// -------------------------------------------------------------------
	// --------------------[ TEST SECTION ]----------------------------
	// -------------------------------------------------------------------

	@CommandHook("testgroups")
	public void testgroups(CommandSender commandSender) {
		StringBuilder temp = new StringBuilder();
		for (Group x : CosmeticRanks.getLuckPerms().getGroupManager().getLoadedGroups() ) {
			temp.append(x.getName()).append(", ");
		}
		String ftemp = temp.substring(0, temp.length() - 2);
		commandSender.sendMessage("Available Groups : " + ftemp);
	}

	// -------------------------------------------------------------------
	// --------------------[ PLUGIN SECTION ]-----------------------------
	// -------------------------------------------------------------------

	@CommandHook("info")
	public void info(CommandSender commandSender){
		String databaseType;
		String isConnected;
		String version = ChatColor.translateAlternateColorCodes('&', "&ev" + CosmeticRanks.getPDFile().getVersion());
		String apiversion = ChatColor.translateAlternateColorCodes('&', "&e"+CosmeticRanks.getPDFile().getAPIVersion());
		String developer = ChatColor.translateAlternateColorCodes('&', "&1"+CosmeticRanks.getPDFile().getAuthors());

		String temp1 = instance.getMainConfig().db_type;
		if (Objects.equals(temp1, "mysql")){
			databaseType = ChatColor.translateAlternateColorCodes('&', "&3MySQL");
		}
		else{
			databaseType = ChatColor.translateAlternateColorCodes('&', "&3SQLite");
		}

//		if (dbm.isConnected()){
//			isConnected = TextUtils.ConvertTextColor('&', "&2ONLINE");
//		}
//		else{
//			isConnected = TextUtils.ConvertTextColor('&', "&4OFFLINE");
//		}

		commandSender.sendMessage("Cosmetic Ranks");
		commandSender.sendMessage("Version : " + version);
		commandSender.sendMessage("Developer(s) : " + developer);
		commandSender.sendMessage("API Version : " + apiversion);
		commandSender.sendMessage("Database : " + databaseType);
		commandSender.sendMessage("Status : ??" /* + isConnected*/);
	}

	@CommandHook("reload")
	public void reload(CommandSender commandSender){
		String warning = "&aIt is best to restart the server as reloading will just break the plugin.";
		commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', warning));
		//instance.ReloadConfigs();
	}

	@CommandHook("debug")
	public void debug(CommandSender commandSender) {
		String warning = "&4Debug will be added soon.";
		commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', warning));
	}


}
