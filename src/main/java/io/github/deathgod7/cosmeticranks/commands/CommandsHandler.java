// This file is part of CosmeticRanks, created on 14/04/2024 (23:26 PM)
// Name : CommandsHandler
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.commands;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.CommandPermission;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.triumphteam.cmd.bukkit.CommandPermission.hasPermission;

public class CommandsHandler {
	CosmeticRanks instance;
	private final BukkitCommandManager<CommandSender> commandManager;
	public BukkitCommandManager<CommandSender> getCommandManager() {
		return commandManager;
	}

	public CommandsHandler() {
		this.instance = CosmeticRanks.getInstance();
		commandManager = BukkitCommandManager.create(instance);
		registerSuggestions();
		registerCommands();
	}

	void registerSuggestions() {
		// Register suggestions here

		commandManager.registerSuggestion(SuggestionKey.of("lptracks"), (sender, arguments) -> {
			return (List<String>) new ArrayList<String>(instance.getRankManager().getRanksTable().keySet());
		});

	}

	void registerCommands() {
		// Register commands here

		// main commands
		commandManager.registerCommand(new MainCommand());
	}
}
