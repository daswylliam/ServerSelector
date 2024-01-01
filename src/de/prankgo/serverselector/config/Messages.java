package de.prankgo.serverselector.config;

import org.bukkit.ChatColor;

public class Messages {
	
	private static Messages instance;
	
	private String prefix;
	private String noPermissionMessage;
	private String connect;
	private String notOnline;
	private String startService;
	private String stopService;
	
	public Messages() {
		instance = this;
		load();
	}
	
	public void load() {
		prefix = color(MessagesFile.get().getString("prefix"));
		noPermissionMessage = color(MessagesFile.get().getString("noPermission").replaceAll("%prefix%", prefix));
		connect = color(MessagesFile.get().getString("connect").replaceAll("%prefix%", prefix));
		notOnline = color(MessagesFile.get().getString("notOnline").replaceAll("%prefix%", prefix));
		startService = color(MessagesFile.get().getString("startService").replaceAll("%prefix%", prefix));
		stopService = color(MessagesFile.get().getString("stopService").replaceAll("%prefix%", prefix));
	}
	
	public String color(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getNoPermissionMessage() {
		return noPermissionMessage;
	}
	
	public String getConnect(String server) {
		return connect.replaceAll("%server%", server);
	}
	
	public String getNotOnline(String server) {
		return notOnline.replaceAll("%server%", server);
	}
	
	public String getStartService(String server) {
		return startService.replaceAll("%server%", server);
	}
	
	public String getStopService(String server) {
		return stopService.replaceAll("%server%", server);
	}
	
	public static Messages getInstance() {
		return instance;
	}
	
}
