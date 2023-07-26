package yy.guidemenu.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yy.guidemenu.uimodule.GuiController;
import yy.guidemenu.uimodule.TipGuiController;

public class GuideCommand implements CommandExecutor
{//实现了commandExecuter接口的类 它的onCommand方法会在执行指令时被调用

    //命令方法 会在mc出现指令时触发
    //第一个参数sender是命令发送者
    //第二个参数command是指令 如/guide open这个指令 此时command==guide
    //第四个参数args[]是命令 此时args[0]是open
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equals("guide"))
        {//判断是否为该插件的指令
            if (args == null || args.length == 0)
            {
                displayInfo((Player) sender);
                return false;
            }
            else if (args[0].equalsIgnoreCase("open") && sender instanceof Player)//判断命令行是否为open
            {
                display((Player) sender);//将gui呈现给玩家
            }
            else if (args[0].equalsIgnoreCase("show") && sender instanceof Player)
            {
                if (args.length != 3)
                {
                    sender.sendMessage("参数数量错误");
                    return false;
                }
                showGuideTip((Player) sender, args[1], args[2]);
            }
        }

        return false;
    }

    private void displayInfo(Player player)
    {
        player.sendMessage("/guide open    --  打开帮助教程界面");
        player.sendMessage("/guide show 目录名 教程名    --  为玩家显示一个教程");
    }

    //获取了一个gui界面的实例之后
    //调用gui实例的openGui方法，传入玩家
    //实现把gui显示给某个玩家
    private void display(Player player)
    {
        GuiController.display(player);
    }

    private void showGuideTip(Player player, String catalogueName, String guideName)
    {
        TipGuiController.display(player, catalogueName, guideName);
    }
}
