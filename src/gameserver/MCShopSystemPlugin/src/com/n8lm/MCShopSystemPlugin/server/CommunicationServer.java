package com.n8lm.MCShopSystemPlugin.server;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.PacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
	private HashMap<Byte, PacketHandler> PacketHandlers = new HashMap<Byte, PacketHandler>();
	
	public CommunicationServer()
	{
	}
	
	public void init()
	{
		getPacketHandlers("com.n8lm.MCShopSystemPlugin.packets");
	}
	
	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void getPacketHandlers(String packageName)
	{
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(path);
		    List<File> dirs = new ArrayList<File>();
		    while (resources.hasMoreElements()) {
		        URL resource = resources.nextElement();
		        dirs.add(new File(resource.getFile()));
		    }
		    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		    for (File directory : dirs) {
		        try {
					classes.addAll(findClasses(directory, packageName));
				} catch (ClassNotFoundException e) {
					MainPlugin.getMainLogger().log(Level.SEVERE, "Server encountered an error.", e);
					e.printStackTrace();
				}
		    }
		    for (Class<?> phclass: classes)
		    {
		    	try {
					this.addPacketHandler((PacketHandler) phclass.newInstance());
				} catch (InstantiationException e) {
					MainPlugin.getMainLogger().log(Level.SEVERE, "Server encountered an error.", e);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					MainPlugin.getMainLogger().log(Level.SEVERE, "Server encountered an error.", e);
					e.printStackTrace();
				}
		    }
		} catch (IOException e1) {
			MainPlugin.getMainLogger().log(Level.SEVERE, "Server encountered an error.", e1);
			e1.printStackTrace();
		}
	    
	    
	}
	
	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class<?>> classes = new ArrayList<Class<?>>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}
	/**
	 * Add PacketHandler
	 * @param PacketHandler customhandler
	 */
	public void addPacketHandler(PacketHandler wph)
	{
		PacketHandlers.put(wph.getHeader(), wph);
	}
	
	@Override
	public void run()
	{
		try
		{
			if (MainPlugin.getSettings().isDebugMode())
			{
				MainPlugin.getMainLogger().log(Level.INFO, "Starting server");
			}
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
			if (MainPlugin.getSettings().isDebugMode())
			{
				MainPlugin.getMainLogger().log(Level.INFO, "Waiting for client.");
			}
			Socket skt = serverSkt.accept();
			if (MainPlugin.getSettings().isDebugMode())
			{
				MainPlugin.getMainLogger().log(Level.INFO, "Client connected.");
			}
			if (MainPlugin.getSettings().isTrusted(skt.getInetAddress()))
			{
				if (MainPlugin.getSettings().isDebugMode())
				{
					MainPlugin.getMainLogger().log(Level.INFO, "Client is trusted.");
				}
				skt.setKeepAlive(true);
				DataInputStream in = new DataInputStream(skt.getInputStream());
				DataOutputStream out = new DataOutputStream(skt.getOutputStream());

				connected = true;

				if (MainPlugin.getSettings().isDebugMode())
				{
					MainPlugin.getMainLogger().log(Level.INFO, "Trying to read first byte.");
				}

				try
				{
					if (in.readByte() == 21)
					{
						if (MainPlugin.getSettings().isDebugMode())
						{
							MainPlugin.getMainLogger().log(Level.INFO, "First packet is password packet.");
						}
						authenticated = parsePasswordPacket(in, out);
						if (!authenticated)
						{
							MainPlugin.getMainLogger().log(Level.INFO, "Password is incorrect! Client disconnected!");
							connected = false;
						}
						else
						{
							if (MainPlugin.getSettings().isDebugMode())
							{
								MainPlugin.getMainLogger().log(Level.INFO, "Password is correct! Client connected.");
							}
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
						if (packetHeader == 20)
						{
							if (MainPlugin.getSettings().isDebugMode())
							{
								MainPlugin.getMainLogger().log(Level.INFO, "Got packet header: Disconnect");
							}
							connected = false;
						}
						else if (PacketHandlers.containsKey(packetHeader))
						{
							if (MainPlugin.getSettings().isDebugMode())
							{
								MainPlugin.getMainLogger().log(Level.INFO, "Got custom packet header: " + packetHeader);
							}
							PacketHandlers.get(packetHeader).onHeaderReceived(in, out);
						}
						else
						{
							MainPlugin.getMainLogger().log(Level.WARNING, "Unsupported packet header!");
						}
					}
					if (MainPlugin.getSettings().isDebugMode())
					{
						MainPlugin.getMainLogger().log(Level.INFO, "Closing connection with client.");
					}
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
				MainPlugin.getMainLogger().log(Level.WARNING, "Add this address to trusted.txt to allow access.");
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
		return inPass.equals(MainPlugin.getSettings().getPassword());
	}
}
