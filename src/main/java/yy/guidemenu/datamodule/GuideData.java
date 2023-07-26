package yy.guidemenu.datamodule;

import java.util.ArrayList;

public class GuideData
{
    public String guideName;
    public String secondTitle;
    private ArrayList<String> textures;
    private ArrayList<String> texts;
    private int index;

    public GuideData(String guideName, String secondTitle, ArrayList<String> textures, ArrayList<String> texts)
    {
        this.guideName = guideName;
        this.textures = textures;
        this.texts = texts;
        this.index = 0;
    }

    public int getIndex()
    {
        return index;
    }

    public void reset()
    {
        index = 0;
    }

    public void next()
    {
        index = ++index % texts.size();
    }

    public boolean nextNoLoop()
    {
        ++index;
        return index >= texts.size() - 1;
    }

    public void previous()
    {
        index = (index - 1 + texts.size()) % texts.size();
    }

    public boolean previousNoLoop()
    {
        if (index == 0) return true;
        --index;
        boolean result = index <= 0;
        if (result) index = 0;
        return result;
    }

    public String getShowText()
    {
        return texts.get(index);
    }

    public String getShowTexturePath()
    {
        if (texts.size() == 1 || index > texts.size()) return texts.get(0);
        return texts.get(index);
    }

    public int getGuideSize()
    {
        return texts.size();
    }
}
