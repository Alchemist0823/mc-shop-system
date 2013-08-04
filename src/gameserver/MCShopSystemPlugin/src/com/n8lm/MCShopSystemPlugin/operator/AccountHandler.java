/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.operator;

/**
 * @author Alchemist
 *
 */
public abstract class AccountHandler {
	
	// Password	
	abstract public boolean hasPassword(String userName);

	abstract public boolean checkPassword(String userName, String pass);
	
	abstract public boolean register(String userName, String pass);
	
	abstract public boolean changePassword(String userName, String pass);
}