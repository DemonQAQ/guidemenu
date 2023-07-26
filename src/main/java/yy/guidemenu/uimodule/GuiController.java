package yy.guidemenu.uimodule;

import com.germ.germplugin.api.dynamic.DynamicBase;
import com.germ.germplugin.api.dynamic.gui.*;
import org.bukkit.entity.Player;
import yy.guidemenu.Guidemenu;
import yy.guidemenu.datamodule.CatalogueData;
import yy.guidemenu.datamodule.GuideData;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiController
{
    /*
     * 在触发指令时调用。
     * 功能：先查找系统内是否存有该玩家对应的gui对象，如有则用此对象打开gui给玩家，如无则新建一个对象再打开gui给玩家
     * */
    public static void display(Player player)
    {
        GermGuiScreen gui = getHelpGuideGui(player);
        init(gui, player);
        gui.openGui(player);
    }

    private static GermGuiScreen getHelpGuideGui(Player player)
    {
        GermGuiScreen gui = GuiManager.getOpenedGui(player, "helpguide");
        if (gui != null) return gui;
        return Guidemenu.guideGui.clone();
    }

    /*
     * 功能：初始化gui界面并将gui对象加入系统
     * */
    private static void init(GermGuiScreen gui, Player player)
    {
        showCatalogueList(player, gui);
        showGuideList(gui, Guidemenu.infos.get(0).guides, player);
    }

    /*
     * 功能：显示界面上方的教程分类图标，并默认选中第一个。
     * */
    private static void showCatalogueList(Player player, GermGuiScreen gui)
    {
        //从gui配置里读取索引名为bannerTemplate的组件进来，根据配置的type属性可知会获得一个GermGuiCheckbox对象
        GermGuiCheckbox template = (GermGuiCheckbox) Guidemenu.guideGui.getGuiPart("bannerTemplate");
        int offset = 32;
        int iconWidth = 128;
        int index = 0;
        //遍历主类里存的目录的信息，根据目录信息生成对应的复选按钮并且加入引导的ui中
        for (CatalogueData catalogueData : Guidemenu.infos)
        {
            //从模板克隆一个对象出来，一个对象代表一个按钮
            GermGuiCheckbox icon = template.clone();
            icon.setDefaultPath(catalogueData.defaultPath);
            icon.setHoverPath(catalogueData.checkPath);
            icon.setCheckedPath(catalogueData.checkPath);
            //设置按钮的x坐标，按钮直接间隔一个固定值，假设a按钮的x坐标为860，a按钮的宽度为128，按钮间间隔为160
            //往x轴正方向生成，那么b按钮的x坐标就等于(860+128*1+160*1)，c按钮的x坐标就等于(860+128*2+160*2)
            //必须理解这个公式是什么意思，生成列表类的坐标计算都是这个逻辑
            //最后的W/1920,w代表玩家屏幕的宽度，1920代表ui的默认尺寸，将得出的坐标乘以这个系数，就能保证ui在任何尺寸下都有正确的位置
            //icon.setLocationX((860+offset*index+iconWidth*index) + "*(W/1920)");
            double width = GuiManager.getMCWidth(player);
            double x = (860 + offset * index + iconWidth * index) * (width / 1920);
            icon.setLocationX(x);
            if (index == 0) icon.setChecked(true);
            icon.setEnable(true);
            //设置组件的索引名，后续监听按键事件的时候就依靠这个判断
            icon.setIndexName("banner_" + catalogueData.catalogueName);
            //将设置好的组件加入ui里
            gui.addGuiPart(icon);
            index++;
        }
    }

    /*
     * 用法：参数catalogueData传入null自动获取默认值
     * 功能：显示左侧的教程按钮列表
     * */
    private static void showGuideList(GermGuiScreen gui, ArrayList<GuideData> guideDataArrayList, Player player)
    {
        ((GermGuiLabel) gui.getGuiPart("searchNo")).setEnable(false);
        GermGuiScroll guideList = (GermGuiScroll) gui.getGuiPart("buttonList");
        GermGuiCheckbox template = (GermGuiCheckbox) guideList.getGuiPart("buttonTemplate");
        int height = 128;
        for (int index = 0; index < guideDataArrayList.size(); index++)
        {
            assert template != null;
            GermGuiCheckbox guide = template.clone();
            guide.setText(guideDataArrayList.get(index).guideName);
            double width = GuiManager.getMCWidth(player);
            guide.setLocationY((4 + height * index) * (width / 1920));
            //index等于0的时候代表第一个引导。界面第一次打开时，默认选中第一个分类的第一个引导教程
            if (index == 0) guide.setChecked(true);
            guide.setEnable(true);
            guide.setIndexName("list_" + guideDataArrayList.get(index).guideName);
            guideList.addGuiPart(guide);
        }
        showGuide(gui, guideDataArrayList.get(0), player);
    }

    /*
     * 在点击分类按钮时，界面初始化时，点击教程按钮时调用
     * 功能：显示教程内容
     * */
    public static void showGuide(GermGuiScreen gui, GuideData guideData, Player player)
    {
        displayGuideDefaultInformation(gui, guideData);
        flashGuideCheckedState(getGuideList(gui), guideData.guideName);
        removeTipSlots(gui, getTipSlots(gui));
        showTipSlots(gui, guideData, player);
    }

    /*
     * 在点击下一条/上一条时调用
     * 功能：显示教程下一条信息
     * */
    public static void showNextInfo(GermGuiScreen gui, boolean right)
    {
        updateGuideInformation(gui, right);
        updateTipSlot(gui);
    }

    /*
     * 在点击分类按钮时调用
     * 功能：显示某个分类的所有教程
     * */
    public static void showBanner(GermGuiScreen gui, CatalogueData catalogueData, Player player)
    {
        removeGuideList(gui, getGuideList(gui));
        flashGuideCheckedState(getCatalogueList(gui), catalogueData.catalogueName);
        showGuideList(gui, catalogueData.guides, player);
    }

    public static void showSearchedGuide(GermGuiScreen gui, String searchName, Player player)
    {
        removeGuideList(gui, getGuideList(gui));
        removeTipSlots(gui, getTipSlots(gui));
        CatalogueData catalogueData = Guidemenu.getCatalogueData(getCheckedCatalogueName(gui));
        if (searchName == null) showGuideList(gui, catalogueData.guides, player);
        else searchAndShowGuide(gui, catalogueData, searchName, player);
    }

    private static ArrayList<GuideData> searchGuide(CatalogueData catalogueData, String searchName)
    {
        ArrayList<GuideData> results = new ArrayList<>();
        for (GuideData guideData : catalogueData.guides)
        {
            if (guideData.guideName.contains(searchName)) results.add(guideData);
        }
        return results;
    }

    private static void searchAndShowGuide(GermGuiScreen gui, CatalogueData catalogueData, String searchName, Player player)
    {
        ArrayList<GuideData> results = searchGuide(catalogueData, searchName);
        if (results.size() == 0)
        {
            ((GermGuiTexture) gui.getGuiPart("texture")).setPath("'local<->textures/help/noResult.png'");
            ((GermGuiLabel) gui.getGuiPart("tip")).setText("什么也没有...");
            ((GermGuiLabel) gui.getGuiPart("searchNo")).setEnable(true);
            gui.openGui(gui.getPlayer());
        }
        else showGuideList(gui, results, player);
    }

    /*
     * 功能：根据教程的数量添加提示图标，并默认选中第一个
     * */
    private static void showTipSlots(GermGuiScreen gui, GuideData guideData, Player player)
    {
        GermGuiTexture tipSlotTemplate = (GermGuiTexture) gui.getGuiPart("tipSlotTemplate");
        int offset = 16;
        int width = 36;
        //判断教程条数的数量，奇数和偶数的起始坐标不同，一个教程内只支持1-9条内容。一条内容 = 一张图片 + 一段文字
        //从中间开始生成比如5条，从第3条开始生成，9条从第5条开始生成
        //一条生成一个tipSlot
        int x;
        if (guideData.getGuideSize() % 2 == 0)
        {
            x = 1200 - (guideData.getGuideSize() / 2 - 1) * (offset + width);
        }
        else
        {
            x = 1226 - (guideData.getGuideSize() / 2) * (offset + width);
        }

        double scale = GuiManager.getMCWidth(player) / 1920;
        for (int i = 0; i < guideData.getGuideSize(); i++)
        {
            GermGuiTexture tipSlot = tipSlotTemplate.clone();
            if (i == 0) tipSlot.setPath("local<->textures/help/tipslot_down.png");
            tipSlot.setLocationX((x + offset * i + width * i) * scale);
            tipSlot.setIndexName("tipSlot_" + i);
            tipSlot.setEnable(true);
            gui.addGuiPart(tipSlot);
        }

        if (guideData.getGuideSize() > 1)
        {
            gui.getGuiPart("next_left").setEnable(true);
            gui.getGuiPart("next_right").setEnable(true);
        }
    }

    /*
     * 功能：在gui上显示传入的教程数据中的第一条。
     * */
    private static void displayGuideDefaultInformation(GermGuiScreen gui, GuideData guideData)
    {
        GermGuiTexture texture = (GermGuiTexture) gui.getGuiPart("texture");
        GermGuiLabel tip = (GermGuiLabel) gui.getGuiPart("tip");
        texture.setPath(guideData.getShowTexturePath());
        tip.setText(guideData.getShowText());
    }

    /*
     * 功能：更新右侧显示的信息
     * */
    private static void updateGuideInformation(GermGuiScreen gui, boolean right)
    {
        GuideData guideData = Guidemenu.getGuideData(getCheckedGuideName(gui), getCheckedCatalogueName(gui));
        if (guideData == null) return;
        if (right) guideData.next();
        else guideData.previous();
        GermGuiTexture texture = (GermGuiTexture) gui.getGuiPart("texture");
        GermGuiLabel tip = (GermGuiLabel) gui.getGuiPart("tip");
        texture.setPath(guideData.getShowTexturePath());
        tip.setText(guideData.getShowText());
    }

    /*
     * 功能：操作传入的复选框列表，对于索引名包含name的复选框设置其checked属性为true，其余复选框checked属性设置为false
     * */
    private static void flashGuideCheckedState(ArrayList<GermGuiCheckbox> list, String name)
    {
        for (GermGuiCheckbox checkbox : list) checkbox.setChecked(checkbox.getIndexName().contains(name));
    }

    /*
     * 功能：更新提示图标的选中状态。先更新选中的下标，再根据选中的下标更新图标的显示效果。
     * */
    private static void updateTipSlot(GermGuiScreen gui)
    {
        GuideData guideData = Guidemenu.getGuideData(getCheckedGuideName(gui), getCheckedCatalogueName(gui));
        if (guideData == null) return;
        ArrayList<GermGuiTexture> tipSlots = getTipSlots(gui);
        updateCheckedTipSlot(tipSlots, guideData.getIndex());
    }

    /*
     * 功能：操作传入的图标列表，将下标index的图标的图片路径设置为选中，其余图标的图片路径设置为未选中
     * */
    protected static void updateCheckedTipSlot(ArrayList<GermGuiTexture> tipSlots, int index)
    {
        for (GermGuiTexture tipSlot : tipSlots) tipSlot.setPath("local<->textures/help/tipslot.png");
        tipSlots.get(index).setPath("local<->textures/help/tipslot_down.png");
    }


    /*
     * 功能：返回当前gui内的tipSlot列表
     * */
    private static ArrayList<GermGuiTexture> getTipSlots(GermGuiScreen gui)
    {
        List<GermGuiPart<? extends DynamicBase>> temp = gui.getGuiParts();
        ArrayList<GermGuiTexture> tipSlots = new ArrayList<>();
        for (GermGuiPart<? extends DynamicBase> t : temp)
        {
            if (t instanceof GermGuiTexture && t.getIndexName().contains("tipSlot_"))
                tipSlots.add((GermGuiTexture) t);
        }
        return tipSlots;
    }

    /*
     * 功能：移除gui内的tipSlot
     * */
    private static void removeTipSlots(GermGuiScreen gui, ArrayList<GermGuiTexture> tipSlots)
    {
        for (GermGuiTexture tipSlot : tipSlots)
        {
            gui.removeGuiPart(tipSlot);
        }
        gui.getGuiPart("next_left").setEnable(false);
        gui.getGuiPart("next_right").setEnable(false);
    }

    /*
     * 功能：查找某个教程的按钮
     * */
    private static GermGuiCheckbox getGuideCheckBox(GermGuiScreen gui, String name)
    {
        GermGuiScroll scroll = (GermGuiScroll) gui.getGuiPart("buttonList");
        List<GermGuiPart<? extends DynamicBase>> temp = scroll.getGuiParts();
        for (GermGuiPart<? extends DynamicBase> t : temp)
        {
            if (t instanceof GermGuiCheckbox && t.getIndexName().equals("list_" + name))
                return (GermGuiCheckbox) t;
        }
        return (GermGuiCheckbox) temp.get(0);
    }

    /*
     * 功能：获取教程按钮的列表
     * */
    private static ArrayList<GermGuiCheckbox> getGuideList(GermGuiScreen gui)
    {
        GermGuiScroll scroll = (GermGuiScroll) gui.getGuiPart("buttonList");
        List<GermGuiPart<? extends DynamicBase>> temp = scroll.getGuiParts();
        ArrayList<GermGuiCheckbox> guideList = new ArrayList<>();
        for (GermGuiPart<? extends DynamicBase> t : temp)
        {
            if (t instanceof GermGuiCheckbox && !(t.getIndexName().equals("buttonTemplate")))
                guideList.add((GermGuiCheckbox) t);
        }
        return guideList;
    }

    /*
     * 功能：清空教程按钮的列表
     * */
    private static void removeGuideList(GermGuiScreen gui, ArrayList<GermGuiCheckbox> guideList)
    {
        GermGuiScroll scroll = (GermGuiScroll) gui.getGuiPart("buttonList");
        for (GermGuiCheckbox guide : guideList)
        {
            if (guide.getIndexName().contains("list_")) scroll.removeGuiPart(guide);
        }
    }

    /*
     * 功能：获取分类的列表
     * */
    private static ArrayList<GermGuiCheckbox> getCatalogueList(GermGuiScreen gui)
    {
        List<GermGuiPart<? extends DynamicBase>> temp = gui.getGuiParts();
        ArrayList<GermGuiCheckbox> guideList = new ArrayList<>();
        for (GermGuiPart<? extends DynamicBase> t : temp)
        {
            if (t instanceof GermGuiCheckbox && (t.getIndexName().contains("banner_")))
                guideList.add((GermGuiCheckbox) t);
        }
        return guideList;
    }

    /*
     * 功能：获取当前选中的分类的名称
     * */
    public static String getCheckedCatalogueName(GermGuiScreen gui)
    {
        ArrayList<GermGuiCheckbox> banners = getCatalogueList(gui);
        for (GermGuiCheckbox checkbox : banners)
        {
            if ((boolean) checkbox.getChecked()) return checkbox.getIndexName();
        }
        return banners.get(0).getIndexName();
    }

    /*
     * 功能：获取当前选中的教程的名称
     * */
    public static String getCheckedGuideName(GermGuiScreen gui)
    {
        ArrayList<GermGuiCheckbox> guides = getGuideList(gui);
        for (GermGuiCheckbox checkbox : guides)
        {
            if ((boolean) checkbox.getChecked()) return checkbox.getIndexName();
        }
        return guides.get(0).getIndexName();
    }
}
