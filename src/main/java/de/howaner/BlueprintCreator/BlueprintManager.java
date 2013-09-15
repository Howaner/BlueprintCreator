package de.howaner.BlueprintCreator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.howaner.BlueprintCreator.command.BlueprintCommand;
import de.howaner.BlueprintCreator.listener.BlueprintListener;
import de.howaner.BlueprintCreator.util.Cache.SelectionCache;
import de.howaner.BlueprintCreator.util.Config;
import de.howaner.BlueprintCreator.util.Lang;

public class BlueprintManager {
	
	public BlueprintPlugin p;

	public BlueprintManager(BlueprintPlugin plugin) {
		this.p = plugin;
	}

	public void onEnable() {
		//Config
		if (!Config.configFile.exists()) Config.save();
		Config.load();
		Config.save();
		//Language
		Lang.load();
		
		Bukkit.getPluginManager().registerEvents(new BlueprintListener(this), this.p);
		this.p.getCommand("blueprint").setExecutor(new BlueprintCommand(this));
		//Texturen downloaden
		if (!new File("plugins/BlueprintCreator/textur").exists())
		{
			new File("plugins/BlueprintCreator/textur").mkdirs();
			List<String> texturen = this.getTexturen();
			if (texturen.isEmpty())
			{
				this.getLogger().info("Error while downloading the Textures!");
				this.getLogger().info("Check your Internet Connection!");
			}
			else
			{
				this.downloadTextur(texturen.get(0));
				this.getLogger().info(Lang.DOWNLOAD_TEXTUR.getText().replace("%texture", texturen.get(0)));
			}
		}
	}

	public void onDisable() {
		
	}
	
	public Logger getLogger() {
		return BlueprintPlugin.log;
	}
	
	public ItemStack giveSelectiontool(Player player) {
		ItemStack item = new ItemStack(Material.STONE_AXE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Selectiontool");
		List<String> lore = new ArrayList<String>();
		lore.add("Click on two Points!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		return item;
	}
	
	public void createBlueprint(SelectionCache selection) {
		if (selection == null) return;
		Location min = selection.getMinPoint();
		Location max = selection.getMaxPoint();
		int width = Config.BlockWidth * (max.getBlockX() - min.getBlockX()) + Config.BlockWidth;
		int height = Config.BlockHeight * (max.getBlockZ() - min.getBlockZ()) + Config.BlockHeight;
		
		//Bilder erstellen
		File texturFolder = new File("plugins/BlueprintCreator/textur");
		if (!texturFolder.exists()) texturFolder.mkdirs();
		int hoehe = 1;
		for (int y = min.getBlockY(); y <= max.getBlockY(); y++)
		{
			BufferedImage image = new BufferedImage(width, height, BufferedImage.SCALE_DEFAULT);
			Graphics2D graphic = image.createGraphics();
			graphic.setBackground(Color.WHITE);
			int reihe = 0;
			for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++)
			{
				int spalte = 0;
				for (int x = min.getBlockX(); x <= max.getBlockX(); x++)
				{
					Block block = min.getWorld().getBlockAt(x, y, z);
					Image blockImage;
					try {
						if (new File(texturFolder, block.getType().name() + "-" + String.valueOf(block.getData()) + ".png").exists())
							blockImage = ImageIO.read(new File(texturFolder, block.getType().name() + "-" + String.valueOf(block.getData()) + ".png"));
						else if (new File(texturFolder, block.getType().name() + "-" + String.valueOf(block.getData()) + ".jpg").exists())
							blockImage = ImageIO.read(new File(texturFolder, block.getType().name() + "-" + String.valueOf(block.getData()) + ".jpg"));
						else if (new File(texturFolder, block.getType().name() + ".png").exists())
							blockImage = ImageIO.read(new File(texturFolder, block.getType().name() + ".png"));
						else if (new File(texturFolder, block.getType().name() + ".jpg").exists())
							blockImage = ImageIO.read(new File(texturFolder, block.getType().name() + ".jpg"));
						else
							throw new Exception();
					} catch (Exception e) {
						blockImage = new BufferedImage(Config.BlockWidth, Config.BlockHeight, BufferedImage.TYPE_INT_ARGB);
						((BufferedImage)blockImage).createGraphics().drawString(block.getType().name(), 0, 0);
					}
					//blockImage = blockImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
					graphic.drawImage(blockImage, Config.BlockWidth * spalte, Config.BlockHeight * reihe, Config.BlockWidth, Config.BlockHeight, null);
					//Linie machen
					if (Config.LinienEnabled && spalte != 0) graphic.drawLine(Config.BlockWidth * spalte, 0, Config.BlockWidth * spalte, height);
					spalte++;
				}
				if (Config.LinienEnabled && reihe != 0) graphic.drawLine(0, Config.BlockHeight * reihe, width, Config.BlockHeight * reihe);
				reihe++;
			}
			File outputFolder = new File("plugins/BlueprintCreator/blueprint");
			if (outputFolder.exists()) outputFolder.delete();
			outputFolder.mkdirs();
			try {
				ImageIO.write(image, "png", new File(outputFolder, hoehe + ".png"));
			} catch (IOException e) {
				this.getLogger().warning(Lang.ERROR.getText());
				e.printStackTrace();
			}
			hoehe++;
		}
	}
	
	public void downloadTextur(final String texturName) {
		final String webZip = "http://dl.howaner.de/BlueprintCreator/textures/" + texturName + ".zip".replace(" ", "%20");
		final File of = new File("plugins/BlueprintCreator/textur/download.zip");
		if (of.getParentFile().exists())
		{
			for (File file : of.getParentFile().listFiles())
			{
				file.delete();
			}
		}
		else
		{
			of.getParentFile().mkdirs();
		}
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					//Download
					OutputStream os = new FileOutputStream(of);
					URL url = new URL(webZip);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.connect();
					int responseCode = conn.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK)
					{
						byte tmp_buffer[] = new byte[4096];
						InputStream is = conn.getInputStream();
						int n;
						while ((n = is.read(tmp_buffer)) > 0)
						{
							os.write(tmp_buffer, 0, n); 
							os.flush();
						}
					}
					os.close();
					//Entpacken
					unzip(of);
					//Zip löschen
					of.delete();
					getLogger().info(Lang.TEXTURE_INSTALLED.getText().replace("%texture", texturName));
				} catch (Exception e) {
					getLogger().warning("Failed to Install the Textur " + texturName + ": " + e.getMessage());
				}
			}
		};
		new Thread(task, "DownloadTextur").start();
	}
	
	public List<String> getTexturen() {
		List<String> texturen = new ArrayList<String>();
		try {
			URL url = new URL("http://dl.howaner.de/BlueprintCreator/textures/list.txt");
			InputStreamReader isr = new InputStreamReader(url.openConnection().getInputStream());
			BufferedReader br = new BufferedReader(isr);
			
			String line = "";
			while ((line = br.readLine()) != null)
			{
				if (line.isEmpty()) continue;
				line = line.replace("\n", "");
				texturen.add(line);
			}
			br.close();
			isr.close();
		} catch (Exception e) {}
		return texturen;
	}
	
	private void unzip(File archive) throws Exception {
		ZipEntry entry;
		ZipFile zipin = new ZipFile(archive);
		Enumeration<? extends ZipEntry> entries = zipin.entries();
		byte[] buffer = new byte[16384];
		int len;
		while (entries.hasMoreElements()) {
			entry = (ZipEntry) entries.nextElement();
			String entryFileName = entry.getName();
			entryFileName = entry.getName().substring(entry.getName().lastIndexOf("/")+1);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(archive.getParentFile(),entryFileName)));
			BufferedInputStream bis = new BufferedInputStream(zipin.getInputStream(entry));
			while ((len = bis.read(buffer)) > 0) {
				bos.write(buffer,0,len);
			}
			bos.flush();
			bos.close();
			bis.close();
		}
		zipin.close();
	}

}
