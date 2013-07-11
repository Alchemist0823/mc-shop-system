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
import org.bukkit.event.player.PlayerJoinEvent;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.FileOperator.WaitListOperator;
import com.n8lm.MCShopSystemPlugin.utils.CommandHelper;

public class PlayerListener implements Listener{
	
	public PlayerListener(){
	}
	
	@EventHandler //EventPriority.NORMAL by default
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		String userName = player.getName();
        WaitListOperator operator = MainPlugin.getWaitListOperator();
		if(operator.isOnWaitList(userName)){
			player.sendMessage("MCShop: 发现您有购买却未发货的物品!");
			player.sendMessage("MCShop: 正在尝试发送！");

			String[] args = operator.getAllCommands(userName);
			for(String command: args){
				if(CommandHelper.sendCommand(command)){
					if(operator.deleteCommand(userName, command)){
						player.sendMessage("MCShop: 一物品发送成功！");
						MainPlugin.getMainLogger().info("Send command successfully");
					}
					else{
						MainPlugin.getMainLogger().log(Level.WARNING, "Wrong when deal with User:" + userName + "'s store thing.");
						MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't delete the command in store file!");
					}
				}
				else{
					player.sendMessage("MCShop: 一物品发送失败！");
					MainPlugin.getMainLogger().info("Send command failed");
				}
			}
		}
	}
}