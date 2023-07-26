package yy.guidemenu.listener;

import com.germ.germplugin.api.dynamic.gui.GermGuiButton;
import com.germ.germplugin.api.dynamic.gui.GermGuiCheckbox;
import com.germ.germplugin.api.dynamic.gui.GermGuiInput;
import com.germ.germplugin.api.dynamic.gui.GermGuiScreen;
import com.germ.germplugin.api.event.gui.GermGuiButtonEvent;
import com.germ.germplugin.api.event.gui.GermGuiCheckboxEvent;
import com.germ.germplugin.api.event.gui.GermGuiInputEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import yy.guidemenu.Guidemenu;
import yy.guidemenu.uimodule.GuiController;
import yy.guidemenu.uimodule.TipGuiController;


public class EventListener implements Listener
{
    @EventHandler
    public void onTipButtonClick(GermGuiButtonEvent e)
    {
        if (e.getGermGuiScreen().getGuiName().equals(Guidemenu.guideTip.getGuiName()))
        {
            if (!(e.getEventType().equals(GermGuiButton.EventType.LEFT_CLICK))) return;
            switch (e.getGermGuiButton().getIndexName())
            {
                case "exit":
                    TipGuiController.onExit(e.getPlayer());
                    break;
                case "right":
                    TipGuiController.onClickNext(e.getGermGuiScreen(), e.getPlayer(), true);
                    break;
                case "left":
                    TipGuiController.onClickNext(e.getGermGuiScreen(), e.getPlayer(), false);
                    break;
            }
        }
    }

    @EventHandler
    public void onButtonClick(GermGuiButtonEvent e)
    {
        if (e.getGermGuiScreen().getGuiName().equals(Guidemenu.guideGui.getGuiName()))
        {
            if (!(e.getEventType().equals(GermGuiButton.EventType.LEFT_CLICK))) return;
            if (e.getGermGuiButton().getIndexName().contains("next_"))
            {
                boolean right = e.getGermGuiButton().getIndexName().contains("right");
                onNextClick(e.getGermGuiScreen(), right);
            }
        }
    }

    @EventHandler
    public void onCheckBoxClick(GermGuiCheckboxEvent e)
    {
        if (e.getGermGuiScreen().getGuiName().equals(Guidemenu.guideGui.getGuiName()))
        {
            if (e.getEventType().equals(GermGuiCheckbox.EventType.CANCELLED))
            {
                e.setCancelled(true);
                return;
            }
            else if (!(e.getEventType().equals(GermGuiCheckbox.EventType.CHECKED))) return;

            if (e.getGermGuiCheckbox().getIndexName().contains("banner_"))
                onBannerClick(e.getPlayer(), e.getGermGuiCheckbox(), e.getGermGuiScreen());
            else if (e.getGermGuiCheckbox().getIndexName().contains("list_"))
                onListClick(e.getPlayer(), e.getGermGuiCheckbox(), e.getGermGuiScreen());
        }
    }

    @EventHandler
    public void onPlayerInput(GermGuiInputEvent e)
    {
        if (e.getGermGuiScreen().getGuiName().equals(Guidemenu.guideGui.getGuiName()))
        {
            if (e.getEventType().equals(GermGuiInput.EventType.ENTER))
            {
                GuiController.showSearchedGuide(e.getGermGuiScreen(), e.getInput(), e.getPlayer());
            }
        }
    }

    private void onBannerClick(Player player, GermGuiCheckbox checkbox, GermGuiScreen gui)
    {
        if ((boolean) checkbox.getChecked())
        {
            GuiController.showBanner(gui, Guidemenu.getCatalogueData(checkbox.getIndexName()), player);
        }
    }

    private void onListClick(Player player, GermGuiCheckbox checkbox, GermGuiScreen gui)
    {
        if ((boolean) checkbox.getChecked())
        {
            GuiController.showGuide(gui, Guidemenu.getGuideData(checkbox.getIndexName(),
                    GuiController.getCheckedCatalogueName(gui)), player);
        }
    }

    private void onNextClick(GermGuiScreen gui, boolean right)
    {
        GuiController.showNextInfo(gui, right);
    }
}
