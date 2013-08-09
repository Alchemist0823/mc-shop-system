/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.operator;

import com.n8lm.MCShopSystemPlugin.utils.CommandHelper;

import uk.org.whoami.authme.api.API;

/**
 * @author Alchemist
 *
 */
public class AuthmeAccountHandler extends AccountHandler {

	@Override
	public boolean hasPassword(String username) {
		return API.isRegistered(username);
	}

	@Override
	public boolean checkPassword(String username, String pass) {
		return API.checkPassword(username, pass);
	}

	@Override
	public boolean register(String username, String pass) {
		return CommandHelper.sendCommand("authme register " + username + " " + pass);
	}

	@Override
	public boolean changePassword(String username, String pass) {
		return CommandHelper.sendCommand("authme changepassword " + username + " " + pass);	
	}
}
