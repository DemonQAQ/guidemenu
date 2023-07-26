package yy.guidemenu;

import com.germ.germplugin.api.dynamic.gui.GermGuiScreen;
import demon.utils.gui.GuiUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import yy.guidemenu.commands.GuideCommand;
import yy.guidemenu.datamodule.CatalogueData;
import yy.guidemenu.datamodule.GuideData;
import yy.guidemenu.listener.EventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/// TODO: 2023/7/27 将教程从默认全部开放给玩家更改为只能查看解锁后的教程，用/guide show 指令来解锁教程
/*
* 注意：
* 1. 保证GuideData内静态数据和动态数据分离，不要出现n个玩家则拷贝n*guideData数量的静态数据的情况
* 2. 玩家解锁的教程储存在数据库中，数据库以及一系列通用工具的接口文档:http://47.119.161.172:90/
* 3. 在帮助界面内右侧图片上方显示每一个教程的副标题。
* 4. 检查搜索功能，如果未完成或有问题请修复他
* 5. 参考案例，原神的帮助菜单（按G打开那个）
* */

public final class Guidemenu extends JavaPlugin
{
    public static Guidemenu plugin;
    public static Logger logger;
    public static GermGuiScreen guideGui;
    public static GermGuiScreen guideTip;
    public static File config;
    public static ArrayList<CatalogueData> infos = new ArrayList<>();

    @Override
    public void onEnable()
    {
        plugin = this;
        logger = getLogger();
        commandInit();
        listenerInit();
        configInit();
    }

    private void configInit()
    {
        config = new File(Guidemenu.plugin.getDataFolder(), "config.yml");//尝试从插件的文件目录下加载config.yml
        if (!config.exists())
        {
            saveResource("config.yml", false);
        }
        catalogueInit();
        guideGui = GuiUtils.readGuiTemplate(this, "helpguide.yml", "helpguide");
        guideTip = GuiUtils.readGuiTemplate(this, "guideTip.yml", "guideTip");
    }

    private void commandInit()
    {
        //getCommand()方法检查是否在plugin.yml内注册有对应指令
        //如果注册有就会执行.setExecuter()
        //setExecuter传入一个刚刚写的执行命令的类的实例
        //就是告诉服务器注册这个指令，并且在执行的时候触发传入的实例的onCommand()方法
        Objects.requireNonNull(this.getCommand("guide"), "xxxx")
                .setExecutor(new GuideCommand());
    }

    private void catalogueInit()
    {
        Set<String> temp = getConfig().getConfigurationSection("guideList").getKeys(false);
        if (temp == null)
        {
            getConfig().set("guideList", new ArrayList<>());
        }
        else
        {
            String[] indexes = temp.toArray(new String[0]);
            String key = "guideList.";
            for (String index : indexes)
            {
                String defaultPath = getConfig().getString(key + index + ".icon.defaultPath");
                String checkedPath = getConfig().getString(key + index + ".icon.checkedPath");
                ArrayList<GuideData> guideList = guideInit(index);
                CatalogueData catalogueData = new CatalogueData(index, defaultPath, checkedPath, guideList);
                infos.add(catalogueData);
            }
        }
    }

    private ArrayList<GuideData> guideInit(String key)
    {
        Set<String> temp = getConfig().getConfigurationSection("guideList." + key + ".list").getKeys(false);
        if (temp != null)
        {
            ArrayList<GuideData> guideData = new ArrayList<>();
            for (String guideName : temp)
            {
                guideData.add(new GuideData(guideName,getConfig().getString("guideList." + key + ".list." + guideName + ".secondTitle"),
                        (ArrayList<String>) getConfig().getStringList("guideList." + key + ".list." + guideName + ".texture"), (ArrayList<String>) getConfig().getStringList("guideList." + key + ".list." + guideName + ".text")));
            }
            return guideData;
        }
        return null;
    }

    private void listenerInit()
    {
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    public static CatalogueData getCatalogueData(String catalogueName)
    {
        for (CatalogueData catalogueData : infos)
        {
            if (catalogueName.contains(catalogueData.catalogueName)) return catalogueData;
        }
        return infos.get(0);
    }

    public static GuideData getGuideData(String guideName, String catalogueName)
    {
        CatalogueData catalogueData = getCatalogueData(catalogueName);
        for (GuideData guideData : catalogueData.guides)
        {
            if (guideName.contains(guideData.guideName)) return guideData;
        }
        return infos.get(0).guides.get(0);
    }
}
