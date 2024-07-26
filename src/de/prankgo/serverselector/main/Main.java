package de.prankgo.serverselector.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.prankgo.serverselector.commands.ServerSelectorCommand;
import de.prankgo.serverselector.config.Messages;
import de.prankgo.serverselector.config.MessagesFile;
import de.prankgo.serverselector.server.ServerList;
import de.prankgo.serverselector.server.ServerSelectorFile;
import de.prankgo.serverselector.server.ServerSelectorGUI;
import de.prankgo.serverselector.utils.ServiceManager;

public class Main extends JavaPlugin {

	public final static String NAME = "ServerSelector", VERSION = "1.1.0", AUTHOR = "daswylliam";
	
	private Messages messages;
	private ServiceManager serviceManager;
	private ServerSelectorGUI serverSelectorGUI;
	
	@Override
	public void onEnable() {
		MessagesFile.setup();
		MessagesFile.get().addDefault("prefix", "&8[&6ServerSelector&8]");
		MessagesFile.get().addDefault("noPermission", "%prefix% &cDazu hast du keine Berechtigung!");
		MessagesFile.get().addDefault("connect", "%prefix% &7Verbinde mit &6%server%&7...");
		MessagesFile.get().addDefault("notOnline", "%prefix% &c%server% &cist nicht Online!");
		MessagesFile.get().addDefault("startService", "%prefix% &7Der Server &6%server% &7wird &agestartet&7.");
		MessagesFile.get().addDefault("stopService", "%prefix% &7Der Server &6%server% &7wird &cgestoppt&7.");
		MessagesFile.get().addDefault("isStarting", "%prefix% &cDer Server &c%server% &cstartet gerade!");
		MessagesFile.get().options().copyDefaults(true);
		MessagesFile.save();
		
 		ServerSelectorFile.setup();
 		ServerSelectorFile.get().addDefault("amount", 1);
 		ServerSelectorFile.get().addDefault("server.1.name", "example-1");
 		ServerSelectorFile.get().addDefault("server.1.permission", "server.example");
 		ServerSelectorFile.get().addDefault("server.1.service", "example");
 		ServerSelectorFile.get().addDefault("server.1.material", "PAPER");
 		ServerSelectorFile.get().options().copyDefaults(true);
 		ServerSelectorFile.save();
		
 		ServerList.init();
 		
		messages = new Messages();
		serviceManager = new ServiceManager(this);
		serverSelectorGUI = new ServerSelectorGUI(this);
		
		
		getCommand("serverselector").setExecutor(new ServerSelectorCommand(this));
		
		Bukkit.getPluginManager().registerEvents(serverSelectorGUI, this);
		
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		getLogger().info(NAME + " v" + VERSION + " von " + AUTHOR + " wurde erfolgreich aktiviert.");
		
		serverSelectorGUI.startUpdating();
	}
	
	
	@Override
	public void onDisable() {
		getLogger().info(NAME + " v" + VERSION + " von " + AUTHOR + " wurde erfolgreich deaktiviert.");
	}
	
	public ServerSelectorGUI getServerSelectorGUI() {
		return serverSelectorGUI;
	}
	
}
