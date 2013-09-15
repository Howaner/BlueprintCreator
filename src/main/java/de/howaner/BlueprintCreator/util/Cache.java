package de.howaner.BlueprintCreator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Cache {
	
	private static Map<Player, SelectionCache> selectionCaches = new HashMap<Player, SelectionCache>();
	
	public static boolean hasSelectionCache(Player player) {
		return selectionCaches.containsKey(player);
	}
	
	public static SelectionCache getSelection(Player player) {
		return selectionCaches.get(player);
	}
	
	public static void setSelection(Player player, SelectionCache cache) {
		selectionCaches.put(player, cache);
	}
	
	public static void removeSelection(Player player) {
		selectionCaches.remove(player);
	}
	
	public static List<SelectionCache> getSelections() {
		List<SelectionCache> selections = new ArrayList<SelectionCache>();
		for (Entry<Player, SelectionCache> e : selectionCaches.entrySet())
		{
			SelectionCache sel = e.getValue();
			selections.add(sel);
		}
		return selections;
	}
	
	public static class SelectionCache {
		private Player player;
		private Location pos1;
		private Location pos2;
		
		public SelectionCache(Player player) {
			this.player = player;
		}
		
		public Player getPlayer() {
			return this.player;
		}
		
		public Location getPos1() {
			return this.pos1;
		}
		
		public Location getPos2() {
			return this.pos2;
		}
		
		public void setPos1(Location loc) {
			this.pos1 = loc;
		}
		
		public void setPos2(Location loc) {
			this.pos2 = loc;
		}
		
		public Location getMinPoint() {
			return new Location(pos1.getWorld(),
					Math.min(pos1.getX(), pos2.getX()),
                    Math.min(pos1.getY(), pos2.getY()),
                    Math.min(pos1.getZ(), pos2.getZ()));
		}
		
		public Location getMaxPoint() {
			return new Location(pos2.getWorld(),
					Math.max(pos1.getX(), pos2.getX()),
					Math.max(pos1.getY(), pos2.getY()),
					Math.max(pos1.getZ(), pos2.getZ()));
		}
	}

}
