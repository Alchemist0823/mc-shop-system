package com.n8lm.MCShopSystemPlugin.config;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import com.n8lm.MCShopSystemPlugin.MainPlugin;


/**
 * @author Alchemist
 *
 */
public class PlainTextConfigHandler extends ConfigHandler
{

	public PlainTextConfigHandler()
	{
		super();
	}
	
	@Override
	public Settings loadSettings()
	{
		// Prepare new settings map

		MainPlugin.getMainLogger().info("Loading Configuration.");
		// Set default values
		settings.setPort(10808);
		settings.setDebugMode(false);
		settings.setServerActive(false);

		
		try {
			// Open file
			BufferedReader reader;
			reader = openFile();
			// Parse each line if line is not null
			String currentLine;
			while ((currentLine = reader.readLine()) != null)
			{
				parseLine(currentLine, settings);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			MainPlugin.getMainLogger().log(Level.WARNING, "Can not find out Configuration!");
		} catch (IOException e1) {
			MainPlugin.getMainLogger().log(Level.WARNING, "Failed to read your configuration file.");
			MainPlugin.getMainLogger().log(Level.SEVERE, null, e1);
		}

		return settings;
	}

	public void generateConfig()
	{
		// File declaration
		File mcshopDir = MainPlugin.getInstance().getDataFolder();
		if (!mcshopDir.exists())
		{
			mcshopDir.mkdirs();
		}
		File configFile = new File(mcshopDir, "config.txt");

		// Prepare file
		PrintWriter writer = null;
		try
		{
			if (!configFile.createNewFile())
			{
				MainPlugin.getMainLogger().log(Level.WARNING, "Could not create new config file.");
			}
			writer = new PrintWriter(new FileWriter(configFile));
		}
		catch (IOException ex)
		{
			MainPlugin.getMainLogger().info("Mcshop failed to create a new configuration file.");
			MainPlugin.getMainLogger().log(Level.SEVERE, null, ex);
		}

		// Fill file
		writer.println("#Configuration and settings file!");
		writer.println("#Help: PASS: change the password to one of your choice (set the same in the server php file).");
		writer.println("#Help: DEBUG_MCSHOP: shows debugging messages for easier tracking of bugs.");
		//writer.println("#Help: SALT: adds a salt to the hashed password when sending over bukkit -> php connection.");
		writer.println("PASS=123456");
		writer.println("#Trusted domains can connect to mcshop via php->mcshop.");
		writer.println("#Domains are allowed in either IP or hostname form.");
		writer.println("HOST_ADD=localhost");
		writer.println("HOST_ADD=127.0.0.1");
		writer.println("#HOST_ADD=123.456.798.132");
		writer.println("#Optional settings. Remove the '#' to use.");
		writer.println("#URL=yoururl.com/page.php");
		writer.println("#WEBLISTENER_ACTIVE=false/true");
		writer.println("#ALTPORT=1234");
		writer.println("#DEBUG_MCSHOP=false/true");
		writer.println("#SALT=abc123");
		writer.close();
	}

	@Override
	public Boolean hasConfig() {
		try {
			// Open file
			@SuppressWarnings("unused")
			BufferedReader reader;
			reader = openFile();
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}

	private BufferedReader openFile() throws FileNotFoundException
	{
		// File declaration
		File folder = MainPlugin.getInstance().getDataFolder();
		File configFile = new File(folder, "config.txt");

		// Reader opening
		BufferedReader reader = new BufferedReader(new FileReader(configFile));
		return reader;
	}

	private void parseLine(String line, Settings settings)
	{
		// Is the line a comment?
		if (line.trim().startsWith("#"))
		{
			return;
		}
		else
		{
			// What value does this line contain?
			if (line.startsWith("COMMAND="))
			{
				String value = line.replaceFirst("COMMAND=", "");
				settings.setCommand(value);
			}
			else if (line.startsWith("PASS="))
			{
				String value = line.replaceFirst("PASS=", "");
				settings.setPassword(value);
			}
			else if (line.startsWith("URL="))
			{
				String value = line.replaceFirst("URL=", "");
				settings.setURL(value);
			}
			else if (line.startsWith("ALTPORT="))
			{
				String value = line.replaceFirst("ALTPORT=", "");
				int convertedValue = 0;
				try
				{
					convertedValue = Integer.parseInt(value.trim());
					if (convertedValue == MainPlugin.getBukkitServer().getPort())
					{
						MainPlugin.getMainLogger().log(Level.WARNING, "You are trying to host mcshop on the minecraft server port! Choose a different port.");
					}
				}
				catch (Exception ex)
				{
					MainPlugin.getMainLogger().log(Level.SEVERE, "Mcshop failed to parse your new port value:" + value, ex);
					return;
				}
				settings.setPort(convertedValue);
			}
			else if (line.startsWith("DEBUG_MCSHOP="))
			{
				String value = line.replaceFirst("DEBUG_MCSHOP=", "");
				if (value.toLowerCase().trim().contains("true"))
				{
					settings.setDebugMode(true);
				}
				else
				{
					settings.setDebugMode(false);
				}
			}
			else if (line.startsWith("WEBLISTENER_ACTIVE="))
			{
				String value = line.replaceFirst("WEBLISTENER_ACTIVE=", "");
				if (value.toLowerCase().trim().contains("true"))
				{
					settings.setServerActive(true);
				}
				else
				{
					settings.setServerActive(false);
				}
			}
			else if (line.startsWith("SALT="))
			{
				String value = line.replaceFirst("SALT=", "");
				settings.setSalt(value);
			}
			else if (line.startsWith("HASH_ALGORITHM="))
			{
				String value = line.replaceFirst("HASH_ALGORITHM=", "");
				try
				{
					@SuppressWarnings("unused")
					MessageDigest md = MessageDigest.getInstance(value);
					settings.setHashingAlgorithm(value);
				}
				catch (NoSuchAlgorithmException ex)
				{
					MainPlugin.getMainLogger().info("Hashing algorithm '" + value + "' is not available on this machine. Reverting to MD5");
				}
			}
			else if (line.startsWith("HOST_ADD="))
			{
				String value = line.replaceFirst("HOST_ADD=", "");
				try {
					InetAddress address = InetAddress.getByName(value);
					address = InetAddress.getByAddress(address.getAddress());
					settings.addHost(address);
					MainPlugin.getMainLogger().info("Add Host '" + address + "' successfully");
				} catch (UnknownHostException ex) {
					// TODO Auto-generated catch block
					MainPlugin.getMainLogger().info("Host '" + value + "' is invaild");
				}
			}
			else
			{
				MainPlugin.getMainLogger().info("MCSHOP ERROR: Error while parsing config file.");
				MainPlugin.getMainLogger().info("Invalid line: " + line);
			}
		}
	}
}
