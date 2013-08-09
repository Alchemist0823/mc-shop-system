package com.n8lm.MCShopSystemPlugin.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.n8lm.MCShopSystemPlugin.MainPlugin;
import com.n8lm.MCShopSystemPlugin.operator.AccountHandler;

public class UserCommandListener implements Listener {
	
	@EventHandler(priority=EventPriority.LOW)
	public void preCommandListener(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) {
			return;
		}
		String[] args = event.getMessage().split(" ");
		if (args[0].equalsIgnoreCase("/" + MainPlugin.getSettings().getCommand())) {

			int params = args.length;
			Player player = event.getPlayer();
			String UserName = player.getName();

			AccountHandler operator = MainPlugin.getAccountHandler();
			if(operator.hasPassword(UserName)) {
				if(params == 3) {
					if(operator.checkPassword(UserName,args[1])) {
						if(operator.changePassword(UserName,args[2])) {
							player.sendMessage("您已重设密码！");
							// true
						}
						else {
							player.sendMessage("密码设置出现错误！未能重设密码！");
						}
					}
					else {
						player.sendMessage("旧密码输入错误，请重试！");
						player.sendMessage("重设密码，请使用如下格式：/" + MainPlugin.getSettings().getCommand() +" 旧密码 新密码");
					}
				}
				else {
					player.sendMessage("您已设置密码。 请登陆网站注册用户 " + MainPlugin.getSettings().getURL());
					player.sendMessage("若想重设密码，请使用如下格式：/" + MainPlugin.getSettings().getCommand() +" 已有密码 新密码");
					player.sendMessage("若忘记旧密码，请同管理员联系");
				}
			}
			else{
				if(params == 2)
					if(operator.register(UserName,args[1])) {
						player.sendMessage("成功设置密码！");
						player.sendMessage("请登陆网站注册用户 " + MainPlugin.getSettings().getURL());
						// true
					}
					else{
						player.sendMessage("密码设置出现错误！未能重设密码！");
					}
				else{
					player.sendMessage("设置密码，请使用如下格式：/" + MainPlugin.getSettings().getCommand() +" 密码");
				}
			}

			event.setCancelled(true);
			return;
		}
	}
}
