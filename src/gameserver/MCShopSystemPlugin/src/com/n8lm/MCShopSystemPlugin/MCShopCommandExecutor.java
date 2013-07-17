package com.n8lm.MCShopSystemPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
/*import org.bukkit.entity.Player;

import com.n8lm.MCShopSystemPlugin.FileOperator.PasswordOperator;*/

public class MCShopCommandExecutor implements CommandExecutor {
 
	//private PasswordOperator operator = MainPlugin.getPasswordOperator();
 
	public MCShopCommandExecutor() {
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		/*if (cmd.getName().equalsIgnoreCase(MainPlugin.getSettings().getCommand())){

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
						sender.sendMessage("您已设置密码。 请登陆网站注册用户" + MainPlugin.getSettings().getURL());
						sender.sendMessage("若想重设密码，请使用如下格式：/" + MainPlugin.getSettings().getCommand() +" pass 已有密码 新密码");
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
							sender.sendMessage("重设密码，请使用如下格式：/" + MainPlugin.getSettings().getCommand() +" pass 旧密码 新密码");
							return false;
						}
					}
					sender.sendMessage("设置MCShop商店密码请使用如下格式：");
					sender.sendMessage("/" + MainPlugin.getSettings().getCommand() +" pass 设置密码");
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
						sender.sendMessage("设置密码，请使用如下格式：/" + MainPlugin.getSettings().getCommand() +" pass 密码");
						return false;
					}
				}
			}
			return false;
		}
		else*/ 
		if (cmd.getName().equalsIgnoreCase("mymcshop"))
		{
			sender.sendMessage("Please edit the configuration file.");
			sender.sendMessage("MyMCShopSystem Plugin created by No.8 Lightning Man Studio");
			return true;
		}
		return false;
	}

}