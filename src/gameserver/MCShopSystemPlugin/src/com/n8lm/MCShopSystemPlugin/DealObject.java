package com.n8lm.MCShopSystemPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/*
 * The Object being Dealed.
 * 
 * MUST be assigned when inited:
 * 	String	Player Name,
 * 	String	type of item,
 * 	String	itemID or permissionName
 * 	int		how much the item bought.
 * 
 */


public final class DealObject{


	static enum WHAT{
		item,
		house,
		permission,
		coin
	}
	
	static int ID_LOW = 0; // LOWER LIMIT OF ITEM ID 
	static int ID_HIGH = 1280; // UPPER LIMIT OF ITEM ID

	// Prefix of Log
	private String prefix = "[Deal] ";
	
	// The Player who buy
	private Player player;
	
	// The type of item bought
	private WHAT what;
	
	// The name of permission bought, "" if unavailable
	private String permissionName;
	
	// The ID of item or house, null if can't use.
	private int itemID;
	
	// How much item or coin bought.
	private int itemMuch;
	
	//Init
	public DealObject(String user, String what, String itemID, int itemMuch) { //throws Exception{
		
		//Load Player
		this.player = Bukkit.getServer().getPlayer(user);
		if(this.player == null){
			this.player = (Player) Bukkit.getOfflinePlayer(user);
			if(player == null){
				//Load Player Failed!
				MainPlugin.getMainLogger().severe( prefix + "Failed to Load Player : " + user);
				//throw new Exception();
			}
			MainPlugin.getMainLogger().info(prefix + "Load offline player " + player.getName());
		}
		else MainPlugin.getMainLogger().info(prefix + "Load online player " + this.player.getName());
		
		try{
			this.what = WHAT.valueOf(what);
		}
		catch(IllegalArgumentException ex){
			MainPlugin.getMainLogger().severe(prefix + "Failed to load WHAT : " + what);
			//throw new Exception();
		}
		
		if(this.what == WHAT.permission)
			this.permissionName = itemID;
		else if(itemID.length() > 0){
			this.itemID = Integer.parseInt(itemID);
			if(!(this.itemID >= ID_LOW && this.itemID <= ID_HIGH)){
				MainPlugin.getMainLogger().severe(prefix + "Bought item's ID is out of range! ID: " + this.itemID);
				//throw new Exception();
			}
		}
		
		this.itemMuch = itemMuch;
	}
	
	//get
	public Player getPlayer(){
		return player;
	}
	public WHAT getWhat(){
		return what;
	}
	public int getItemID(){
		return itemID;
	}
	public String getPermissionName(){
		return permissionName;
	}
	public int getItemMuch(){
		return itemMuch;
	}

}