package de.howaner.BlueprintCreator.util;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	
	public static File configFile = new File("plugins/BlueprintCreator/config.yml");
	public static int BlockWidth = 20;
	public static int BlockHeight = 20;
	public static boolean LinienEnabled = true;
	
	public static void load() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		BlockWidth = config.getInt("Block.Width");
		BlockHeight = config.getInt("Block.Height");
		LinienEnabled = config.getBoolean("Enabled.Linien");
	}
	
	public static void save() {
		YamlConfiguration config = new YamlConfiguration();
		config.set("Block.Width", BlockWidth);
		config.set("Block.Height", BlockHeight);
		config.set("Enabled.Linien", LinienEnabled);
		try {
			config.save(configFile);
		} catch (Exception e) {}
	}

}
