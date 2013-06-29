/*
 * PlayerListener
 * 	Used to handle things in store,
 * 	When Player Login.
 * 
 * TODO: complete sendItem, 
 * 	or use sendItem method of DoCommandPacketHandler;
 * 
 */
package com.n8lm.MCShopSystemPlugin.Listener;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.packets.*;
import com.n8lm.MCShopSystemPlugin.FileOperator.WaitListOperator;

public class PlayerListener implements Listener{
	
	private MainPlugin plugin;
	
	public PlayerListener(MainPlugin plugin){
		this.plugin = plugin;
	}
	
	@EventHandler //EventPriority.NORMAL by default
	public void onPlayerLogin(PlayerLoginEvent event){
		Player player = event.getPlayer();
		String userName = player.getName();
        WaitListOperator store = MainPlugin.getWaitListOperator();
		if(store.checkStore(userName)){
			player.sendMessage("MCShop: 发现您有购买却未发货的物品!");
			player.sendMessage("MCShop: 正在尝试发送！");

			String[] arg = store.getAllThing(userName);
			for(String command: arg){
				if(DoCommandPacketHandler.sendItem(command)){
					if(store.deleteCommand(userName, command)){
						player.sendMessage("MCShop: 一物品发送成功！");
						
					}
					else{
						MainPlugin.getMainLogger().log(Level.WARNING, "Wrong when deal with User:" + userName + "'s store thing.");
						MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't delete the command in store file!");
					}
				}
				else{
					player.sendMessage("MCShop: 一物品发送失败！");
				}
			}
		}
	}
}