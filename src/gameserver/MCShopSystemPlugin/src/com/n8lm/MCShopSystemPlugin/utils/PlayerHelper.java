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
	public static boolean hasPassword(String userName){
		return MainPlugin.getPasswordOperator().hasPassword(userName);
	}

	public static boolean checkPassword(String userName, String pass){
		return MainPlugin.getPasswordOperator().checkPassword(userName,pass);
	}
	
	public static boolean inOnline(String userName){
		return (MainPlugin.getBukkitServer().getPlayerExact(userName) != null);
	}

	public static Player getPlayer(String userName) {
		return MainPlugin.getBukkitServer().getPlayerExact(userName);
	}
}