/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.PacketHandler;
import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.server.CommunicationHelper;

/**
 * @author Alchemist
 *
 */
public class DoCommandPacketHandler extends PacketHandler {

	public DoCommandPacketHandler()
	{
		super((byte) 0x01);
	}

	@Override
	public void onHeaderReceived(DataInputStream in, DataOutputStream out) throws IOException {
		// TODO Auto-generated method stub
		String command = CommunicationHelper.readString(in);
		
		boolean success;
		try
		{
			success = MainPlugin.getBukkitServer().dispatchCommand(MainPlugin.getBukkitServer().getConsoleSender(), command);
		}
		catch(Exception ex)
		{
			if(MainPlugin.getSettings().isDebugMode())
			{
				MainPlugin.getMainLogger().log(Level.WARNING, "MCShop caught an exception while running command '"+command+"'", ex);
			}
			success = false;
		}
		if (success)
		{
			out.writeInt(1);
		}
		else
		{
			out.writeInt(0);
		}
	}

}
