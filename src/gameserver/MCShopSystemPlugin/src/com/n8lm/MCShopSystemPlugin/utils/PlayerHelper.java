/*
 * CheckUser Class
 * MUST give USERNAME When created.
 * -> new CheckUser("UserName");
 * 
 * 	hasPassword
 * 	checkPassword
 */
package com.n8lm.MCShopSystemPlugin.utils;


import org.bukkit.entity.Player;
import com.n8lm.MCShopSystemPlugin.MainPlugin;

public class PlayerHelper{
	
	// Password	
	public static boolean isRegistered(String username){
		return MainPlugin.getAccountHandler().hasPassword(username);
	}
	
	public static boolean register(String username, String pass){
		return MainPlugin.getAccountHandler().register(username, pass);
	}

	public static boolean checkPassword(String username, String pass){
		return MainPlugin.getAccountHandler().checkPassword(username,pass);
	}
	
	public static boolean inOnline(String username){
		return (MainPlugin.getBukkitServer().getPlayerExact(username) != null);
	}

	public static Player getPlayer(String username) {
		return MainPlugin.getBukkitServer().getPlayerExact(username);
	}
}