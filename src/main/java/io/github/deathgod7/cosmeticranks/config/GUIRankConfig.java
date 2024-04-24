// This file is part of CosmeticRanks, created on 24/04/2024 (19:37 PM)
// Name : GUIRankConfig
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.config;

import java.util.LinkedList;
import java.util.List;

public class GUIRankConfig {
	String name;
	LinkedList<String> description;
	public GUIRankConfig() {
		this.name = "Default";
		this.description = new LinkedList<String>();
		this.description.add("Default Description");
	}

	public GUIRankConfig(String name, LinkedList<String> description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public LinkedList<String> getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(LinkedList<String> description) {
		this.description = description;
	}


}
