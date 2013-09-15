package de.howaner.BlueprintCreator.command;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.howaner.BlueprintCreator.BlueprintManager;
import de.howaner.BlueprintCreator.util.Cache;
import de.howaner.BlueprintCreator.util.Config;
import de.howaner.BlueprintCreator.util.Lang;
import de.howaner.BlueprintCreator.util.Cache.SelectionCache;

public class BlueprintCommand implements CommandExecutor {
	private BlueprintManager manager;
	
	public BlueprintCommand(BlueprintManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (args.length == 0)
		{
			sendHelp(sender);
			return true;
		}
		String aufgabe = args[0];
		//CREATE
		if (aufgabe.equalsIgnoreCase("create"))
		{
			if (!(sender instanceof Player))
			{
				sender.sendMessage(Lang.PREFIX.getText() + Lang.NOT_PLAYER.getText());
				return true;
			}
			Player player = (Player)sender;
			if (!player.hasPermission("BlueprintCreator.create"))
			{
				player.sendMessage(Lang.PREFIX.getText() + Lang.NO_PERMISSION.getText());
				return true;
			}
			if (args.length != 1)
			{
				sendHelp(sender);
				return true;
			}
			if (Cache.hasSelectionCache(player))
			{
				Cache.removeSelection(player);
				player.sendMessage(Lang.PREFIX.getText() + Lang.CREATE_CANCEL.getText());
				return true;
			}
			SelectionCache cache = new SelectionCache(player);
			Cache.setSelection(player, cache);
			//Item erhalten
			manager.giveSelectiontool(player);
			player.sendMessage(Lang.PREFIX.getText() + Lang.CREATE_BLUEPRINT.getText());
			return true;
		}
		//TEXTUR
		else if (aufgabe.equalsIgnoreCase("texture"))
		{
			if (args.length < 2)
			{
				this.sendHelp(sender);
				return true;
			}
			String type = args[1];
			if (type.equalsIgnoreCase("install"))
			{
				if (sender instanceof Player)
				{
					Player player = (Player)sender;
					if (!player.hasPermission("BlueprintCreator.texture.install"))
					{
						player.sendMessage(Lang.PREFIX.getText() + Lang.NO_PERMISSION.getText());
						return true;
					}
				}
				if (args.length != 3)
				{
					sendHelp(sender);
					return false;
				}
				String textur = args[2];
				List<String> texturen = manager.getTexturen();
				if (texturen.isEmpty())
				{
					sender.sendMessage(Lang.PREFIX.getText() + Lang.NO_INTERNET.getText());
					return true;
				}
				if (!texturen.contains(textur))
				{
					sender.sendMessage(Lang.PREFIX.getText() + Lang.NOT_FOUND_TEXTUR.getText().replace("%textur", textur));
					return true;
				}
				manager.downloadTextur(textur);
				sender.sendMessage(Lang.PREFIX.getText() + Lang.DOWNLOAD_TEXTUR.getText().replace("%textur", textur));
				return true;
			}
			else if (type.equalsIgnoreCase("list"))
			{
				if (sender instanceof Player)
				{
					Player player = (Player)sender;
					if (!player.hasPermission("BlueprintCreator.texture.list"))
					{
						player.sendMessage(Lang.PREFIX.getText() + Lang.NO_PERMISSION.getText());
						return true;
					}
				}
				if (args.length != 2)
				{
					sendHelp(sender);
					return false;
				}
				List<String> texturen = manager.getTexturen();
				if (texturen.isEmpty())
				{
					sender.sendMessage(Lang.PREFIX.getText() + Lang.NO_INTERNET.getText());
					return true;
				}
				StringBuilder texturenBuilder = new StringBuilder();
				for (int i=0; i<texturen.size(); i++)
				{
					String textur = texturen.get(i);
					if (i != 0) texturenBuilder.append(", ");
					texturenBuilder.append(textur);
				}
				sender.sendMessage(Lang.PREFIX.getText() + Lang.TEXTURES_LIST.getText().replace("%list", texturenBuilder.toString()));
				return true;
			}
			else
			{
				sendHelp(sender);
				return true;
			}
		}
		//RELOAD
		else if (aufgabe.equalsIgnoreCase("reload"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player)sender;
				if (!player.hasPermission("BlueprintManager.reload"))
				{
					player.sendMessage(Lang.PREFIX.getText() + Lang.NO_PERMISSION.getText());
					return true;
				}
			}
			if (!Config.configFile.exists()) Config.save();
			Config.load();
			Config.save();
			if (!new File("plugins/BlueprintCreator/textur").exists())
			{
				new File("plugins/BlueprintCreator/textur").mkdirs();
				List<String> texturen = manager.getTexturen();
				if (texturen.isEmpty())
				{
					manager.getLogger().info("Error while downloading the Textures!");
					manager.getLogger().info("Check your Internet Connection!");
				}
				else
				{
					manager.downloadTextur(texturen.get(0));
					manager.getLogger().info(Lang.DOWNLOAD_TEXTUR.getText().replace("%texture", texturen.get(0)));
				}
			}
			sender.sendMessage(Lang.PREFIX.getText() + Lang.PLUGIN_RELOADED.getText());
			return true;
		}
		else
		{
			sendHelp(sender);
			return true;
		}
	}
	
	private void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.BLUE + "--- " + ChatColor.GREEN + "Help from /blueprint " + ChatColor.BLUE + "---");
		sender.sendMessage("/blueprint create  " + ChatColor.GOLD + "-" + ChatColor.WHITE + "   Create a Blueprint!");
		sender.sendMessage("/blueprint texture download <Textur>   " + ChatColor.GOLD + "-" + ChatColor.WHITE + "   Download a Textur.");
		sender.sendMessage("/blueprint texture list   " + ChatColor.GOLD + "-" + ChatColor.WHITE + "   Print a List of available Textures.");
		sender.sendMessage("/blueprint reload   " + ChatColor.GOLD + "-" + ChatColor.WHITE + "   Reload the Plugin.");
	}

}
