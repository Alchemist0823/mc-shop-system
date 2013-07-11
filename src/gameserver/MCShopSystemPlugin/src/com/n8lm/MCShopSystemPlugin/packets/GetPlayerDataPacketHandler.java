/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.entity.Player;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.server.CommunicationHelper;
import com.n8lm.MCShopSystemPlugin.server.PacketHandler;
import com.n8lm.MCShopSystemPlugin.utils.PlayerHelper;

public class GetPlayerDataPacketHandler extends PacketHandler {

	/**
	 * @param header
	 */
	public GetPlayerDataPacketHandler() {
		super((byte) 4);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.n8lm.MCShopSystemPlugin.server.PacketHandler#onHeaderReceived(java.io.DataInputStream, java.io.DataOutputStream)
	 */
	@Override
	public void onHeaderReceived(DataInputStream in, DataOutputStream out)
			throws IOException {
		// TODO Auto-generated method stub
		String user = CommunicationHelper.readString(in);
		
		MainPlugin.getMainLogger().info("GetPlayer Info '" + user + "'");
		
		String s="";
		if (PlayerHelper.inOnline(user))
		{
			Player player;
			player = PlayerHelper.getPlayer(user);

			s=s+"DisplayName:"+player.getDisplayName()+",";
			s=s+"PlayerTime:"+ player.getPlayerTime()+",";
			s=s+"LastPlayed:"+ player.getLastPlayed()+",";
			s=s+"Level:"+ player.getLevel()+",";
			s=s+"Exp:"+player.getExp()+",";
			s=s+"FoodLevel:"+ player.getFoodLevel()+",";
			s=s+"Health:"+player.getHealth();// +",";
		}
		else
		{
			s="Message:This player is not online";
		}
		
		CommunicationHelper.writeString(out,s);
		
	}
	/*public static void main(String[] arg){
	}*/
}
