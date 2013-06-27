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
import com.n8lm.MCShopSystemPlugin.server.CommunicationServer;
import com.n8lm.MCShopSystemPlugin.FileOperator.PasswdOperator;
import com.n8lm.MCShopSystemPlugin.Listener.PlayerListener; // Your PlayerListener class has never been pushed to our repo

public final class MainPlugin extends JavaPlugin {
	
	private static Logger logger;
	private static MainPlugin plugin;
	private static Server bukkitServer;
	private static CommunicationServer server;
	
	private final PlayerListener playerListener = new PlayerListener(this);
	
	private static Settings settings;
	private static PasswordOperator passwordOperator;
	
	@Override
    public void onEnable(){
		
		Boolean needSetup;
		
		needSetup = false;
		plugin = this;
		bukkitServer = this.getServer();
		logger = this.getLogger();
		
		ConfigHandler configHandler = new ConfigHandler();
		try
		{
			settings = configHandler.loadSettings();
		}
		catch (FileNotFoundException ex)
		{
			configHandler.generateConfig();
			logger.info("Mcshop generated a config file. Go edit it!");
			needSetup = true;
		}
		catch (IOException ex)
		{
			logger.info("Mcshop failed to read your configuration file.");
			logger.log(Level.SEVERE, null, ex);
			return;
		}


		if (needSetup)
		{
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// Start server
		if (settings.isServerActive())
		{
			server = new CommunicationServer();
			server.start();
			// Here we need use server.start() instead of startServer()
			// Because CommunicationServer is subclass of Thread.
			// "start" is a method of Thread it will call "run" method automatically.
		}
		
		// Load File
		passwordOperator = new PasswordOperator();
		//TODO: StoreFile = new StoreOperator();
		
		// Register Listener
		bukkitServer.getPluginManager().registerEvents(playerListener, plugin);
		
		// Register Executor
		getCommand("mcshop").setExecutor(new MCShopCommands(this));

		// TODO Insert logic to be performed when the plugin is enabled
		
		this.getLogger().info("MC Shop System is enable");
    }
 
    @Override
    public void onDisable() {

		if (server != null)
		{
			server.stopServer();
		}
		this.getLogger().info("MC Shop System is disable");
		
        // TODO Insert logic to be performed when the plugin is disabled
		PlayerInteractEvent.getHandlerList().unregister(plugin);
		bukkitServer.getScheduler().cancelTasks(this);
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
	
	public static PasswordOperator getPasswordOperator(){
		return passwordOperator;
	}
    
}
