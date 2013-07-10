/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.server.CommunicationHelper;
import com.n8lm.MCShopSystemPlugin.server.PacketHandler;
import com.n8lm.MCShopSystemPlugin.FileOperator.CheckUser;

public class GetServerDataHandlerPacket extends PacketHandler {

	/**
	 * @param header
	 */
	public GetServerDataHandlerPacket() {
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
		
		Player[] players = MainPlugin.getInstance().getServer().getOnlinePlayers();
		String s="";

		s=s+"OnlinePlayersNumber:"+players.length+",";
		s=s+"OnlinePlayersList:";
		for(Player x:players){
			s=s+x.getDisplayName()+";";
		}
		s=s+",";
		s=s+"ServerId:"+MainPlugin.getInstance().getServer().getServerId()+",";
		s=s+"ServerName:"+MainPlugin.getInstance().getServer().getServerName();
		CommunicationHelper.writeString(out,s);
		
	}
	/*public static void main(String[] arg){
	}*/
}

