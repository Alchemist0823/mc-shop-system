package com.n8lm.MCShopSystemPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.n8lm.MCShopSystemPlugin.FileOperator.PasswordOperator;

public class MCShopCommands implements CommandExecutor {
 
	private PasswordOperator operator = MainPlugin.getPasswordOperator();
 
	public MCShopCommands() {
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mcshop")){

			int params = args.length;

			if(params > 0 && args[0].equalsIgnoreCase("pass")){

				if(!(sender instanceof Player)){
					sender.sendMessage("You must be a player to set password!");
					return false;
				}

				Player player = (Player) sender;
				String UserName = player.getName();

				if(operator.hasPasswd(UserName)){
					if(params == 2){
						sender.sendMessage("您已设置密码");
						sender.sendMessage("若想重设密码，请使用如下格式：/mcshop pass 已有密码 新密码");
						sender.sendMessage("若忘记旧密码，请同管理员联系");
						return false;
					}
					if(params == 3){
						if(operator.checkPasswd(UserName,args[1])){
							if(operator.changePasswd(UserName,args[2])){
								sender.sendMessage("您已重设密码！");
								return true;
							}
							else{
								sender.sendMessage("密码设置出现错误！未能重设密码！");
								return false;
							}
						}
						else{
							sender.sendMessage("旧密码输入错误，请重试！");
							sender.sendMessage("重设密码，请使用如下格式：/mcshop pass 旧密码 新密码");
							return false;
						}
					}
					sender.sendMessage("设置MCShop商店密码请使用如下格式：");
					sender.sendMessage("/mcshop pass 设置密码");
					return false;
				}
				else{
					if(params == 2)
						if(operator.changePasswd(UserName,args[1])){
							sender.sendMessage("成功设置密码！");
							return true;
						}
						else{
							sender.sendMessage("密码设置出现错误！未能重设密码！");
							return false;
						}
					else{
						sender.sendMessage("设置密码，请使用如下格式：/mcshop pass 密码");
						return false;
					}
				}
			}
			return false;
		}
		return false;
	}

}