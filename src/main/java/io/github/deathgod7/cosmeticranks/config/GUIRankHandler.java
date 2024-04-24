// This file is part of CosmeticRanks, created on 24/04/2024 (19:26 PM)
// Name : GUIRankHandler
// Author : Death GOD 7

package io.github.deathgod7.cosmeticranks.config;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import io.github.deathgod7.cosmeticranks.CosmeticRanks;
import io.github.deathgod7.cosmeticranks.utils.Logger;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GUIRankHandler {
	CosmeticRanks instance = CosmeticRanks.getInstance();
	File configFile;

	LinkedHashMap<String, GUIRankConfig> config;

	public LinkedHashMap<String, GUIRankConfig> getConfig() {
		return config;
	}
	public GUIRankHandler(File file) {
		this.configFile = file;
		this.checkConfig();
		this.load();
	}

	private void checkConfig() {
		if (!configFile.exists()) {
			this.saveDefaults(
					instance.getResource(configFile.getName())
			);
		}
	}

	private void saveDefaults(InputStream inputStream) {
		Path guiconfigpath = configFile.toPath();
		if (Files.notExists(guiconfigpath)) {
			try {
				// create plugin dir
				Files.createDirectory(guiconfigpath.getParent());
				// put the default config file in plugin dir
				Files.copy(Objects.requireNonNull(inputStream), guiconfigpath);
			}
			catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	public void load() {
		try {
			config = new LinkedHashMap<>();

			YamlMapping yamlMapping = Yaml.createYamlInput(configFile).readYamlMapping();
			YamlMapping ranks = yamlMapping.yamlMapping("ranks");

			for (YamlNode key : ranks.keys()) {
				GUIRankConfig temp = new GUIRankConfig();

				YamlMapping r = ranks.yamlMapping(key);
				temp.setName(r.string("display-name"));

				LinkedList<String> desc = new LinkedList<>();
				YamlSequence alldesc =  r.yamlSequence("description");
				for (int i = 0; i < alldesc.size(); i++) {
					desc.add(alldesc.string(i));
				}
				temp.setDescription(desc);

				config.put(key.asScalar().value(), temp);
			}


		}
		catch (IOException ex) {
			Component msg = Component.text("Error loading the rank config file.\n" + ex.getMessage());
			Logger.log(msg, Logger.LogTypes.debug);
		}
	}

	public void reload() {
		this.load();
	}
}
