/*
 * PasswordOperator:
 * 
 * 		Init:
 *  	Open password.dat;
 *  	Load a HashMap from .dat;
 *  
 *  	Get Boolean:
 *  	hasPasswd(UserName);
 *  	checkPasswd(UserName,Password);
 *  
 *  	Operate:
 *  	setPassword for player;
 *  	changePassword for player;
 *  	return password of a player;
 *  
 */
package com.n8lm.MCShopSystemPlugin.FileOperator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.MainPlugin;

public class PasswordOperator
{
	private HashMap<String, String> PasswdMap;
	private static File dat;
	private static File folder = MainPlugin.getInstance().getDataFolder();
	
	public PasswordOperator() throws IOException
	{
		dat = new File(folder, "password.dat");
		
		BufferedReader reader = openFile();
		if(reader == null){
			throw new IOException();
		}
		else{
			loadMap(reader);
			reader.close();
			// TODO FileWriter write = openFile();
		}
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
		PasswdMap.put(UserName, Passwd);
		//TODO Operate the File.
		return true;
	}

	public boolean setPasswd(String UserName, String Passwd) {
		PasswdMap.put(UserName, Passwd);
		//TODO Operate the File.
		return true;
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
}
