/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.server.CommunicationHelper;
import com.n8lm.MCShopSystemPlugin.server.PacketHandler;

public class GetServerDataPacketHandler extends PacketHandler {

	/**
	 * @param header
	 */
	public GetServerDataPacketHandler() {
		super((byte) 5);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.n8lm.MCShopSystemPlugin.server.PacketHandler#onHeaderReceived(java.io.DataInputStream, java.io.DataOutputStream)
	 */
	@Override
	public void onHeaderReceived(DataInputStream in, DataOutputStream out)
			throws IOException {
		// TODO Auto-generated method stub
		
		MainPlugin.getMainLogger().log(Level.INFO, "Get Server Data");
		
		Player[] players = MainPlugin.getBukkitServer().getOnlinePlayers();
		String s="";

		s += "OnlinePlayersNumber:" + players.length + ",";
		s += "OnlinePlayers:";
		for(Player x:players){
			s += x.getName() + "|";
		}
		s = s.substring(0,s.length()-1);
		s += ",";
		
		Plugin[] plugins = MainPlugin.getBukkitServer().getPluginManager().getPlugins();
		s += "PluginsNumber:"+plugins.length+",";
		s += "Plugins:";
		
		for(Plugin x:plugins){
			s += x.getName() + "|";
		}
		s = s.substring(0,s.length()-1);
		//s += ",";
		CommunicationHelper.writeString(out,s);
		
	}
	/*public static void main(String[] arg){
	}*/
}

