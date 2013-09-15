package de.howaner.BlueprintCreator.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.howaner.BlueprintCreator.BlueprintManager;
import de.howaner.BlueprintCreator.util.Cache;
import de.howaner.BlueprintCreator.util.Lang;
import de.howaner.BlueprintCreator.util.Cache.SelectionCache;

public class BlueprintListener implements Listener {
	private BlueprintManager manager;
	
	public BlueprintListener(BlueprintManager manager) {
		this.manager = manager;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		final Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		ItemStack item = player.getItemInHand();
		if (item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null || !item.getItemMeta().getDisplayName().equalsIgnoreCase("Selectiontool")) return;
		final SelectionCache selection = Cache.getSelection(player);
		if (selection == null)
		{
			player.sendMessage(Lang.PREFIX.getText() + Lang.ERROR.getText());
			player.setItemInHand(null);
			event.setCancelled(true);
			return;
		}
		if (selection.getPos1() == null)
		{
			selection.setPos1(block.getLocation());
			player.sendMessage(Lang.PREFIX.getText() + Lang.SET_POINT_1.getText());
			event.setCancelled(true);
		}
		else if (selection.getPos2() == null) {
			selection.setPos2(block.getLocation());
			player.sendMessage(Lang.PREFIX.getText() + Lang.SET_POINT_2.getText());
			event.setCancelled(true);
			//Erstellen des Bauplans
			player.sendMessage(Lang.PREFIX.getText() + Lang.WAIT_CREATE_BLUEPRINT.getText());
			Runnable task = new Runnable() {
				@Override
				public void run() {
					manager.createBlueprint(selection);
					Cache.removeSelection(player);
					player.setItemInHand(null);
					player.sendMessage(Lang.PREFIX.getText() + Lang.BLUEPRINT_CREATED.getText());
				}
			};
			new Thread(task, "ServiceThread").start();
		}
		else
		{
			player.sendMessage(Lang.PREFIX.getText() + Lang.ERROR.getText());
			return;
		}
	}

}
