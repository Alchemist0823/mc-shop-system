/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.config;

/**
 * @author Alchemist
 *
 */
abstract public class ConfigHandler {
	
	protected Settings settings = new Settings();
	
	abstract public Settings loadSettings();
	
	abstract public void generateConfig();
	
	abstract public Boolean hasConfig();
}
