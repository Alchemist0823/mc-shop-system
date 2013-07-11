package com.n8lm.MCShopSystemPlugin.FileOperator;

/*
 * WaitListOperator:
 * 
 *  boolean isOnWaitList(String PlayerName)
 *  	check if a player has thing in commands;
 *  
 *  int getCommandsNumber(String PlayerName)
 *  	return number of thing in commands of a player
 *  
 *  String[] getAllThing(String PlayerName)
 *  	return all things of a player, commands with prefix Hash.
 *  
 *  boolean addCommand(String PlayerName, String command)
 *  	no return, throw exception
 *  
 *  boolean deleteCommand(String userName, int hash)
 *  	delete one single command in commands;
 *  
 *  boolean deletePlayer(String userName)
 *  	delete all things of one player in commands;
 *  
 *  !Store in form:
 *  	PlayerName MUST BE BEFORE THE FIRST " " !
 *  
 *  @author Kelym and Alchemist
 *  
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.MainPlugin;

public class WaitListOperator
{
	private HashMap<String, ArrayList<String>> commandTable;

	private static File folder;
	private static File dat;
	private static File bak;

	public WaitListOperator() throws IOException
	{
		folder = MainPlugin.getInstance().getDataFolder();
		dat  = new File(folder, "waitlist.dat");
		bak = new File(folder, "waitlist.bak");
		this.commandTable = new HashMap<String, ArrayList<String>>();

		loadMap();
	}

	public boolean isOnWaitList(String userName) {
		return commandTable.containsKey(userName);
	}

	public int getCommandsNumber(String userName){

		if(!isOnWaitList(userName))
			return 0;

		return commandTable.get(userName).size();
	}

	public String[] getAllCommands(String userName){
		
		if(!isOnWaitList(userName))
			return null;
		String[] args = {};
		args = commandTable.get(userName).toArray(args);
		return args;
	}

	/*
	private boolean writeCommand(String arg){
		try{
			FileWriter fw = new FileWriter(bak,true);
			fw.write(arg + "\n");
			fw.close();
			return true;
		}
		catch (IOException e){
			return false;
		}
	}*/
	
	public boolean addCommand(String userName, String command){
		
		/*if(!writeCommand(userName + ' ' + command)){
			MainPlugin.getMainLogger().log(Level.WARNING, 
					"Couldn't write to waitlist.bak!");
			return false;
		}*/
		
		if(loadCommand(userName, command))
			return updateFile();
		else
			return false;
	}
	
	private boolean loadCommand(String userName, String command){
		ArrayList<String> commands;
		if(commandTable.containsKey(userName)){
			commands = commandTable.get(userName);
		}
		else{
			commands = new ArrayList<String>();
			commandTable.put(userName, commands);
		}
		return commands.add(command);
	}
	
	public boolean deleteCommand(String userName, String command){
		
		if(getCommandsNumber(userName) == 0){
			MainPlugin.getMainLogger().log(Level.WARNING,
					"Could not find anything in commands belong to User: " + userName);
			return false;
		}
		
		ArrayList<String> commands = commandTable.get(userName);
		if(!commands.remove(command)){
			MainPlugin.getMainLogger().log(Level.WARNING,
					"Could not find command:" + command +
					"belong to User: " + userName);
			return false;
		}
		
		if(commands.isEmpty()){
			commandTable.remove(userName);
			commands.trimToSize();
		}
		
		return updateFile();
	}

	public boolean deletePlayer(String userName) throws IOException {
		if(getCommandsNumber(userName) == 0){
			return true;
		}
		
		ArrayList<String> commands = commandTable.get(userName);
		commands.clear();
		commands.trimToSize();
		commandTable.remove(userName);
		
		return updateFile();
	}

	private BufferedWriter openBak() throws IOException{
		BufferedWriter writer = null;
		try{
			if(!(bak.createNewFile())){
				MainPlugin.getMainLogger().log(Level.SEVERE, "A Waitlist Temp File already exists.");
				MainPlugin.getMainLogger().log(Level.SEVERE, "A Waitlist change request may haven't been performed.");
				try{
					cleanBak();
				}
				catch(IOException ex){
					MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't overwrite waitlist Temp File.");
					throw ex;
				}
			}
			writer = new BufferedWriter(new FileWriter(bak));
		}
		catch (IOException e){
			MainPlugin.getMainLogger().log(Level.WARNING, "Could not create a temp waitlist file.");
			throw e;
		}
		return writer;
	}

	private BufferedReader openFile() throws IOException
	{
		BufferedReader reader = null;

		try{
			reader = new BufferedReader(new FileReader(dat));
		}
		catch (FileNotFoundException ex){
			if (!folder.exists()){
				folder.mkdirs();
			}
			try{
				dat.createNewFile();
				reader = new BufferedReader(new FileReader(dat));
			}
			catch (IOException e){
				MainPlugin.getMainLogger().log(Level.WARNING, "Could not create new waitlist file.");
				throw e;
			}

		}
		return reader;
	}

	private void loadMap() throws IOException{
		BufferedReader reader = openFile();
		String temp,user;
		int space;
		temp = null;
		while((temp = reader.readLine()) != null){

			// Get Command and PlayerName
			space = temp.indexOf(" ");
			user = temp.substring(0, space);

			//Put in to Table
			loadCommand(user, temp.substring(space + 1));
		}
		reader.close();
	}
	
	private boolean updateFile(){
		
		try{
			BufferedWriter writer = openBak();
			//Write to new file
			for(Entry<String, ArrayList<String>> entry : commandTable.entrySet()) {
				ArrayList<String> value = entry.getValue();
				for(String i:value){
					writer.write(entry.getKey() + " " + i);
					writer.newLine();
					writer.flush();
					}
				}
			writer.close();
		}
		catch (IOException e){
			MainPlugin.getMainLogger().log(Level.WARNING,
					"Couldn't write to waitlist.bak!");
			e.printStackTrace();
			return false;
		}

		// Replace
		dat  = new File(folder, "waitlist.dat");
		bak = new File(folder, "waitlist.bak");

		if(!dat.delete()){
			MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't delete waitlist.dat!");
			return false;
		}
		else{
			if(!bak.renameTo(new File(folder, "waitlist.dat"))){
				MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't rename waitlist.bak!");
				return false;
			}
		}
		return true;
	}

	private void cleanBak() throws IOException{
		FileWriter fw = new FileWriter(bak);
		fw.write("");
		fw.close();
	}
}