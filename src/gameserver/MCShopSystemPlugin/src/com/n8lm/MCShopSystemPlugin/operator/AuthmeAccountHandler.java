/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.operator;

import java.security.NoSuchAlgorithmException;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.utils.CommandHelper;

import uk.org.whoami.authme.cache.auth.PlayerAuth;
import uk.org.whoami.authme.security.PasswordSecurity;
import uk.org.whoami.authme.settings.Settings;
import uk.org.whoami.authme.AuthMe;

/**
 * @author Alchemist
 *
 */
public class AuthmeAccountHandler extends AccountHandler {

	@Override
	public boolean hasPassword(String username) {
		
		String name = username.toLowerCase();
		
		AuthMe authme = (AuthMe) MainPlugin.getBukkitServer().getPluginManager().getPlugin("authme");
		
		PlayerAuth playerAuth = authme.database.getAuth(name);
		if(playerAuth == null)
			return false;
		else
			return true;
	}

	@Override
	public boolean checkPassword(String username, String pass) {
		
		String name = username.toLowerCase();
		
		AuthMe authme = (AuthMe) MainPlugin.getBukkitServer().getPluginManager().getPlugin("authme");
		
		PlayerAuth playerAuth = authme.database.getAuth(name);
		if(playerAuth == null)
			return false;
		String hashnew;
		try {
			hashnew = PasswordSecurity.getHash(Settings.getPasswordHash, pass, name);
			if(playerAuth.getHash() == hashnew)
				return true;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean register(String userName, String pass) {
		return CommandHelper.sendCommand("authme admin register " + userName + " " + pass);	
	}

	@Override
	public boolean changePassword(String userName, String pass) {
		return CommandHelper.sendCommand("authme admin changepassword " + userName + " " + pass);	
	}
}
