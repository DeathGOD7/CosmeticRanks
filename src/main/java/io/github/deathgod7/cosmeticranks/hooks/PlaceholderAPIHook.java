// This file is part of CosmeticRanks, created on 18/04/2024 (17:10 PM)
// Name : PlaceholderAPIHook
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.hooks;

import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PlaceholderAPIHook extends PlaceholderExpansion {

	CosmeticRanks instance;
	public PlaceholderAPIHook() {
		this.instance = CosmeticRanks.getInstance();
	}
	@Override
	public @NotNull String getIdentifier() {
		return "cr";
	}

	@Override
	public @NotNull String getAuthor() {
		return "Death GOD 7";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0.0";
	}

	@Override
	public boolean persist() {
		return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
	}

	// cr_rank_{player}

	@Override
	public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
		System.out.println("Params: " + params);

		List<String> parms = Arrays.asList(params.split("_"));

		if (!parms.isEmpty()) {
			if (parms.size() > 2) return ""; // checks for max paramaters = 2
			else if (parms.size() == 1) {
				if (parms.get(0).equalsIgnoreCase("rank")) {
					return "self rank" + ": ???";
				}
			}
			else {
				if (parms.get(0).equalsIgnoreCase("rank")) {
					return "rank of " + parms.get(1) + ": ???" ;
				}
			}
		}

		return "";
	}
}
