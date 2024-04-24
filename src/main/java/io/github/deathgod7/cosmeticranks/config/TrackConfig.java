// This file is part of CosmeticRanks, created on 26/10/2023 (16:40 PM)
// Name : MainConfig
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.config;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class TrackConfig {
	String name;
	boolean hidelocked; // hide the locked ranks
	List<String> permanentranks;

	LinkedHashMap<String, Integer> gui = new LinkedHashMap<>();

	String iconItem;

	public TrackConfig() {
		this.name = "default";
		this.hidelocked = false;
		this.permanentranks = Arrays.asList("default", "admin");
		this.gui.put("row", 3);
		this.gui.put("col", 5);
		this.iconItem = "PAPER";
	}

	public TrackConfig(String name, boolean hidelocked, List<String> permanentranks, int row, int col, String iconItem) {
		this.name = name;
		this.hidelocked = hidelocked;
		this.permanentranks = permanentranks;
		this.gui.put("row", row);
		this.gui.put("col", col);
		this.iconItem = iconItem;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setHidelocked(boolean hidelocked) {
		this.hidelocked = hidelocked;
	}

	public boolean getHidelocked() {
		return hidelocked;
	}

	public void setPermanentranks(List<String> permanentranks) {
		this.permanentranks = permanentranks;
	}

	public List<String> getPermanentranks() {
		return permanentranks;
	}

	public void setGui(LinkedHashMap<String, Integer> gui) {
		this.gui = gui;
	}

	public LinkedHashMap<String, Integer> getGui() {
		return gui;
	}


	public void setIconItem(String iconItem) {
		this.iconItem = iconItem;
	}

	public String getIconItem() {
		return iconItem;
	}

}







