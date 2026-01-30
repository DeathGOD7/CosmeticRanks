// This file is part of CosmeticRanks, created on 16/04/2024 (01:21 AM)
// Name : Helper
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.utils;

import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import io.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import io.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.track.Track;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Helper {
	public static Column findColumn(List<Column> columns, String columnName) {
		for (Column column : columns) {
			if (column.getName().equals(columnName)) {
				return column;
			}
		}
		return null; // Column with the specified name not found
	}

	public static List<Group> getAllObtainedRanksOfTrack(OfflinePlayer player, String track) {
		LuckPerms lp = CosmeticRanks.getInstance().getLuckPerms();
		Track tr = lp.getTrackManager().getTrack(track);

		List<Group> obtainedRanks = new ArrayList<>();
		if (tr == null || player == null) return obtainedRanks;

		for (String g : tr.getGroups()) {
			Group group = lp.getGroupManager().getGroup(g);
			UUID uuid = player.getUniqueId();
			User user = lp.getUserManager().loadUser(uuid).join();

			Collection<Group> inheritedG = user.getInheritedGroups(user.getQueryOptions());
			if (inheritedG.contains(group)) {
				obtainedRanks.add(group);
			}
		}

		return obtainedRanks;
	}

	public static String getTableName(String s) {
		return CosmeticRanks.getInstance().getMainConfig().getDatabase().getTablepreifx() + s;
	}

	public static Component deserializeString(String value) {
		boolean containsLegacyFormattingCharacter = value.indexOf(LegacyComponentSerializer.AMPERSAND_CHAR) != -1
				|| value.indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1;

		if (containsLegacyFormattingCharacter) {
			return LegacyComponentSerializer.legacyAmpersand().deserialize(value).toBuilder()
					.build().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
		} else {
			return CosmeticRanks.getInstance().getMiniMessage().deserialize(value).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
		}
	}

	public static String getGroupPrefix(String group) {
		Group rankgroup = CosmeticRanks.getInstance().getLuckPerms().getGroupManager().getGroup(group);

		if (rankgroup == null) {
			Logger.log(Component.text(CosmeticRanks.getInstance().getLanguageFile().getProperty("group.notfound")
					.replace("<group>", group)), Logger.LogTypes.debug);
			return "???";
		}

		String out = rankgroup.getNodes().stream()
				.filter(node -> node instanceof PrefixNode)
				.map(node -> ((PrefixNode) node).getMetaValue())
				.findFirst().orElse(null);

		if (out == null) {
			out = StringUtils.capitalize(group);
		}

		return out;
	}

	public static String getGroupDisplayName(String group) {
		Group rankgroup = CosmeticRanks.getInstance().getLuckPerms().getGroupManager().getGroup(group);

		if (rankgroup == null) {
			Logger.log(Component.text(CosmeticRanks.getInstance().getLanguageFile().getProperty("group.notfound").replace("<group>", group)), Logger.LogTypes.debug);
			return "???";
		}

		String out = rankgroup.getDisplayName();
		if (out == null) {
			out = StringUtils.capitalize(group);
		}

		return out;
	}

	public static List<Column> getPlayerDatas(OfflinePlayer player, String table) {
		Column uuid = new Column("uuid", player.getUniqueId().toString(), DatabaseManager.DataType.VARCHAR);
		List<Column> allCols = null;

		if (CosmeticRanks.getInstance().getDBM().getDatabase() instanceof SQLite) {
			allCols = CosmeticRanks.getInstance().getDBM().getSQLite().getExactData(table, uuid);
		}
		else if (CosmeticRanks.getInstance().getDBM().getDatabase() instanceof MySQL) {
			allCols = CosmeticRanks.getInstance().getDBM().getMySQL().getExactData(table, uuid);
		}
		else if (CosmeticRanks.getInstance().getDBM().getDatabase() instanceof MongoDB) {
			allCols = CosmeticRanks.getInstance().getDBM().getMongoDB().getExactData(table, uuid);
		}

		return allCols;
	}

	public static OfflinePlayer getPlayer(String playername) {
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			if (p.getName() != null && p.getName().equals(playername)) {
				return p;
			}
		}
		return null;
	}

	public static String parsePlaceholders(OfflinePlayer player, String text) {
		if (CosmeticRanks.getInstance().isPAPIAvailable()) {
			int count = 0;

			// Initialize the text with the original value
			String updatedText = text;

			// Keep resolving placeholders until no more valid placeholders are found
			while (updatedText.matches(".*%[^%]+%.*")) {
				if (count > 10) break;

				Logger.log(Component.text("[Helper] Parse PAPI : " + updatedText), Logger.LogTypes.debug);
				updatedText = PlaceholderAPI.setPlaceholders(player, updatedText);
				count++;
			}

			// Set the final resolved text
			return updatedText;
		}
		return text;
	}

}
