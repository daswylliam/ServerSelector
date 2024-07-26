package de.prankgo.serverselector.server;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.prankgo.serverselector.config.Messages;
import de.prankgo.serverselector.config.MessagesFile;
import de.prankgo.serverselector.main.Main;
import de.prankgo.serverselector.utils.ItemBuilder;
import de.prankgo.serverselector.utils.ServiceManager;

public class ServerSelectorGUI implements Listener {

	private Main plugin;
	private final String GUI_NAME = "ServerSelector", START_STOP_SERVER_GUI_NAME = "Server Manager";
	
	private int taskID;
	private boolean isUpdating;
	
	public ServerSelectorGUI(Main plugin) {
		this.plugin = plugin;
	}
	
	public void startUpdating() {
		if(isUpdating) return;
		isUpdating = true;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				for(Player c : Bukkit.getOnlinePlayers()) {
					if(c.getOpenInventory() != null && c.getOpenInventory().getTitle().equals(GUI_NAME)) {
						update(c, c.getOpenInventory());
					}
				}
				
			}
		}, 50, 50);
	}
	
	public void stopUpdating() {
		if(!isUpdating) return;
		isUpdating = false;
		Bukkit.getScheduler().cancelTask(taskID);
	}
	
	public void open(Player p) {
		int amount = ServerSelectorFile.get().getInt("amount");
		int size = 9*1;
		while(amount > size) {
			size = size + 9;
		}
		
		Inventory inv = Bukkit.createInventory(null, size, GUI_NAME);
		for(int i = 0; i < amount; i++) {
			if(p.hasPermission(ServerList.get()[i].getPermission()) || p.hasPermission("serverselector.join.*")) {
				if(ServiceManager.getInstance().isStarting(ServerList.get()[i].getService())) {
					inv.setItem(i, new ItemBuilder(ServerList.get()[i].getMaterial()).setAmount(1).setLore("§6Startet...").setName("§e" + ServerList.get()[i].getName()).addGlow().build());
				} else if(ServiceManager.getInstance().isServiceOnline(ServerList.get()[i].getService())) {
					inv.setItem(i, new ItemBuilder(ServerList.get()[i].getMaterial()).setAmount(1).setLore("§aOnline " + onlineCount(ServerList.get()[i])).setName("§e" + ServerList.get()[i].getName()).addGlow().build());
				} else
					inv.setItem(i, new ItemBuilder(ServerList.get()[i].getMaterial()).setAmount(1).setLore("§cOffline").setName("§e" + ServerList.get()[i].getName()).build());
			} else
				inv.setItem(i, new ItemBuilder(Material.BARRIER).setAmount(1).setName("§4§lKEINE BERECHTIGUNG!").build());
			
		}
		p.openInventory(inv);
	}
	
	public void update(Player p, InventoryView invView) {
		Inventory inv = invView.getTopInventory();
		for(int i = 0; i < ServerSelectorFile.get().getInt("amount"); i++) {
			if(p.hasPermission(ServerList.get()[i].getPermission()) || p.hasPermission("serverselector.join.*")) {
				if(ServiceManager.getInstance().isStarting(ServerList.get()[i].getService())) {
					inv.setItem(i, new ItemBuilder(ServerList.get()[i].getMaterial()).setAmount(1).setLore("§6Startet...").setName("§e" + ServerList.get()[i].getName()).addGlow().build());
				} else if(ServiceManager.getInstance().isServiceOnline(ServerList.get()[i].getService())) {
					inv.setItem(i, new ItemBuilder(ServerList.get()[i].getMaterial()).setAmount(1).setLore("§aOnline " + onlineCount(ServerList.get()[i])).setName("§e" + ServerList.get()[i].getName()).addGlow().build());
				} else
					inv.setItem(i, new ItemBuilder(ServerList.get()[i].getMaterial()).setAmount(1).setLore("§cOffline").setName("§e" + ServerList.get()[i].getName()).build());
			} else
				inv.setItem(i, new ItemBuilder(Material.BARRIER).setAmount(1).setName("§4§lKEINE BERECHTIGUNG!").build());
			
		}
	}
	
	public void open(Player p, Server server) {
		Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, START_STOP_SERVER_GUI_NAME);
		inv.setItem(0, new ItemBuilder(Material.RED_DYE).setName("§c§lSTOPPEN").setAmount(1).setLore("§7stoppe " + server.getName()).build());
		inv.setItem(2, new ItemBuilder(server.getMaterial()).setName("§5§l" + server.getName()).build());
		inv.setItem(4, new ItemBuilder(Material.LIME_DYE).setName("§a§lSTARTEN").setLore("§7starte " + server.getName()).build());
		p.openInventory(inv);
	}
	
	@EventHandler
	public void handleItemClick(InventoryClickEvent e) {
		if(e.getCurrentItem() == null) return;
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(e.getView().getTitle().equals(GUI_NAME)) {
				e.setCancelled(true);
				if(e.getAction().equals(InventoryAction.PICKUP_ALL)) {
					for(int i = 0; i < ServerSelectorFile.get().getInt("amount"); i++) {
						Server server = ServerList.get()[i];
						String displayname = e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "");
						if(displayname.equals(server.getName())) {
							if(p.hasPermission("serverselector.join.*") || p.hasPermission(server.getPermission())) {
								if(ServiceManager.getInstance().isStarting(server.getService())) {
									p.sendMessage(Messages.getInstance().getIsStarting(server.getName()));
									p.closeInventory();
								} else if(ServiceManager.getInstance().isServiceOnline(server.getService())) {
									p.sendMessage(Messages.getInstance().getConnect(server.getName()));
									ServiceManager.getInstance().connect(server.getService(), p);
									p.closeInventory();
								} else {
									p.sendMessage(Messages.getInstance().getNotOnline(server.getName()));
									p.closeInventory();
								}
								
							} else {
								p.sendMessage(Messages.getInstance().getNoPermissionMessage());
								p.closeInventory();
							}
								
							
						}
						
						
					}
				} else if(e.getAction().equals(InventoryAction.PICKUP_HALF)) {
					e.setCancelled(true);
					for(int i = 0; i < ServerSelectorFile.get().getInt("amount"); i++) {
						Server server = ServerList.get()[i];
						String displayname = e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "");
						if(displayname.equals(server.getName())) {
							if(p.hasPermission("serverselector.manage")) {
								open(p, server);
							} else {
								if(p.hasPermission("serverselector.join.*") || p.hasPermission(server.getPermission())) {
									if(ServiceManager.getInstance().isStarting(server.getService())) {
										p.sendMessage(Messages.getInstance().getIsStarting(server.getName()));
										p.closeInventory();
									} else if(ServiceManager.getInstance().isServiceOnline(server.getService())) {
										p.sendMessage(Messages.getInstance().getConnect(server.getName()));
										
										ServiceManager.getInstance().connect(server.getService(), p);

										p.closeInventory();
									} else {
										p.sendMessage(Messages.getInstance().getNotOnline(server.getName()));
										p.closeInventory();
									}
									
								} else {
									p.sendMessage(Messages.getInstance().getNoPermissionMessage());
									p.closeInventory();
								}
							}
						}
					}
				}
			} else if(e.getView().getTitle().equals(START_STOP_SERVER_GUI_NAME)) {
				e.setCancelled(true);
				Server selectedServer = null;
				for(int i = 0; i < ServerSelectorFile.get().getInt("amount"); i++) {
					Server server = ServerList.get()[i];
					String displayname = p.getOpenInventory().getItem(2).getItemMeta().getDisplayName().replace("§5§l", "");
					if(displayname.equals(server.getName())) {
						selectedServer = server;
					}
				}
				
				if(selectedServer == null) return;
				
				switch(e.getCurrentItem().getType()) {
				case LIME_DYE:
					System.out.println("0");
					p.closeInventory();
					ServiceManager.getInstance().start(selectedServer.getService());
					p.sendMessage(Messages.getInstance().getStartService(selectedServer.getName()));
					break;
				case RED_DYE:
					p.closeInventory();
					ServiceManager.getInstance().stop(selectedServer.getService());
					p.sendMessage(Messages.getInstance().getStopService(selectedServer.getName()));
					break;
					
					
					default:
						break;
				}
			}
		}
	}
	
	public String onlineCount(Server server) {
		return "§8(§7" + ServiceManager.getInstance().getOnlinePlayerCount(server.getService()) + "§8/§7" + ServiceManager.getInstance().getMaxPlayerCount(server.getService()) + "§8)";
	}
	
}
