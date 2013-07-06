/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.packets;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.server.CommunicationHelper;
import com.n8lm.MCShopSystemPlugin.server.PacketHandler;
import com.n8lm.MCShopSystemPlugin.FileOperator.CheckUser;

public class BroadcastPacketHandler extends PacketHandler {

	/**
	 * @param header
	 */
	public BroadcastPacketHandler() {
		super((byte) 6);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.n8lm.MCShopSystemPlugin.server.PacketHandler#onHeaderReceived(java.io.DataInputStream, java.io.DataOutputStream)
	 */
	@Override
	public void onHeaderReceived(DataInputStream in, DataOutputStream out)
			throws IOException {
		// TODO Auto-generated method stub
		String msg = CommunicationHelper.readString(in);
		
		//MainPlugin.getMainLogger().log(Level.INFO, "User Account '" + user + "' '" + pass + "' ");
		
		Bukkit.broadcastMessage(msg);
	}
	/*public static void main(String[] arg){
	}*/
}
