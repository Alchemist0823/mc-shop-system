/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.server.CommunicationHelper;
import com.n8lm.MCShopSystemPlugin.server.PacketHandler;

/**
 * This is a example of packetHandler. we need fix it in some way.
 * 
 * @author Alchemist
 *
 */
public class DoCommandPacketHandler extends PacketHandler {

	private HashMap<String, String> variables;
	private String originalCommand, parsedCommand;
	
	public DoCommandPacketHandler()
	{
		super((byte) 3);
		
		this.variables = new HashMap<String, String>();
	}

	@Override
	public void onHeaderReceived(DataInputStream in, DataOutputStream out) throws IOException {
				
		originalCommand = CommunicationHelper.readString(in);
		parsedCommand = parseCommand(originalCommand);
		
		MainPlugin.getMainLogger().info("Receive command: " + parsedCommand);
		
		if (sendItem())
		{
			out.writeInt(1);
		}
		else
		{
			out.writeInt(0);
		}
	}
	
	public boolean sendItem(){
		boolean success = false;
		
		if(parsedCommand == null)
			return success;
		String playerName = variables.get("player");
		if(MainPlugin.getPasswordOperator().hasPasswd(playerName))
		{
			if(MainPlugin.getBukkitServer().getPlayer(playerName) != null)
			{
				try
				{
					success = MainPlugin.getBukkitServer().dispatchCommand(MainPlugin.getBukkitServer().getConsoleSender(), parsedCommand);
				}
				catch(Exception ex)
				{
					MainPlugin.getMainLogger().log(Level.WARNING, "MCShop caught an exception while running command '"+parsedCommand+"'", ex);
					success = false;
				}
				if(success)
					MainPlugin.getMainLogger().info("Send command successfully");
			}
			else
			{
				MainPlugin.getMainLogger().info("Add a waitlist command \"" + parsedCommand + "\" to " + playerName);
				success = MainPlugin.getWaitListOperator().addCommand(playerName, parsedCommand);
			}
		}
		else MainPlugin.getMainLogger().log(Level.WARNING,"Command error: Can not find out the user \"" + playerName +"\"");
		return success;
	}
	/*
	public static void main(String[] arg){
		
		DoCommandPacketHandler handler = new DoCommandPacketHandler();
		
		System.out.println(handler.parseCommand("  give {player} 123 {quantity}    $player(alchemist) $quantity(10) $pass(40)  "));
		
	}*/

	public String parseCommand(String command){

		this.variables.clear();
		
		command = command.trim();
		
		String temp, var, content;
		int poss, posl, posr;

		// Find $temp(content), get content
		while((poss = command.indexOf("$")) >= 0){
			
			// Find {temp} , get temp;
			try{
				temp = command.substring(poss);
				posl = temp.indexOf("(");
				posr = temp.indexOf(")");
				var = temp.substring(1, posl);
				content = temp.substring(posl + 1, posr);
				if(!this.variables.containsKey(var))
					this.variables.put(var, content);
				
				temp = command.substring(poss, poss + posr + 1);
				command = command.replace(temp, "");
			}
			catch (IndexOutOfBoundsException ex){
				MainPlugin.getMainLogger().log(Level.WARNING,
						"Failed to convert Command! " + 
						" Cannot find ( ) for $ !" );
				return null;
			}
		}
		
		// replace {}
		while((posl = command.indexOf("{")) >= 0){
			
			// Find {temp} , get temp;
			try{
				posr = command.indexOf("}");
				var = command.substring(posl + 1, posr);
			}
			catch (IndexOutOfBoundsException ex){
				MainPlugin.getMainLogger().log(Level.WARNING,
						"Failed to convert Command! " + 
						" Cannot find } for { !" );
				return null;
			}
			
			if(this.variables.containsKey(var))
			{
				temp = "{" + var + "}";
				command = command.replace(temp, this.variables.get(var));
			}
		}

		command = command.trim();
		
		return command;
	}
}
