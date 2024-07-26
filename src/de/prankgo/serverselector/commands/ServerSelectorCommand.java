package de.prankgo.serverselector.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.prankgo.serverselector.config.Messages;
import de.prankgo.serverselector.main.Main;
import de.prankgo.serverselector.utils.ServiceManager;

public class ServerSelectorCommand implements CommandExecutor {

	private Main plugin;
	
	public ServerSelectorCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(p.hasPermission("serverselector.use")) {
				plugin.getServerSelectorGUI().open(p);
			} else
				p.sendMessage(Messages.getInstance().getNoPermissionMessage());
		}
		return false;
	}

}
