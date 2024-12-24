// This file is part of CosmeticRanks, created on 20/04/2024 (19:28 PM)
// Name : GuiCommand
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.extention.meta.MetaKey;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.components.exception.GuiException;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.cosmeticranks.utils.Helper;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.ranks.RankManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

@Command(value = "rank", alias = {"ranks", "rankgui", "ranksgui"})
@Permission("cosmeticranks.use.gui")
public class GuiCommand {
	private final CosmeticRanks instance;
	private final LuckPerms lp;
	private final BukkitAudiences audiences;
	private final MiniMessage mm;
	private final DatabaseManager dbm;
	private Properties lang;
	private final RankManager rankManager;

	private String guiTitle;

	private String subGuiTitle;

	public GuiCommand() {
		this.instance = CosmeticRanks.getInstance();
		this.dbm = instance.getDBM();
		this.lp = this.instance.getLuckPerms();
		this.audiences = CosmeticRanks.getInstance().adventure();
		this.mm = instance.getMiniMessage();
		this.lang = instance.getLanguageFile();
		this.rankManager = instance.getRankManager();
		this.guiTitle = lang.getProperty("gui.title");
		this.subGuiTitle = lang.getProperty("gui.title.track");
	}

	// -------------------------------------------------------------------
	// -----------------------[ GUI SECTION ]-----------------------------
	// -------------------------------------------------------------------

	@Command
	public void guiExceutor(CommandSender sender) {
		if (sender instanceof ConsoleCommandSender) {
			Component msgTC = instance.getMiniMessage().deserialize(lang.getProperty("error.playeronly"));
			Logger.log(msgTC, Logger.LogTypes.log);
			return;
		}

		menuCommand(sender, null);

	}

	private void fillGUI(BaseGui gui) {
		String fillername = lang.getProperty("gui.filler");
		gui.getFiller().fillBorder(
				Collections.singletonList(
						ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
								.name(Helper.deserializeString(fillername).decoration(TextDecoration.ITALIC, false))
								.asGuiItem()
				)
		);
	}

	@Command("menu")
	public void menuCommand(CommandSender sender, @Optional Player player) {
		Player p;
		if (player == null) { p = (Player) sender; }
		else { p = player; }

		Gui maingui = Gui.gui()
				.title(Helper.deserializeString(guiTitle))
				.rows(5)
				.disableAllInteractions()
				.create();

		fillGUI(maingui);

		for (String lptrack : rankManager.getRanksTable().keySet()) {
			String trackName = instance.getMainConfig().getLptracks().get(lptrack).getName();

			String materialName = instance.getMainConfig().getLptracks().get(lptrack).getIconItem();
			Material material;
			try {
				material = Material.valueOf(materialName.toUpperCase());
			}
			catch (IllegalArgumentException e) {
				Logger.log(Component.text("Error while getting material: " + e.getMessage()), Logger.LogTypes.debug);
				material = Material.PAPER;
			}

			int row = instance.getMainConfig().getLptracks().get(lptrack).getGui().get("row");
			int col = instance.getMainConfig().getLptracks().get(lptrack).getGui().get("col");

			GuiItem guiItem = ItemBuilder.from(material)
					.name(Helper.deserializeString(trackName).decoration(TextDecoration.ITALIC, false))
					.glow()
					.asGuiItem();

			guiItem.setAction(e -> {
				String sub_GuiTitle = subGuiTitle.replace("<track>", trackName);
				PaginatedGui subgui = Gui.paginated()
						.title(Helper.deserializeString(sub_GuiTitle).decoration(TextDecoration.ITALIC, false))
						.rows(6)
						.pageSize(28)
						.disableAllInteractions()
						.create();

				fillGUI(subgui);

				// fill items with #addItem(..)
				List<Column> allDatas = rankManager.getCachedPlayerData().get(p.getUniqueId()).get(lptrack);
				Column obtainedranks = Helper.findColumn(allDatas, "obtainedranks");
				Column selectedrank = Helper.findColumn(allDatas, "selectedrank");

				if (obtainedranks != null) {
					List<String> temp = new ArrayList<>(Arrays.asList(obtainedranks.getValue().toString().split(",")));
					// fix for ghost rank id tag
					temp.remove("");

					for (String rank : temp) {
						String rankName;
						LinkedList<String> description;

						if (instance.getGuiRankHandler().getConfig().containsKey(rank)) {
							rankName = instance.getGuiRankHandler().getConfig().get(rank).getName();
							description = new LinkedList<>(instance.getGuiRankHandler().getConfig().get(rank).getDescription());
						}
						else {
							rankName = rank;
							description = new LinkedList<>();
							description.add("The default description of " + rank);
						}

						if (selectedrank != null) {
							String currentRank = selectedrank.getValue().toString();

							if (currentRank.equals(rank)) {
								description.add("");
								description.add(lang.getProperty("gui.rank.current"));
							}
						}

						Material rankMaterial = Material.NAME_TAG;
						LinkedList<Component> loreComponents = new LinkedList<>();

						for (String desc : description) {
							if (instance.isPAPIAvailable()) desc = PlaceholderAPI.setPlaceholders(p, desc);
							loreComponents.add(Helper.deserializeString(desc).decoration(TextDecoration.ITALIC, false));
						}

						ItemBuilder ib = ItemBuilder.from(rankMaterial)
								.name(Helper.deserializeString(rankName).decoration(TextDecoration.ITALIC, false))
								.glow()
								.lore(loreComponents);


						GuiItem guiRankItem = ib.asGuiItem();

						guiRankItem.setAction(event -> {
							// cr rank set self default default
							Bukkit.dispatchCommand(p, "cr rank set self " + lptrack + " " + rank);
							// close the menu
							subgui.close(p);
						});

						// finally add it to sub gui
						try {
							subgui.addItem(guiRankItem);
						}
						catch (GuiException ex) {
							Component f = Component.text("Error while adding items ("+ rank +") to GUI ("+ lptrack +"): " + ex.getMessage());
							Logger.log(f, Logger.LogTypes.debug);
						}
					}
				}
				else {
					Logger.log(Component.text("Error while getting obtained ranks"), Logger.LogTypes.debug);
				}

				// Player Head
				LinkedList<String> pHeadDesc = new LinkedList<>(instance.getGuiRankHandler().getpHeadDescription());
				LinkedList<Component> pHeadLoreCmp = new LinkedList<>();

				for (String desc : pHeadDesc) {
					String selRank = "None";
					if (selectedrank != null) {
						selRank = selectedrank.getValue().toString();
						if (selRank.isEmpty()) selRank = "None";
					}
					if (instance.getGuiRankHandler().getConfig().containsKey(selRank)) {
						selRank = instance.getGuiRankHandler().getConfig().get(selRank).getName();
					}

					desc = desc.replace("<playername>", p.getName())
						.replace("<rank>", selRank);

					if (instance.isPAPIAvailable())
						desc = PlaceholderAPI.setPlaceholders(p,desc);
					pHeadLoreCmp.add(Helper.deserializeString(desc).decoration(TextDecoration.ITALIC, false));
				}

				ItemBuilder playerHead = ItemBuilder.from(Material.PLAYER_HEAD)
						.name(Helper.deserializeString(lang.getProperty("gui.playerhead")).decoration(TextDecoration.ITALIC, false))
						.lore(pHeadLoreCmp)
						.glow();
				subgui.setItem(1, 5, playerHead.asGuiItem());

				// Previous item
				ItemBuilder previousItem = ItemBuilder.from(Material.ARROW)
						.name(Helper.deserializeString(lang.getProperty("gui.previous")).decoration(TextDecoration.ITALIC, false))
						.glow();
				subgui.setItem(6, 3, previousItem.asGuiItem(event -> subgui.previous()));

				// Next item
				ItemBuilder nextItem = ItemBuilder.from(Material.ARROW)
						.name(Helper.deserializeString(lang.getProperty("gui.next")).decoration(TextDecoration.ITALIC, false))
						.glow();
				subgui.setItem(6, 7, nextItem.asGuiItem(event -> subgui.next()));

				// Back item
				ItemBuilder backItem = ItemBuilder.from(Material.BARRIER)
						.name(Helper.deserializeString(lang.getProperty("gui.back")).decoration(TextDecoration.ITALIC, false))
						.glow();
				subgui.setItem(6, 5,backItem.asGuiItem(event -> { maingui.open(p);}));

				// maybe show the gui too BOB???
				subgui.open(p);


			});

			try {
				maingui.setItem(row, col, guiItem);
			}
			catch (GuiException e) {
				Component f = Component.text("Error while adding items ("+ lptrack +") to GUI(main): " + e.getMessage());
				Logger.log(f, Logger.LogTypes.debug);
			}
		}


		maingui.open(p);


	}

}
