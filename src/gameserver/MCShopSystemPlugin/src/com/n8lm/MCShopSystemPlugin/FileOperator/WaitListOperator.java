/*
 * StoreOperator:
 * 
 *  boolean checkStore(String PlayerName)
 *  	check if a player has thing in store;
 *  
 *  int getStoreNumber(String PlayerName)
 *  	return number of thing in store of a player
 *  
 *  String getThing(String PlayerName)
 *  	return one thing of a player, command.
 *  
 *  String[] getAllThing(String PlayerName)
 *  	return all things of a player, commands
 *  
 *  addCommand(String command)
 *  	no return, throw exception
 *  
 *  deletePlayer(String UserName)
 *  	delete all things of one player in store;
 *  
 *  
 *  !Store in form:
 *  	PlayerName MUST BE BEFORE THE FIRST " " !
 *  
 *  @author Kelym
 *  
 */
package com.n8lm.MCShopSystemPlugin.FileOperator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.MainPlugin;

public class WaitListOperator
{
	private HashMap<String, Integer> Store;
	private HashMap<Integer, Integer> Next;
	private HashMap<Integer, String> CommandTable;
	
	private static File folder;
	private static File dat;
	private static File bak;
	
	private static int HashCode;
	
	public WaitListOperator() throws IOException
	{
		folder = MainPlugin.getInstance().getDataFolder();
		dat  = new File(folder, "waitlist.dat");
		bak = new File(folder, "waitlist.bak");
		HashCode = 0;
		
		BufferedReader reader = openFile();
		loadMap(reader);
		reader.close();
	}
	
	public boolean checkStore(String UserName) {
		return Store.containsKey(UserName);
	}
	
	public int getStoreNumber(String UserName){
		
		if(!checkStore(UserName))
			return 0;
		
		int num = 1,i;
		i = Store.get(UserName);
		while(Next.containsKey(i)) {
			num++;
			i = Next.get(i);
		}
		return num;
	}
	
	public String getThing(String UserName){
		return CommandTable.get(Store.get(UserName));
	}
	
	public String[] getAllThing(String UserName){
		int num = getStoreNumber(UserName);
		String[] arg = new String[num];
		
		int add = Store.get(UserName);
		for(int i=0;i<num;i++){
			arg[i] = CommandTable.get(add);
			add = Next.get(add);
		}
		
		return arg;
	}
	
	public void addCommand(String arg) throws IOException{
		// TODO, If necessary, convert String to form.
		String UserName = arg.substring(0, arg.indexOf(" "));
		
		FileWriter fw = new FileWriter(bak,true);
		fw.write(arg + System.getProperty("line.separator"));
		fw.close();
		
		HashCode ++;
		CommandTable.put(HashCode, arg);
		if(Store.containsKey(UserName)){
			int old = Store.get(UserName);
			Next.put(HashCode, old);
		}
		Store.put(UserName, HashCode);
	}
	
	public void deletePlayer(String UserName) throws IOException {
		BufferedWriter writer = openBak();
			
		// Deal with the table
		int num = getStoreNumber(UserName);
		if(num == 0) return;
			
		int key = Store.get(UserName);
		Store.remove(UserName);
			
		int link;
		while(--num > 0){
			CommandTable.remove(key);
			link = Next.get(key);
			Next.remove(key);
			key = link;
		}
		CommandTable.remove(key);
			
		//Write to new file
		for(Entry<Integer, String> entry : CommandTable.entrySet()) {
			String value = entry.getValue();
			writer.write(value);
			writer.newLine();
			writer.flush();
			}
		writer.close();
			
		// Replace
		dat  = new File(folder, "waitlist.dat");
		bak = new File(folder, "waitlist.bak");
			
		if(!dat.delete()){
			MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't delete waitlist.dat!");
		}
		else{
			if(!bak.renameTo(new File(folder, "waitlist.dat"))){
				MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't rename waitlist.bak!");
			}
		}
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
	
	private void loadMap(BufferedReader reader) throws IOException{
		String temp,user;
		int space, oldnext;
		temp = null;
		while((temp = reader.readLine()) != null){
			HashCode ++;
			
			// Get Command and PlayerName
			space = temp.indexOf(" ");
			user = temp.substring(0, space);
			
			//Put in to Table
			CommandTable.put(HashCode, temp);
			
			//Check LinkedList
			oldnext = Store.get(user);
			Store.put(user, HashCode);
			if(CommandTable.containsKey(oldnext)){
				Next.put(HashCode, oldnext);
			}
		}
	}
	
	private void cleanBak() throws IOException{
		FileWriter fw = new FileWriter(bak);
		fw.write("");
		fw.close();
	}
}
