// This file is part of CosmeticRanks, created on 18/04/2024 (17:10 PM)
// Name : PlaceholderAPIHook
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.hooks;

import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import me.clip.placeholderapi.PlaceholderAPI;
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
import java.util.UUID;

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
		return "1.0.1";
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
			if (parms.size() > 3 || parms.size() < 2) return out; // checks for max paramaters = 3 or min = 2
			// cr_{rank}_...
			if (parms.get(0).equals("rank")) {
				// cr_rank_{testtrack}
				String track = parms.get(1);
				UUID uuid;
				if (parms.size() == 2) {
					UUID uid1 = player.getUniqueId();
					if (!instance.getRankManager().getCachedPlayerData().containsKey(uid1) ||
							!instance.getRankManager().getCachedPlayerData().get(uid1).containsKey(track) ) {
						instance.getRankManager().loadPlayerData(player, track);
					}
					uuid = uid1;
				}
				// cr_rank_{testtrack}_{player}
				else {
					OfflinePlayer pl = Helper.getPlayer(parms.get(2));
					if (pl == null) return out;
					UUID uid2 = pl.getUniqueId();
					if (!instance.getRankManager().getCachedPlayerData().containsKey(uid2) ||
							!instance.getRankManager().getCachedPlayerData().get(uid2).containsKey(track) ) {
						instance.getRankManager().loadPlayerData(player, track);
					}
					uuid = uid2;
				}

				List<Column> allData = instance.getRankManager().getCachedPlayerData().get(uuid).get(track);
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

		// PAPI Parse again for the output from LP prefix
		// To add compatibility for Custom Rank Prefixes Images (e.g. from ItemAdder or Oraxen)
		out = PlaceholderAPI.setPlaceholders(player, out);

		return out;
	}
}
