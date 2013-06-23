package com.n8lm.MCShopSystemPlugin;

import java.util.logging.Level;

public class Debug{
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