package com.n8lm.MCShopSystemPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.n8lm.MCShopSystemPlugin.FileOperator.PasswordOperator;

public class MCShopCommands implements CommandExecutor {
 
	private MainPlugin plugin;
	private PasswordOperator operator = MainPlugin.getPasswordOperator();
 
	public MCShopCommands(MainPlugin plugin) {
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mcshop")){

			int params = args.length;

			if(args[0].equalsIgnoreCase("pass")){

				if(!(sender instanceof Player)){
					sender.sendMessage("You must be a player to set password!");
					return false;
				}

				Player player = (Player) sender;
				String UserName = player.getName();

				if(operator.hasPasswd(UserName)){
					if(params == 2){
						sender.sendMessage("������������");
						sender.sendMessage("�����������룬��ʹ�����¸�ʽ��/mcshop pass �������� ������");
						sender.sendMessage("�����Ǿ����룬��ͬ����Ա��ϵ");
						return false;
					}
					if(params == 3){
						if(operator.checkPasswd(UserName,args[1])){
							if(operator.changePasswd(UserName,args[2])){
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
						if(operator.setPasswd(UserName,args[1])){
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