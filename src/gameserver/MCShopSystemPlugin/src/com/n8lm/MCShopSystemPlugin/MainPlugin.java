/**
 * 
 */
package com.n8lm.MCShopSystemPlugin;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import com.n8lm.MCShopSystemPlugin.config.*;

/**
 * @author Alchemist
 *
 */
public final class MainPlugin extends JavaPlugin {
	
	private static Logger logger;
	private static MainPlugin plugin;
	
	private static Settings settings;
	
	@Override
    public void onEnable(){
		
		Boolean needSetup;
		
		needSetup = false;
		plugin = this;
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
			// TODO Insert code to start CommunicationServer
		}
		
		
		this.getLogger().info("MC Shop System is enable");
        // TODO Insert logic to be performed when the plugin is enabled
    }
 
    @Override
    public void onDisable() {
		this.getLogger().info("MC Shop System is disable");
        // TODO Insert logic to be performed when the plugin is disabled
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
    
    
}
