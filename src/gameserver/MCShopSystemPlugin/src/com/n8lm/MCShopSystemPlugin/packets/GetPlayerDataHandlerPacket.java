/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.server.CommunicationHelper;
import com.n8lm.MCShopSystemPlugin.server.PacketHandler;
import com.n8lm.MCShopSystemPlugin.FileOperator.CheckUser;

public class GetPlayerDataHandlerPacket extends PacketHandler {

	/**
	 * @param header
	 */
	public GetPlayerDataHandlerPacket() {
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
		
		//MainPlugin.getMainLogger().log(Level.INFO, "User Account '" + user + "' '" + pass + "' ");
		
		CheckUser player = new CheckUser(user);
		String s="";

		s=s+"Exp:"+player.getExp()+",";
		s=s+"Level:"+player.getLevel()+",";
		s=s+"Exhaustion:"+player.getExhaustion()+",";
		s=s+"FoodLevel:"+player.getFoodLevel()+",";
		s=s+"ExpToLevel:"+player.getExpToLevel();
		
		CommunicationHelper.writeString(out,s);
		
	}
	/*public static void main(String[] arg){
	}*/
}
