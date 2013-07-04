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

/**
 * @author Alchemist
 *
 */
public class CheckPlayerPacketHandler extends PacketHandler {

	/**
	 * @param header
	 */
	public CheckPlayerPacketHandler() {
		super((byte) 2);
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
		String pass = CommunicationHelper.readString(in);
		
		MainPlugin.getMainLogger().log(Level.INFO, "User Account '" + user + "' '" + pass + "' ");
		
		CheckUser player = new CheckUser(user);
		if(player.checkPassword(pass)) out.writeInt(1);
		else out.writeInt(0);
	}

}
