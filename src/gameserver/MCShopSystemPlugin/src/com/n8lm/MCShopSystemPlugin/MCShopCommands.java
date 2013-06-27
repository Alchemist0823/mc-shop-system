package com.n8lm.MCShopSystemPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.n8lm.MCShopSystemPlugin.FileOperator.CheckUser;

public class MCShopCommands implements CommandExecutor {
 
	private MainPlugin plugin;
 
	public MCShopCommands(MainPlugin plugin) {
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("MCShop")){
			
			int params = args.length;
			
			if(args[0].equalsIgnoreCase("pass")){
				
				if(!(sender instanceof Player)){
					sender.sendMessage("You must be a player to set password!");
					return false;
				}
				
				CheckUser PlayerInfo = new CheckUser(sender.getName());
				
				if(PlayerInfo.hasPasswd()){
					if(params == 2){
						sender.sendMessage("������������");
						sender.sendMessage("�����������룬��ʹ�����¸�ʽ��/mcshop pass �������� ������");
						sender.sendMessage("�����Ǿ����룬��ͬ����Ա��ϵ");
						return false;
					}
					if(params == 3){
						if(PlayerInfo.checkPasswd(args[1])){
							if(MainPlugin.getPasswdFile().changePasswd(args[2])){
								sender.sendMessage("�����������룡");
								return true;
							}
							else{
								sender.sendMessage("�������ó��ִ���δ���������룡");
								return false;
							}
						}
						else{
							sender.sendMessage("������������������ԣ�");
							sender.sendMessage("�������룬��ʹ�����¸�ʽ��/mcshop pass ������ ������");
							return false;
						}
					}
					sender.sendMessage("����MCShop�̵�������ʹ�����¸�ʽ��");
					sender.sendMessage("/mcshop pass ��������");
				}
				else{
					if(params == 2)
						if(MainPlugin.getPasswdFile().setPasswd(args[1])){
							sender.sendMessage("�ɹ��������룡");
							return true;
						}
						else{
							sender.sendMessage("�������ó��ִ���δ���������룡");
							return false;
						}
					else{
						sender.sendMessage("�������룬��ʹ�����¸�ʽ��/mcshop pass ����");
						return false;
					}
				}
			}
		}
		return false;
	}
	
}