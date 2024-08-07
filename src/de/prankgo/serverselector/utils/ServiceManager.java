package de.prankgo.serverselector.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceLifeCycle;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.driver.service.property.ServiceProperty;
import de.dytanic.cloudnet.ext.bridge.BridgeHelper;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import de.dytanic.cloudnet.ext.bridge.node.CloudNetBridgeModule;
import de.dytanic.cloudnet.ext.bridge.server.BridgeServerHelper;
import de.prankgo.serverselector.main.Main;

public class ServiceManager {

	private static ServiceManager instance;

	private Main plugin;
	
	public ServiceManager(Main plugin) {
		this.plugin = plugin;
		instance = this;
	}
	
	public boolean isServiceOnline(String serviceName) {
		boolean value = false;
		for(ServiceInfoSnapshot snapshot : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
				.filter(ServiceInfoSnapshot::isConnected)
				.filter(it -> it.getLifeCycle() == ServiceLifeCycle.RUNNING)
				.filter(it -> it.getServiceId().getName().equals(serviceName))
				.collect(Collectors.toList())) {
			value = true;
		}
		
		return value;
	}

	public void startFromTask(String taskName) {
		ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(taskName);
		if(serviceTask != null) {
			ServiceInfoSnapshot snapshot = CloudNetDriver.getInstance().getCloudServiceFactory().createCloudService(serviceTask);
			snapshot.provider().startAsync();
		}
	}
	
	public void start(String serviceName) {
		
		String taskName = serviceName.split("-")[0];
		String serviceNumber = serviceName.split("-")[1];
		ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(taskName);
		if(serviceTask != null) {
			try {
				int serviceID = Integer.parseInt(serviceNumber);
				ServiceInfoSnapshot snapshot = CloudNetDriver.getInstance().getCloudServiceFactory().createCloudService(serviceTask, serviceID);
				snapshot.provider().startAsync();
			} catch(NumberFormatException e) {
				plugin.getLogger().info("Fehler bei der Config. Bitte tragen sie den Service beispielsweise wie folgt in die Config ein: Example-1 or Lobby-2");
			}
			
		} else
			plugin.getLogger().info("Service wurde nicht gefunden");

		
	}
	
	public void stop(String serviceName) {
		for(ServiceInfoSnapshot snapshot : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
				.filter(ServiceInfoSnapshot::isConnected)
				.filter(it -> it.getLifeCycle() == ServiceLifeCycle.RUNNING)
				.filter(it -> it.getServiceId().getName().equals(serviceName))
				.collect(Collectors.toList())) {
			snapshot.provider().stopAsync();
		}
	}
	
	public Boolean isStarting(String serviceName) {
		for(ServiceInfoSnapshot snapshot : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
				.filter(ServiceInfoSnapshot::isConnected)
				.filter(it -> it.getServiceId().getName().equals(serviceName))
				.collect(Collectors.toList())) {
			return snapshot.getProperty(BridgeServiceProperty.IS_STARTING).orElse(false);
			
		}
		return false;
	}
	
	public int getOnlinePlayerCount(String serviceName) {
		for(ServiceInfoSnapshot snapshot : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
				.filter(ServiceInfoSnapshot::isConnected)
				.filter(it -> it.getServiceId().getName().equals(serviceName))
				.collect(Collectors.toList())) {
			return snapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0);
		} 
		return 0;
	}
	
	public int getMaxPlayerCount(String serviceName) {
		for(ServiceInfoSnapshot snapshot : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
				.filter(ServiceInfoSnapshot::isConnected)
				.filter(it -> it.getServiceId().getName().equals(serviceName))
				.collect(Collectors.toList())) {
			return snapshot.getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(0);
		} 
		return 0;
	}
	
	public ServiceProperty<Boolean> getState(String serviceName) {
		for(ServiceInfoSnapshot snapshot : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
				.filter(ServiceInfoSnapshot::isConnected)
				.filter(it -> it.getServiceId().getName().equals(serviceName))
				.collect(Collectors.toList())) {
			if(snapshot.getProperty(BridgeServiceProperty.IS_ONLINE).orElse(false)) {
				return BridgeServiceProperty.IS_ONLINE;
			} else if(snapshot.getProperty(BridgeServiceProperty.IS_STARTING).orElse(false)) {
				return BridgeServiceProperty.IS_STARTING;
			} else
				return null;
			
		}
		return null;
	}
	
	public void connect(String serviceName, Player p) {
	    try {
		    ByteArrayOutputStream b = new ByteArrayOutputStream();
		    DataOutputStream out = new DataOutputStream(b);
			out.writeUTF("Connect");
		    out.writeUTF(serviceName);
		    p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
		    b.close();
		    out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ServiceManager getInstance() {
		return instance;
	}
	
}
