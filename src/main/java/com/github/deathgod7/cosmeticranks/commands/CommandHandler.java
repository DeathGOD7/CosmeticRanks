// This file is part of CosmeticRanks, created on 26/10/2023 (18:45 PM)
// Name : CommandHandler
// Author : Death GOD 7

package com.github.deathgod7.cosmeticranks.commands;

import com.github.deathgod7.cosmeticranks.CosmeticRanks;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CommandHandler {

	private final CosmeticRanks instance;
	private final LuckPerms lp;
	private final BukkitAudiences audiences;
	private final String pluginPrefix;

	public CommandHandler() {
		this.instance = CosmeticRanks.getInstance();
		this.lp = CosmeticRanks.getLuckPerms();
		this.audiences = CosmeticRanks.getInstance().adventure();
		this.pluginPrefix = instance.getLanguageFile().getProperty("plugin.msgprefix")
				.replace("<prefix>", instance.getMainConfig().prefix);
	}

	// -------------------------------------------------------------------
	// --------------------[ TEST SECTION ]----------------------------
	// -------------------------------------------------------------------

	@CommandHook("testgroups")
	public void testgroups(CommandSender commandSender) {
		StringBuilder temp = new StringBuilder();

		PaginatedGui gui = Gui.paginated()
				.title(Component.text("GUI Title!"))
				.rows(6)
				.disableAllInteractions()
				.create();

		String lptrack = instance.getMainConfig().lptrack;

		for (String x : lp.getTrackManager().getTrack(lptrack).getGroups()) {
			Group gp = lp.getGroupManager().getGroup(x);
			assert gp != null;

			ItemStack item = new ItemStack(Material.OBSIDIAN);
			item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 0);
			ItemMeta itemmeta = item.getItemMeta();
			itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(itemmeta);

			GuiItem gI = ItemBuilder.from(item).name(Component.text(gp.getName())).asGuiItem();
			gI.setAction(event -> {
				changeRanks(commandSender, x);
				audiences.player((Player) commandSender).sendMessage(
						Component.text("You clicked " + gp.getName()).color(TextColor.color(0x8637e6)
						));
				audiences.player((Player) commandSender).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#5e4fa2:#f79459>||||||||||||||||||||||||</gradient>"));
			});
			gui.addItem(gI);
		}

		gui.open((Player) commandSender);
		commandSender.sendMessage(Arrays.toString(instance.getMainConfig().permanentranks.toArray()));

	}

	public void changeRanks(CommandSender cms, String rankgroup) {
		Player p = (Player) cms;
		UserManager userManager = lp.getUserManager();
		String lptrack = instance.getMainConfig().lptrack;
		List<String> allTrackGroups = new ArrayList<>(lp.getTrackManager().getTrack(lptrack).getGroups());

		for (String ps:instance.getMainConfig().permanentranks) {
			allTrackGroups.remove(ps);
		}

		allTrackGroups.remove(rankgroup);

		userManager.modifyUser(p.getUniqueId(), user -> {
			//remove
			for (String x : allTrackGroups) {
				cms.sendMessage(x);
				Node node = Node.builder("group." + x).build();
				if (user.getNodes().contains(node)) {
					user.data().remove(node);
					cms.sendMessage("Removed " + x);
				}
			}

			// add
			Node newNode = Node.builder("group." + rankgroup).build();
			if (!user.getNodes().contains(newNode)) {
				user.data().add(newNode);
				cms.sendMessage("Added " + newNode.getKey());
			}
		});

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
