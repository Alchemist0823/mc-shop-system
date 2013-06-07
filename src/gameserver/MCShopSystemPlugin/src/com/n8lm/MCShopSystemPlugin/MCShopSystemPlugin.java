/**
 * 
 */
package com.n8lm.MCShopSystemPlugin;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Alchemist
 *
 */
public final class MCShopSystemPlugin extends JavaPlugin {
	
	@Override
    public void onEnable(){
		this.getLogger().info("MC Shop System is enable");
        // TODO Insert logic to be performed when the plugin is enabled
    }
 
    @Override
    public void onDisable() {
		this.getLogger().info("MC Shop System is disable");
        // TODO Insert logic to be performed when the plugin is disabled
    }
}
