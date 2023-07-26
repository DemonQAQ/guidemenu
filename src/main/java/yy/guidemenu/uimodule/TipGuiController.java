package yy.guidemenu.uimodule;

import com.germ.germplugin.api.dynamic.gui.*;
import org.bukkit.entity.Player;
import yy.guidemenu.Guidemenu;
import yy.guidemenu.datamodule.GuideData;

import java.util.HashMap;

/**
 * guidemenu yy.guidemenu.uimodule
 *
 * @author Demon
 * 2023/7/27 0:53
 */
public abstract class TipGuiController
{
    private static final HashMap<Player, GuideData> showGuide = new HashMap<>();

    public static void display(Player player, String catalogueName, String guideName)
    {
        GermGuiScreen gui = Guidemenu.guideTip.clone();
        init(gui, player, catalogueName, guideName);
        gui.openGui(player);
    }

    public static void onExit(Player player)
    {
        showGuide.remove(player);
    }

    public static void onClickNext(GermGuiScreen gui, Player player, boolean isNext)
    {
        GuideData guideData = showGuide.get(player);
        if (guideData == null) return;
        if (isNext)
        {
            if (guideData.nextNoLoop())
            {
                gui.getGuiPart("exit").setEnable(true);
                gui.getGuiPart("right").setEnable(false);
            }
            gui.getGuiPart("left").setEnable(true);
        }
        else
        {
            if (guideData.previousNoLoop())
            {
                gui.getGuiPart("left").setEnable(false);
            }
            gui.getGuiPart("right").setEnable(true);
        }
        showGuideInfo(gui, guideData);
        updateTipSlots(gui, guideData);
    }

    private static void showGuideInfo(GermGuiScreen gui, GuideData guideData)
    {
        ((GermGuiLabel) gui.getGuiPart("title")).setText(guideData.guideName);
        ((GermGuiLabel) gui.getGuiPart("secondTitle")).setText(guideData.secondTitle);
        ((GermGuiTexture) gui.getGuiPart("texture")).setPath(guideData.getShowTexturePath());
        ((GermGuiLabel) gui.getGuiPart("label1")).setText(guideData.getShowText());
    }

    private static void init(GermGuiScreen gui, Player player, String catalogueName, String guideName)
    {
        GuideData guideData = Guidemenu.getGuideData(guideName, catalogueName);
        if (guideData == null) return;
        showGuide.put(player, guideData);
        guideData.reset();
        showGuideInfo(gui, guideData);
        showTipSlots(gui, guideData, player);
    }

    private static void updateTipSlots(GermGuiScreen gui, GuideData guideData)
    {
        String index = String.valueOf(guideData.getIndex());
        for (GermGuiTexture texture : ((GermGuiCanvas) gui.getGuiPart("canvas")).getGuiParts(GermGuiTexture.class))
        {
            if (texture.getIndexName().contains(index))
            {
                texture.setPath("local<->textures/help/tipslot_down.png");
            }
            else texture.setPath("local<->textures/help/tipslot.png");
        }
    }

    private static void showTipSlots(GermGuiScreen gui, GuideData guideData, Player player)
    {
        GermGuiCanvas canvas = (GermGuiCanvas) gui.getGuiPart("canvas");
        GermGuiTexture tipSlotTemplate = (GermGuiTexture) canvas.getGuiPart("starTemplate");
        int offset = 16;
        int width = 36;
        int x;
        if (guideData.getGuideSize() % 2 == 0)
        {
            x = 916 - (guideData.getGuideSize() / 2 - 1) * (offset + width);
        }
        else
        {
            x = 942 - (guideData.getGuideSize() / 2) * (offset + width);
        }

        double scale = GuiManager.getMCWidth(player) / 1920;
        for (int i = 0; i < guideData.getGuideSize(); i++)
        {
            GermGuiTexture tipSlot = tipSlotTemplate.clone();
            if (i == 0) tipSlot.setPath("local<->textures/help/tipslot_down.png");
            tipSlot.setLocationX((x + offset * i + width * i) * scale);
            tipSlot.setIndexName("tipSlot_" + i);
            tipSlot.setEnable(true);
            canvas.addGuiPart(tipSlot);
        }

        if (guideData.getGuideSize() > 1)
        {
            gui.getGuiPart("left").setEnable(true);
            gui.getGuiPart("right").setEnable(true);
        }
    }
}
