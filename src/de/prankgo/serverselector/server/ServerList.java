package de.prankgo.serverselector.server;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ServerList {

	private static Server[] server;
	
	public static void init() {
		FileConfiguration config = ServerSelectorFile.get();
		int amount = config.getInt("amount");
		server = new Server[amount];
		for(int i = 1; i <= amount; i++) {
			server[i - 1] = new Server(config.getString("server." + i + ".name"), config.getString("server." + i + ".permission"), config.getString("server." + i + ".service"), Material.getMaterial(config.getString("server." + i + ".material")));
		}
	}
	
	public static Server[] get() {
		return server;
	}
	
}
