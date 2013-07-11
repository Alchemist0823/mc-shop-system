/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.utils;

import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.MainPlugin;

/**
 * @author Alchemist
 *
 */
public final class CommandHelper {

	/**
	 * 
	 */

	public static boolean sendCommand(String command)
	{
		boolean success;
		try
		{
			success = MainPlugin.getBukkitServer().dispatchCommand(MainPlugin.getBukkitServer().getConsoleSender(), command);
		}
		catch(Exception ex)
		{
			MainPlugin.getMainLogger().log(Level.WARNING, "MCShop caught an exception while running command '"+command+"'", ex);
			success = false;
		}

		if(success)
			MainPlugin.getMainLogger().info("Send command successfully");
		return success;
	}
}
