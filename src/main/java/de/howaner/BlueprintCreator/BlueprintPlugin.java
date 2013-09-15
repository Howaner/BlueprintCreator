package de.howaner.BlueprintCreator;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.howaner.BlueprintCreator.util.Lang;

public class BlueprintPlugin extends JavaPlugin {
	
	public static Logger log;
	private static BlueprintManager manager;
	
	@Override
	public void onLoad() {
		log = this.getLogger();
		manager = new BlueprintManager(this);
	}
	
	@Override
	public void onEnable() {
		manager.onEnable();
		log.info(Lang.PLUGIN_ENABLED.getText());
	}
	
	@Override
	public void onDisable() {
		manager.onDisable();
		log.info(Lang.PLUGIN_DISABLED.getText());
	}
	
	public static BlueprintPlugin getPlugin() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("BlueprintCreator");
		if (plugin == null || !(plugin instanceof BlueprintPlugin)) return null;
		return (BlueprintPlugin)plugin;
	}
	
	public static BlueprintManager getManager() {
		return manager;
	}

}
