/**
 * 
 */
package com.n8lm.MCShopSystemPlugin.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

	public DoCommandPacketHandler()
	{
		super((byte) 3);
	}

	@Override
	public void onHeaderReceived(DataInputStream in, DataOutputStream out) throws IOException {
		
		String command = CommunicationHelper.readString(in);
		command = convertCommand(command);
		
		if (sendItem(command))
		{
			out.writeInt(1);
		}
		else
		{
			out.writeInt(0);
		}
	}
	
	public static boolean sendItem(String command){
		boolean success;
		try
		{
			success = MainPlugin.getBukkitServer().dispatchCommand(MainPlugin.getBukkitServer().getConsoleSender(), command);
		}
		catch(Exception ex)
		{
			if(MainPlugin.getSettings().isDebugMode())
			{
				MainPlugin.getMainLogger().log(Level.WARNING, "MCShop caught an exception while running command '"+command+"'", ex);
			}
			success = false;
		}
		return success;
	}
	
	public static void main(String[] arg){
		System.out.println(convertCommand("give {player} 123 {quantity} $player(alchemist) $quantity(10)"));
		System.out.println(convertCommand("   give {player} 123 {quantity} $player(alchemist) $quantity(10)    "));
		
	}

	public static String convertCommand(String command){
		
		command = deleteSpace(command);
		
		String temp, content;
		StringBuilder build;
		
		// replace {}
		int i, j, posi, posj;
		while((i=command.indexOf("{")) >= 0){
			
			// Find {temp} , get temp;
			try{
				j = command.indexOf("}");
				temp = command.substring(i+1, j);
			}
			catch (IndexOutOfBoundsException ex){
				MainPlugin.getMainLogger().log(Level.WARNING,
						"Failed to convert Command! " + 
						" Cannot find } for { !" );
				return null;
			}
			
			// Find $temp(content), get content
			try{
				posi = command.indexOf("$"+temp+"(");
				posj = command.indexOf(")", posi);
				content = command.substring(posi+2+temp.length(), posj);
			}
			catch (IndexOutOfBoundsException ex){
				//MainPlugin.getMainLogger().log(Level.WARNING,
				//		"Failed to convert Command! " + 
				//		" Cannot find $Variable(Value) !" );
				return null;
			}
			
			// Revise command
			build = new StringBuilder();
			if(i>0) build.append(command.substring(0, i));
			build.append(content);
			if(posi > j+1) build.append(command.substring(j+1, posi));
			if(posj < command.length()-1)
				build.append(command.substring(posj+1, command.length()));
			
			command = build.toString();
		}
		
		command = deleteSpace(command);
		
		return command;
	}
	
	public static String deleteSpace(String input){
		
		int begin, end ,length = input.length();

		// Delete Space at head and tail
		for(begin = 0;begin < length;begin++)
			if(input.charAt(begin) != ' ') break;
		if(begin >= length)
			return null;
		for(end = length - 1;end > begin;end --)
			if(input.charAt(end) != ' ') break;
		
		// Deal with input
		StringBuffer output = new StringBuffer();
		char c;
		while(begin <= end){
			c = input.charAt(begin);
			output.append(c);
			if(c!= ' '){
				begin++;
			}
			else{
				while(input.charAt(begin)==' ')
					begin++;
			}
		}
		return output.toString();
	}
}
