/**
 * 
 */
package com.n8lm.MCShopSystemPlugin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Alchemist
 *
 */

public abstract class PacketHandler
{
	private byte header;

	public PacketHandler(byte header)
	{
		this.header = header;
	}

	public abstract void onHeaderReceived(DataInputStream in, DataOutputStream out) throws IOException;

	public byte getHeader()
	{
		return header;
	}
}
