/*
 * PlayerListener
 * 	Used to handle things in store,
 * 	When Player Login.
 * 
 * TODO: complete sendItem
 * 
 */
package com.n8lm.MCShopSystemPlugin.Listener;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.FileOperator.WaitListOperator;

public class PlayerListener implements Listener{
	
	private MainPlugin plugin;
	private WaitListOperator store = MainPlugin.getWaitListOperator();
	
	public PlayerListener(MainPlugin plugin){
		this.plugin = plugin;
	}
	
	@EventHandler //EventPriority.NORMAL by default
	public void onPlayerLogin(PlayerLoginEvent event){
		Player player = event.getPlayer();
		String userName = player.getName();

		if(store.checkStore(userName)){
			player.sendMessage("MCShop: 发现您有购买却未发货的物品!");
			player.sendMessage("MCShop: 正在尝试发送！");

			String[] arg = store.getAllThing(userName);
			int space;
			for(String command: arg){
				space = i.indexOf(" ");
				if(sendItem(command)){
					try {
						store.deleteCommand(userName, command);
						player.sendMessage("MCShop: 一物品发送成功！");
					} catch (IOException e) {
						MainPlugin.getMainLogger().log(Level.WARNING, "Wrong when deal with User:" + userName + "'s store thing.");
						MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't delete the command in store file!");
						// WARNING!
						e.printStackTrace();
					}
				}
				else{
					player.sendMessage("MCShop: 一物品发送失败！");
				}
			}
		}
	}
	
	private boolean sendItem(String arg){
		//TODO sendItem;
		return true;
	}
}