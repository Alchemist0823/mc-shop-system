/*
 * PasswordOperator:
 * 
 * 		Initiation:
 *  	Open password.dat;
 *  	Load a HashMap from password.dat;
 *  
 *  	Get Boolean:
 *  	hasPasswd(UserName);
 *  	checkPasswd(UserName,Password);
 *  
 *  	Operate:
 *  	setPassword for player;
 *  	return password of a player;
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
import java.util.Map;
import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.MainPlugin;

public class PasswordOperator
{
	private HashMap<String, String> PasswdMap;
	private static File folder;
	private static File dat;
	private static File bak;
	
	public PasswordOperator() throws IOException
	{
		folder = MainPlugin.getInstance().getDataFolder();
		dat  = new File(folder, "password.dat");
		bak = new File(folder, "password.bak");
		
		BufferedReader reader = openFile();
		if(reader == null)
			throw new IOException();
		loadMap(reader);
		reader.close();
	}

	public HashMap<String, String> getHashMap(){
		return PasswdMap;
	}
	
	public boolean checkPasswd(String UserName, String GivenPass) {
		return PasswdMap.get(UserName).equals(GivenPass);
	}
	
	public boolean hasPasswd(String UserName) {
		return PasswdMap.containsKey(UserName);
	}
	
	public String getPasswd(String UserName){
		return PasswdMap.get(UserName);
	}

	public boolean changePasswd(String UserName, String Passwd) {
		try{
			//Write to new file
			BufferedWriter writer = openBak();
			PasswdMap.put(UserName, Passwd);
			for(Map.Entry<String, String> entry : PasswdMap.entrySet()) {
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
	
	public boolean setPasswd(String UserName, String Passwd) {
		try{
			FileWriter fw = new FileWriter(bak,true);
			PasswdMap.put(UserName, Passwd);
			fw.write(UserName + " " + Passwd);
			fw.close();
			return true;
		}
		catch (IOException e){
			MainPlugin.getMainLogger().log(Level.WARNING, "Couldn't add user: "+ UserName +" 's password to password.dat!");
			return false;
		}
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
			}
			catch (IOException e){
				MainPlugin.getMainLogger().log(Level.WARNING, "Could not create new password file.");
			}
		}
		return reader;
	}
	
	private void loadMap(BufferedReader reader) throws IOException{
		String temp,user,pass;
		int space;
		temp = null;
		while((temp = reader.readLine()) != null){
			space = temp.indexOf(" ");
			user = temp.substring(0, space);
			pass = temp.substring(space+1, temp.length());
			PasswdMap.put(user, pass);
		}
	}
	
	private void cleanBak() throws IOException{
		FileWriter fw = new FileWriter(bak);
		fw.write("");
		fw.close();
	}
}
