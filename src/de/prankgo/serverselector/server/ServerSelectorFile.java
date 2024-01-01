package de.prankgo.serverselector.server;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.prankgo.serverselector.main.Main;


public class ServerSelectorFile {

	private static File file;
	private static FileConfiguration fileConfig;
	
	public static void setup() {
		file = new File(Bukkit.getServer().getPluginManager().getPlugin(Main.NAME).getDataFolder(), "servers.yml");
		
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				
			}
		}
		
		fileConfig = YamlConfiguration.loadConfiguration(file);
	}
	
	public static FileConfiguration get() {
		return fileConfig;
	}
	
	public static void save() {
		try {
			fileConfig.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error while saving File");
		}
	}
	
	public static void reload() {
		fileConfig = YamlConfiguration.loadConfiguration(file);
	}
	
}