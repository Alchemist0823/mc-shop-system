/*
 * StoreOperator:
 * 
 *  boolean checkStore(String PlayerName)
 *  	check if a player has thing in store;
 *  
 *  int getStoreNumber(String PlayerName)
 *  	return number of thing in store of a player
 *  
 *  String[] getAllThing(String PlayerName)
 *  	return all things of a player, commands with prefix Hash.
 *  
 *  boolean addCommand(String command)
 *  	no return, throw exception
 *  
 *  boolean deleteCommand(String userName, int hash)
 *  	delete one single command in store;
 *  
 *  boolean deletePlayer(String userName)
 *  	delete all things of one player in store;
 *  
 *  !Store in form:
 *  	PlayerName MUST BE BEFORE THE FIRST " " !
 *  
 *  @author Kelym
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

		loadMap();
	}

	public boolean checkStore(String userName) {
		return commandTable.containsKey(userName);
	}

	public int getStoreNumber(String userName){

		if(!checkStore(userName))
			return 0;

		return commandTable.get(userName).size();
	}

	public String[] getAllThing(String userName){
		
		if(!checkStore(userName))
			return null;
		String[] arg = (String[]) commandTable.get(userName).toArray();
		return arg;
	}

	public boolean addCommand(String arg){
		
		String userName = arg.substring(0, arg.indexOf(" "));

		if(!writeCommand(arg)){
			MainPlugin.getMainLogger().log(Level.WARNING, 
					"Couldn't write to waitlist.bak!");
			return false;
		}
			
		return addStoreCommand(userName, arg);
	}

	public boolean deleteCommand(String userName, String command){
		
		if(getStoreNumber(userName) == 0){
			MainPlugin.getMainLogger().log(Level.WARNING,
					"Could not find anything in store belong to User: " + userName);
			return false;
		}
		
		ArrayList<String> store = commandTable.get(userName);
		if(!store.remove(command)){
			MainPlugin.getMainLogger().log(Level.WARNING,
					"Could not find command:" + command +
					"belong to User: " + userName);
			return false;
		}
		
		if(store.isEmpty()){
			commandTable.remove(userName);
			store.trimToSize();
		}
		
		return updateFile();
	}

	public boolean deletePlayer(String userName) throws IOException {
		if(getStoreNumber(userName) == 0){
			return true;
		}
		
		ArrayList<String> store = commandTable.get(userName);
		store.clear();
		store.trimToSize();
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
			addStoreCommand(user,temp);
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
					writer.write(i);
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
	
	private boolean writeCommand(String arg){
		try{
			FileWriter fw = new FileWriter(bak,true);
			fw.write(arg + System.getProperty("line.separator"));
			fw.close();
			return true;
		}
		catch (IOException e){
			return false;
		}
	}
	
	private boolean addStoreCommand(String user,String command){
		ArrayList<String> store;
		if(commandTable.containsKey(user)){
			store = commandTable.get(user);
		}
		else{
			store = new ArrayList<String>();
			commandTable.put(user, store);
		}
		return store.add(command);
	}

	private void cleanBak() throws IOException{
		FileWriter fw = new FileWriter(bak);
		fw.write("");
		fw.close();
	}
}