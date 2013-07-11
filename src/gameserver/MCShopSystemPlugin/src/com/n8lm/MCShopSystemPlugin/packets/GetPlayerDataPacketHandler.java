/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.server.CommunicationHelper;
import com.n8lm.MCShopSystemPlugin.server.PacketHandler;
import com.n8lm.MCShopSystemPlugin.FileOperator.CheckUser;

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
		
		CheckUser player = new CheckUser(user);
		String s="";

		s=s+"DisplayName:"+player.getPlayer().getDisplayName()+",";
		s=s+"PlayerTime:"+ player.getPlayer().getPlayerTime()+",";
		s=s+"LastPlayed:"+ player.getPlayer().getLastPlayed()+",";
		s=s+"Level:"+ player.getPlayer().getLevel()+",";
		s=s+"Exp:"+player.getPlayer().getExp()+",";
		s=s+"FoodLevel:"+ player.getPlayer().getFoodLevel()+",";
		s=s+"Health:"+player.getPlayer().getHealth();// +",";
		
		CommunicationHelper.writeString(out,s);
		
	}
	/*public static void main(String[] arg){
	}*/
}
