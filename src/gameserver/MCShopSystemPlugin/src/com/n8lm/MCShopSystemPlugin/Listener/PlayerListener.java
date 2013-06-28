/*
 * PlayerListener
 * 	Used to handle things in store,
 * 	When Player Login.
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
		String UserName = player.getName();

		if(store.checkStore(UserName)){
			player.sendMessage("MCShop: �������й���ȴδ��������Ʒ!");
			player.sendMessage("MCShop: ���ڳ��Է��ͣ�");

			String[] arg = store.getAllThing(UserName);
			String hashcode,command;
			int space,hash;
			for(String i: arg){
				space = i.indexOf(" ");
				hashcode = i.substring(0, space);
				hash = Integer.valueOf(hashcode);
				command = i.substring(space+1, i.length());
				if(sendItem(command)){
					//TODO sendItem
					try {
						store.deleteCommand(UserName, hash);
						player.sendMessage("MCShop: һ��Ʒ���ͳɹ���");
					} catch (IOException e) {
						MainPlugin.getMainLogger().log(Level.WARNING, "Wrong when deal with User:" + UserName + "'s store thing.");
						MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't delete the command in store file!");
						// WARNING!
						e.printStackTrace();
					}
				}
				else{
					player.sendMessage("MCShop: һ��Ʒ����ʧ�ܣ�");
				}
			}
		}
	}
	
	private boolean sendItem(String arg){
		//TODO sendItem;
		return true;
	}
}