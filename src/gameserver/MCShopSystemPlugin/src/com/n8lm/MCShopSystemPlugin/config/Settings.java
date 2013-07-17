package com.n8lm.MCShopSystemPlugin.config;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * @author Alchemist
 *
 */
public class Settings
{
	private String command = "mcshop";
	private String password = "mymcshop";
	private String URL = "";
	private String salt = "";
	private String algorithm = "MD5";
	private int port = 10808;
	private boolean debugMode = false;
	private boolean serverActive = true;
	private ArrayList<InetAddress> hosts = new ArrayList<InetAddress>();

	public Settings()
	{
	}
	
	public boolean isTrusted(InetAddress address)
	{
		return hosts.contains(address);
	}
	
	public ArrayList<InetAddress> getHosts()
	{
		return hosts;
	}

	public void addHost(InetAddress address)
	{
		if(!hosts.contains(address))
		{
			hosts.add(address);
		}
	}

	public void removeHost(InetAddress address)
	{
		hosts.remove(address);
	}
	
	// getter and setter
	
	public boolean isDebugMode()
	{
		return debugMode;
	}


	public String getURL()
	{
		return URL;
	}
	
	public String getCommand()
	{
		return command;
	}

	public String getPassword()
	{
		return password;
	}

	public int getPort()
	{
		return port;
	}

	public String getSalt()
	{
		return salt;
	}

	public String getHashingAlgorithm()
	{
		return this.algorithm;
	}

	public boolean isServerActive()
	{
		return serverActive;
	}

	public void setURL(String URL)
	{
		this.URL = URL;
	}
	
	public void setDebugMode(boolean debugMode)
	{
		this.debugMode = debugMode;
	}

	public void setCommand(String command)
	{
		this.command = command;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
	
	public void setSalt(String salt)
	{
		this.salt = salt;
	}

	public void setServerActive(boolean serverActive)
	{
		this.serverActive = serverActive;
	}

	public void setHashingAlgorithm(String algorithm)
	{
		this.algorithm = algorithm;
	}
}
