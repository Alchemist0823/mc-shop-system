package com.n8lm.MCShopSystemPlugin.utils;

import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.MainPlugin;

public class DebugHelper{
	public static void log(Level level, String msg){
		if(MainPlugin.getSettings().isDebugMode()){
			MainPlugin.getMainLogger().log(level, msg);
		}
	}

	public static void log(Level level, String msg, Exception ex) {
		if(MainPlugin.getSettings().isDebugMode()){
			MainPlugin.getMainLogger().log(level, msg, ex);
		}
	}
}