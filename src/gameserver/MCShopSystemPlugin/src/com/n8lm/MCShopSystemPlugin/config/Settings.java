package com.n8lm.MCShopSystemPlugin.config;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * @author Alchemist
 *
 */
public class Settings
{
	private String responseURL;
	private String password;
	private String salt = "";
	private String algorithm = "MD5";
	private int port;
	private boolean debugMode;
	private boolean serverActive;
	private String URL;
	private ArrayList<InetAddress> hosts = new ArrayList<InetAddress>();

	public Settings()
	{
	}

	public Settings(String responseURL, String password, String salt, int port, boolean debugMode, boolean serverActive, String URL)
	{
		this.responseURL = responseURL;
		this.password = password;
		this.salt = salt;
		this.port = port;
		this.debugMode = debugMode;
		this.serverActive = serverActive;
		this.URL = URL;
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
	
	public String getURL()
	{
		return URL;
	}

	public boolean isDebugMode()
	{
		return debugMode;
	}

	public String getPassword()
	{
		return password;
	}

	public int getPort()
	{
		return port;
	}

	public String getResponseURL()
	{
		return responseURL;
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

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public void setResponseURL(String responseURL)
	{
		this.responseURL = responseURL;
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
