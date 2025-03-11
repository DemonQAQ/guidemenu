package yy.guidemenu.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yy.guidemenu.uimodule.GuiController;
import yy.guidemenu.uimodule.TipGuiController;

public class GuideCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equals("guide"))
        {
            if (args == null || args.length == 0)
            {
                displayInfo((Player) sender);
                return false;
            }
            else if (args[0].equalsIgnoreCase("open") && sender instanceof Player)
            {
                display((Player) sender);
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

    private void display(Player player)
    {
        GuiController.display(player);
    }

    private void showGuideTip(Player player, String catalogueName, String guideName)
    {
        TipGuiController.display(player, catalogueName, guideName);
    }
}
