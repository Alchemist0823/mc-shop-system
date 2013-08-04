/*
 * PasswordOperator:
 * 
 * 		Initiation:
 *  	Open password.dat;
 *  	Load a HashMap from password.dat;
 *  
 *  	Get Boolean:
 *  	hasPasswd(userName);
 *  	checkPasswd(userName,Password);
 *  
 *  	Operate:
 *  	changePasswd for player;
 *  	return password of a player;
 *  
 */
package com.n8lm.MCShopSystemPlugin.operator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.MainPlugin;

public class CustomAccountHandler extends AccountHandler
{
	private HashMap<String, String> passwdMap;
	private static File folder;
	private static File dat;
	private static File bak;
	
	public CustomAccountHandler() throws IOException
	{
		folder = MainPlugin.getInstance().getDataFolder();
		dat  = new File(folder, "password.dat");
		bak = new File(folder, "password.bak");
		this.passwdMap = new HashMap<String, String>();
		
		loadMap();
	}

	public HashMap<String, String> getHashMap(){
		return passwdMap;
	}
	
	public boolean checkPassword(String userName, String givenPass) {
		String pwd = passwdMap.get(userName);
		if(pwd != null)
			return pwd.equals(givenPass);
		else
			return false;
	}
	
	public boolean hasPassword(String userName) {
		return passwdMap.containsKey(userName);
	}
	
	public String getPassword(String userName){
		return passwdMap.get(userName);
	}

	public boolean register(String username, String passwd) {
		passwdMap.put(username, passwd);
		return updateFile();
	}

	public boolean changePassword(String username, String passwd) {
		if(passwdMap.containsKey(username))
			passwdMap.put(username, passwd);
		return updateFile();
	}
	
	private boolean updateFile()
	{
		try{
			//Write to new file
			BufferedWriter writer = openBak();
			for(Map.Entry<String, String> entry : passwdMap.entrySet()) {
			    String key = entry.getKey();
			    String value = entry.getValue();
			    writer.write(key + " " + value);
			    writer.newLine();
			    writer.flush();
			}
			writer.close();
			
			// Replace
			dat  = new File(folder, "password.dat");
			bak = new File(folder, "password.bak");
			
			if(!dat.delete()){
				MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't delete password.dat!");
			}
			else{
				if(!bak.renameTo(new File(folder, "password.dat"))){
					MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't rename password.bak!");
				}
			}
		}
		catch (IOException ex){
			return false;
		}
		return true;
	}
	
	private BufferedWriter openBak() throws IOException{
		BufferedWriter writer = null;
		try{
			if(!(bak.createNewFile())){
				MainPlugin.getMainLogger().log(Level.SEVERE, "A Password Temp File already exists.");
				MainPlugin.getMainLogger().log(Level.SEVERE, "A password change request may haven't been performed.");
				try{
					cleanBak();
				}
				catch(IOException ex){
					MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't overwrite Password Temp File.");
					throw ex;
				}
			}
			writer = new BufferedWriter(new FileWriter(bak));
		}
		catch (IOException e){
			MainPlugin.getMainLogger().log(Level.WARNING, "Could not create a temp password file.");
			throw e;
		}
		return writer;
	}
	
	private BufferedReader openFile()
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
				MainPlugin.getMainLogger().log(Level.WARNING, "Could not create new password file.");
			}
		}
		return reader;
	}
	
	private void loadMap() throws IOException{

		BufferedReader reader = openFile();
		String temp, user, pass;
		int space;
		temp = null;
		while((temp = reader.readLine()) != null){
			space = temp.indexOf(" ");
			user = temp.substring(0, space);
			pass = temp.substring(space+1, temp.length());
			passwdMap.put(user, pass);
		}
		reader.close();
	}
	
	private void cleanBak() throws IOException{
		FileWriter fw = new FileWriter(bak);
		fw.write("");
		fw.close();
	}
}
