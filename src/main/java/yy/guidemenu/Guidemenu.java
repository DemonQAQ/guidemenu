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
