// This file is part of CosmeticRanks, created on 18/04/2024 (17:10 PM)
// Name : PlaceholderAPIHook
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.hooks;

import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.PrefixNode;
import org.apache.commons.lang.StringUtils;
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

	// cr_rank_{track}_{player}

	@Override
	public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
		String out = "";
		List<String> parms = Arrays.asList(params.split("_"));

		if (!parms.isEmpty()) {
			if (parms.size() > 3) return out; // checks for max paramaters = 3
			// cr_rank_.....
			if (parms.get(0).equals("rank")) {
				if (parms.size() == 2) {
					String track = parms.get(1);
					List<Column> allData = instance.getRankManager().getCachedPlayerData().get(player.getUniqueId()).get(track);
					if (allData == null || allData.isEmpty()) {
						return out;
					}
					Column selRank = Helper.findColumn(allData, "selectedrank");
					assert selRank != null;
					if (!selRank.getValue().toString().isEmpty()) {
						out = Helper.getGroupPrefix(selRank.getValue().toString());
					}
				}
				else if (parms.size() == 3) {
					String track = parms.get(1);
					OfflinePlayer pl = Helper.getPlayer(parms.get(2));
					if (pl == null) return out;
					List<Column> allData = instance.getRankManager().getCachedPlayerData().get(pl.getUniqueId()).get(track);
					if (allData == null || allData.isEmpty()) {
						return out;
					}
					Column selRank = Helper.findColumn(allData, "selectedrank");
					assert selRank != null;
					if (!selRank.getValue().toString().isEmpty()) {
						out = Helper.getGroupPrefix(selRank.getValue().toString());
					}
				}
			}

		}

		return out;
	}
}
