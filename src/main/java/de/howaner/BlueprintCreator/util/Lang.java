package de.howaner.BlueprintCreator.util;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import de.howaner.BlueprintCreator.BlueprintPlugin;

public enum Lang {
	PLUGIN_ENABLED("Plugin enabled!"),
	PLUGIN_DISABLED("Plugin disabled!"),
	PREFIX("&7[&6BlueprintCreator&7] &f"),
	NOT_PLAYER("&4You are not a Player!"),
	CREATE_CANCEL("&3Create was cancelled!"),
	CREATE_BLUEPRINT("&3Please click on 2 Blocks!"),
	NO_PERMISSION("&4No Permission!"),
	NO_INTERNET("&4Please check your Internet Connection!"),
	NOT_FOUND_TEXTUR("&4%textur not found!"),
	DOWNLOAD_TEXTUR("&5The Texture %texture is downloaded..."),
	TEXTURES_LIST("&5The List of available textures: %list"),
	ERROR("&4Error!"),
	SET_POINT_1("&6Point 1 setted!"),
	SET_POINT_2("&6Point 2 setted!"),
	WAIT_CREATE_BLUEPRINT("&3Create of Blueprint has begun!"),
	BLUEPRINT_CREATED("&5Blueprint created!"),
	TEXTURE_INSTALLED("Texture %texture was installed!"),
	PLUGIN_RELOADED("&3The Plugin was reloaded!");
	
	private final String value;
	public static YamlConfiguration config = null;
	public static File configFile = new File("plugins/BlueprintCreator/messages.yml");
	
	private Lang(final String value) {
		this.value = value;
	}
	
	public String getText() {
		String value = this.getValue();
		if (config != null && config.contains(this.name()))
		{
			value = config.getString(this.name());
		}
		value = ChatColor.translateAlternateColorCodes('&', value);
		return value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public static void load() {
		if (!configFile.exists()) createConfig();
		config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public static void createConfig() {
		YamlConfiguration newConfig = new YamlConfiguration();
		newConfig.options().header("The Messages from BlueprintCreator.");
		newConfig.options().copyHeader(true);
		for (Lang lang : Lang.values())
		{
			String name = lang.name();
			String value = lang.getValue();
			newConfig.set(name, value);
		}
		try {
			newConfig.save(configFile);
		} catch (Exception e) {
			BlueprintPlugin.log.log(Level.WARNING, "Error while save the messages.yml: " + e.getMessage());
		}
	}

}
