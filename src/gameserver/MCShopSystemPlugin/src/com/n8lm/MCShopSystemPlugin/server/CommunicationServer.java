package com.n8lm.MCShopSystemPlugin.server;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.Debug;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

/**
 * @author Alchemist
 *
 */

public class CommunicationServer extends Thread
{
	private boolean running = false;
	private boolean connected = false;
	private boolean authenticated = false;
	private ServerSocket serverSkt;
	
	public CommunicationServer()
	{
		PacketManager.setupPacketHandlers();
	}
	
	@Override
	public void run()
	{
		try
		{
			Debug.log(Level.INFO, "Starting server");
			startServer();
		}
		catch (Exception ex)
		{
			MainPlugin.getMainLogger().log(Level.SEVERE, "Server encountered an error. Attempting restart.", ex);
			running = true;
			connected = false;
			authenticated = false;

			try
			{
				serverSkt.close();
			}
			catch (IOException ex1)
			{
			}

			try
			{
				startServer();
			}
			catch (IOException ex1)
			{
				MainPlugin.getMainLogger().log(Level.SEVERE, "Server encountered an error. Server down.", ex);
			}
		}
	}
	
	private void startServer() throws IOException
	{
		running = true;
		serverSkt = new ServerSocket(MainPlugin.getSettings().getPort());
		while (running)
		{
			Debug.log(Level.INFO, "Waiting for client.");
			Socket skt = serverSkt.accept();
			Debug.log(Level.INFO, "Client connected.");
			if (MainPlugin.getSettings().isTrusted(skt.getInetAddress()))
			{
				Debug.log(Level.INFO, "Client is trusted.");
				skt.setKeepAlive(true);
				DataInputStream in = new DataInputStream(skt.getInputStream());
				DataOutputStream out = new DataOutputStream(skt.getOutputStream());

				connected = true;

				Debug.log(Level.INFO, "Trying to read first byte.");

				try
				{
					if (in.readByte() == 21)
					{
						Debug.log(Level.INFO, "First packet is password packet.");
						authenticated = parsePasswordPacket(in, out);
						if (!authenticated)
						{
							MainPlugin.getMainLogger().log(Level.INFO, "Password is incorrect! Client disconnected!");
							connected = false;
						}
						else
						{
							Debug.log(Level.INFO, "Password is correct! Client connected.");
						}
					}
					else
					{
						MainPlugin.getMainLogger().log(Level.WARNING, "First packet wasn't a password packet! Disconnecting. (Are you using the correct protocol?)");
						connected = false;
					}

					while (connected)
					{
						byte packetHeader = in.readByte();
						if (packetHeader == 21)
						{
							Debug.log(Level.INFO, "Got packet header: Disconnect");
							authenticated = parsePasswordPacket(in, out);
							if (!authenticated)
							{
								MainPlugin.getMainLogger().log(Level.INFO, "Password is incorrect! Client disconnected!");
								connected = false;
							}
						}
						else if (packetHeader == 20)
						{
							Debug.log(Level.INFO, "Got packet header: Disconnect");
							connected = false;
						}
						else if (PacketManager.packetHandlers.containsKey(packetHeader))
						{
							Debug.log(Level.INFO, "Got packet header: " + packetHeader);
							PacketManager.packetHandlers.get(packetHeader).onHeaderReceived(in, out);
						}
						else
						{
							MainPlugin.getMainLogger().log(Level.WARNING, "Unsupported packet header!");
						}
					}
					Debug.log(Level.INFO, "Closing connection with client.");
					out.flush();
					out.close();
					in.close();
				}
				catch (IOException ex)
				{
					MainPlugin.getMainLogger().log(Level.WARNING, "IOException while communicating to client! Disconnecting.");
					connected = false;
				}
			}
			else
			{
				MainPlugin.getMainLogger().log(Level.WARNING, "Connection request from unauthorized address!");
				MainPlugin.getMainLogger().log(Level.WARNING, "Address: " + skt.getInetAddress());
				MainPlugin.getMainLogger().log(Level.WARNING, "Add this address to config.txt");
			}
			skt.close();
		}
		serverSkt.close();
	}

	public void stopServer()
	{
		running = false;
	}

	public boolean isConnected()
	{
		return connected;
	}
	
	// static Password Method
	private static boolean parsePasswordPacket(DataInputStream in, DataOutputStream out) throws IOException
	{
		String inPass = CommunicationHelper.readString(in);
		if(inPass.equals(MainPlugin.getSettings().getPassword()))
		{
			CommunicationHelper.writeInt(out, 1);
			return true;
		}
		else
		{
			CommunicationHelper.writeInt(out, 0);
			return false;
		}
	}
	
}
