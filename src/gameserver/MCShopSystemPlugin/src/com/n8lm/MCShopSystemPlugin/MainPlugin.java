/**
 * @Author Alchemist 
 */
package com.n8lm.MCShopSystemPlugin;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.n8lm.MCShopSystemPlugin.config.*;
import com.n8lm.MCShopSystemPlugin.listener.PlayerListener;
import com.n8lm.MCShopSystemPlugin.listener.UserCommandListener;
import com.n8lm.MCShopSystemPlugin.operator.*;
import com.n8lm.MCShopSystemPlugin.server.CommunicationServer;

public final class MainPlugin extends JavaPlugin {
	
	private static Logger logger;
	private static MainPlugin plugin;
	private static Server bukkitServer;
	private static CommunicationServer server;

	private final PlayerListener playerListener = new PlayerListener();
	private final UserCommandListener userCommandListener = new UserCommandListener();
	
	private static Settings settings;
	private static AccountHandler accountHandler;
	private static WaitListOperator waitListOperator;
	
	@Override
    public void onEnable(){
		
		Boolean needSetup;
		
		needSetup = false;
		plugin = this;
		bukkitServer = this.getServer();
		logger = this.getLogger();
		
		ConfigHandler configHandler = new BukkitConfigHandler();
		if(configHandler.hasConfig())
		{
			settings = configHandler.loadSettings();
		}
		else
		{
			configHandler.generateConfig();
			if(configHandler.hasConfig())
			{
				settings = configHandler.loadSettings();
			}
			else
			{
				logger.log(Level.SEVERE,"Configuration File Problem. Can not setup!");
				needSetup = true;
			}
		}


		if (needSetup)
		{
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		// Load Account Handler
		try{
			if(settings.getMode().equals(Settings.CUSTOM_MODE))
			{
				logger.info("Enable Custom Account Handler");
				accountHandler = new CustomAccountHandler();
			}
			else if(settings.getMode().equals(Settings.AUTHME_MODE))
			{
				logger.info("Enable Authme Account Handler");
				accountHandler = new AuthmeAccountHandler();
			}
			else if(settings.getMode().equals(Settings.XAUTH_MODE))
			{
				logger.info("Enable XAuth Account Handler");
				accountHandler = new CustomAccountHandler();
			}
			// TODO: add the XAuth function
		}
		catch (IOException e){
			logger.info("Mcshop failed to load account file.");
			needSetup = true;
		}
		
		// Load WaitList File
		try{
			waitListOperator = new WaitListOperator();
		}
		catch (IOException e){
			logger.info("Mcshop failed to load waitlist.dat file.");
			needSetup = true;
		}

		// Start server
		if (settings.isServerActive())
		{
			server = new CommunicationServer();
			server.start();
		}
		
		
		// Register Listener
		bukkitServer.getPluginManager().registerEvents(this.playerListener, plugin);
		bukkitServer.getPluginManager().registerEvents(this.userCommandListener, plugin);
		
		// Register Executor
		MCShopCommandExecutor mcshopCommandExecutor = new MCShopCommandExecutor();
		getCommand("mymcshop").setExecutor(mcshopCommandExecutor);
		

		// TODO Insert logic to be performed when the plugin is enabled
		
		this.getLogger().info("MC Shop System is enable");
    }
 
    @Override
    public void onDisable() {

		if (server != null)
		{
			server.stopServer();
		}
		PlayerInteractEvent.getHandlerList().unregister(plugin);
		
        // TODO Insert logic to be performed when the plugin is disabled
		bukkitServer.getScheduler().cancelTasks(this);
		
		this.getLogger().info("MC Shop System has been disabled");
    }
    
    public static MainPlugin getInstance()
    {
    	return plugin;
    }
    
    public static Logger getMainLogger()
    {
    	return logger;
    }

	public static Settings getSettings() {
		return settings;
	}

	public static Server getBukkitServer() {
		return bukkitServer;
	}

	public static CommunicationServer getCommunicationServer() {
		return server;
	}
	
	public static AccountHandler getAccountHandler(){
		return accountHandler;
	}
	
	public static WaitListOperator getWaitListOperator(){
		return waitListOperator;
	}
    
}
