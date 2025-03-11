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

    private static void init(GermGuiScreen gui, Player player)
    {
        showCatalogueList(player, gui);
        showGuideList(gui, Guidemenu.infos.get(0).guides, player);
    }

    private static void showCatalogueList(Player player, GermGuiScreen gui)
    {
        GermGuiCheckbox template = (GermGuiCheckbox) Guidemenu.guideGui.getGuiPart("bannerTemplate");
        int offset = 32;
        int iconWidth = 128;
        int index = 0;
        for (CatalogueData catalogueData : Guidemenu.infos)
        {
            GermGuiCheckbox icon = template.clone();
            icon.setDefaultPath(catalogueData.defaultPath);
            icon.setHoverPath(catalogueData.checkPath);
            icon.setCheckedPath(catalogueData.checkPath);
            //icon.setLocationX((860+offset*index+iconWidth*index) + "*(W/1920)");
            double width = GuiManager.getMCWidth(player);
            double x = (860 + offset * index + iconWidth * index) * (width / 1920);
            icon.setLocationX(x);
            if (index == 0) icon.setChecked(true);
            icon.setEnable(true);
            icon.setIndexName("banner_" + catalogueData.catalogueName);
            gui.addGuiPart(icon);
            index++;
        }
    }

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
            if (index == 0) guide.setChecked(true);
            guide.setEnable(true);
            guide.setIndexName("list_" + guideDataArrayList.get(index).guideName);
            guideList.addGuiPart(guide);
        }
        showGuide(gui, guideDataArrayList.get(0), player);
    }

    public static void showGuide(GermGuiScreen gui, GuideData guideData, Player player)
    {
        displayGuideDefaultInformation(gui, guideData);
        flashGuideCheckedState(getGuideList(gui), guideData.guideName);
        removeTipSlots(gui, getTipSlots(gui));
        showTipSlots(gui, guideData, player);
    }
    
    public static void showNextInfo(GermGuiScreen gui, boolean right)
    {
        updateGuideInformation(gui, right);
        updateTipSlot(gui);
    }

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

    private static void showTipSlots(GermGuiScreen gui, GuideData guideData, Player player)
    {
        GermGuiTexture tipSlotTemplate = (GermGuiTexture) gui.getGuiPart("tipSlotTemplate");
        int offset = 16;
        int width = 36;
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

    private static void displayGuideDefaultInformation(GermGuiScreen gui, GuideData guideData)
    {
        GermGuiTexture texture = (GermGuiTexture) gui.getGuiPart("texture");
        GermGuiLabel tip = (GermGuiLabel) gui.getGuiPart("tip");
        texture.setPath(guideData.getShowTexturePath());
        tip.setText(guideData.getShowText());
    }

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

    private static void flashGuideCheckedState(ArrayList<GermGuiCheckbox> list, String name)
    {
        for (GermGuiCheckbox checkbox : list) checkbox.setChecked(checkbox.getIndexName().contains(name));
    }

    private static void updateTipSlot(GermGuiScreen gui)
    {
        GuideData guideData = Guidemenu.getGuideData(getCheckedGuideName(gui), getCheckedCatalogueName(gui));
        if (guideData == null) return;
        ArrayList<GermGuiTexture> tipSlots = getTipSlots(gui);
        updateCheckedTipSlot(tipSlots, guideData.getIndex());
    }

    protected static void updateCheckedTipSlot(ArrayList<GermGuiTexture> tipSlots, int index)
    {
        for (GermGuiTexture tipSlot : tipSlots) tipSlot.setPath("local<->textures/help/tipslot.png");
        tipSlots.get(index).setPath("local<->textures/help/tipslot_down.png");
    }

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

    private static void removeTipSlots(GermGuiScreen gui, ArrayList<GermGuiTexture> tipSlots)
    {
        for (GermGuiTexture tipSlot : tipSlots)
        {
            gui.removeGuiPart(tipSlot);
        }
        gui.getGuiPart("next_left").setEnable(false);
        gui.getGuiPart("next_right").setEnable(false);
    }

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

    private static void removeGuideList(GermGuiScreen gui, ArrayList<GermGuiCheckbox> guideList)
    {
        GermGuiScroll scroll = (GermGuiScroll) gui.getGuiPart("buttonList");
        for (GermGuiCheckbox guide : guideList)
        {
            if (guide.getIndexName().contains("list_")) scroll.removeGuiPart(guide);
        }
    }

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

    public static String getCheckedCatalogueName(GermGuiScreen gui)
    {
        ArrayList<GermGuiCheckbox> banners = getCatalogueList(gui);
        for (GermGuiCheckbox checkbox : banners)
        {
            if ((boolean) checkbox.getChecked()) return checkbox.getIndexName();
        }
        return banners.get(0).getIndexName();
    }

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
