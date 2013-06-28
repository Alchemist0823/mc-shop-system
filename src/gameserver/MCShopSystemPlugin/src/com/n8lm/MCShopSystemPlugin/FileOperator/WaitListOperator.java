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
 *  	return one thing of a player, command with prefix Hash.
 *  
 *  String[] getAllThing(String PlayerName)
 *  	return all things of a player, commands with prefix Hash.
 *  
 *  addCommand(String command)
 *  	no return, throw exception
 *  
 *  boolean deleteCommand(String userName, int hash)
 *  	delete one single command in store;
 *  
 *  deletePlayer(String userName)
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
	private HashMap<String, Integer> store;
	private HashMap<Integer, Integer> next;
	private HashMap<Integer, String> commandTable;
	
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
	
	public boolean checkStore(String userName) {
		return store.containsKey(userName);
	}
	
	public int getStoreNumber(String userName){
		
		if(!checkStore(userName))
			return 0;
		
		int num = 1,i;
		i = store.get(userName);
		while(next.containsKey(i)) {
			num++;
			i = next.get(i);
		}
		return num;
	}
	
	public String getThing(String userName){
		return store.get(userName)+" "+commandTable.get(store.get(userName));
	}
	
	public String[] getAllThing(String userName){
		int num = getStoreNumber(userName);
		String[] arg = new String[num];
		
		int add = store.get(userName);
		for(int i=0;i<num;i++){
			arg[i] = add + " " + commandTable.get(add);
			add = next.get(add);
		}
		
		return arg;
	}
	
	public void addCommand(String arg) throws IOException{
		// TODO, If necessary, convert String to form.
		String userName = arg.substring(0, arg.indexOf(" "));
		
		FileWriter fw = new FileWriter(bak,true);
		fw.write(arg + System.getProperty("line.separator"));
		fw.close();
		
		HashCode ++;
		commandTable.put(HashCode, arg);
		if(store.containsKey(userName)){
			int old = store.get(userName);
			next.put(HashCode, old);
		}
		store.put(userName, HashCode);
	}
	
	public boolean deleteCommand(String userName, int hash) throws IOException {
		BufferedWriter writer = openBak();
			
		// Deal with the table
		if(!commandTable.containsKey(hash)){
			MainPlugin.getMainLogger().log(Level.WARNING, "Could not find the store thing. Hash: " + hash);
			return false;
		}
		commandTable.remove(hash);
		
		int num = getStoreNumber(userName);
		int key = store.get(userName);
		
		if(key == hash){
			commandTable.remove(key);
			if(next.containsKey(key)){
				store.put(userName, next.get(key));
				next.remove(key);
			}
			else{
				store.remove(userName);
			}
		}
		else{
			int link = 0;
			while(--num > 0 && key != hash){
				link = key;
				key = next.get(key);
			}
			if(num == 0){
				MainPlugin.getMainLogger().log(Level.WARNING, "The store thing. Hash: " + hash + "Don't belong to Player "+ userName);
				return false;
			}
			commandTable.remove(key);
			if(next.containsKey(key)){
				next.put(link, next.get(key));
				next.remove(key);
			}
			else{
				next.remove(link);
			}
		}
			
		//Write to new file
		for(Entry<Integer, String> entry : commandTable.entrySet()) {
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
	
	public void deletePlayer(String userName) throws IOException {
		BufferedWriter writer = openBak();
			
		// Deal with the table
		int num = getStoreNumber(userName);
		if(num == 0) return;
			
		int key = store.get(userName);
		store.remove(userName);
			
		int link;
		while(--num > 0){
			commandTable.remove(key);
			link = next.get(key);
			next.remove(key);
			key = link;
		}
		commandTable.remove(key);
			
		//Write to new file
		for(Entry<Integer, String> entry : commandTable.entrySet()) {
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
			commandTable.put(HashCode, temp);
			
			//Check LinkedList
			oldnext = store.get(user);
			store.put(user, HashCode);
			if(commandTable.containsKey(oldnext)){
				next.put(HashCode, oldnext);
			}
		}
	}
	
	private void cleanBak() throws IOException{
		FileWriter fw = new FileWriter(bak);
		fw.write("");
		fw.close();
	}
}
