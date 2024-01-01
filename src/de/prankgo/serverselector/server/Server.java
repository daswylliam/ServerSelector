package de.prankgo.serverselector.server;

import org.bukkit.Material;

public class Server {

	private String name;
	private String permission;
	private String service;
	private Material material;
	
	public Server(String name, String permission, String service, Material material) {
		this.name = name;
		this.permission = permission;
		this.service = service;
		this.material = material;
	} 
	
	public String getName() {
		return name;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public String getService() {
		return service;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	
}
