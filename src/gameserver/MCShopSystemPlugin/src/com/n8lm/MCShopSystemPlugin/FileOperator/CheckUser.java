/*
 * CheckUser Class
 * MUST give USERNAME When created.
 * -> new CheckUser("UserName");
 * 
 * 	hasPassword
 * 	checkPassword
 */
package com.n8lm.MCShopSystemPlugin.FileOperator;


import org.bukkit.entity.Player;
import com.n8lm.MCShopSystemPlugin.MainPlugin;

public class CheckUser{
	
	private String userName;
	private Player player;
	private static PasswordOperator Passwd = MainPlugin.getPasswordOperator();
	
	public CheckUser(String userName){
		this.userName = userName;
		player = MainPlugin.getBukkitServer().getPlayer(userName);
	}
	
	// Password
	
	public boolean hasPassword(){
		return Passwd.hasPasswd(userName);
	}

	public boolean checkPassword(String pass){
		return Passwd.checkPasswd(userName,pass);
	}

	public Player getPlayer() {
		return player;
	}
}