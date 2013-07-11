package com.n8lm.MCShopSystemPlugin.server;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.packets.*;

import java.util.HashMap;

/**
 * @author Alchemist
 *
 */
public class PacketManager {

	public static HashMap<Byte, PacketHandler> packetHandlers = new HashMap<Byte, PacketHandler>();
	

	/**
	 * Add PacketHandler
	 * @param PacketHandler customhandler
	 */
	public static void addPacketHandler(PacketHandler wph)
	{
		if(packetHandlers.containsKey(wph.getHeader()))
			MainPlugin.getMainLogger().info("Error PacketHandler" + wph.getHeader());
		else packetHandlers.put(wph.getHeader(), wph);
	}
	
	/**
	 *  Load all the packetHandlers
	 */
	public static void setupPacketHandlers()
	{
		addPacketHandler(new DoCommandPacketHandler());
		addPacketHandler(new CheckPlayerPacketHandler());
		addPacketHandler(new GetPlayerDataPacketHandler());
		addPacketHandler(new GetServerDataPacketHandler());
		addPacketHandler(new BroadcastPacketHandler());
		
		MainPlugin.getMainLogger().info("Find out packets" + packetHandlers.toString());
	}
}
