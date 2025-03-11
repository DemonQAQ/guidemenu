package yy.guidemenu.datamodule;

import java.util.ArrayList;

public class CatalogueData
{
    public String catalogueName;
    public ArrayList<GuideData> guides;
    public String defaultPath;
    public String checkPath;

    public CatalogueData(String catalogueName, String defaultPath, String checkPath, ArrayList<GuideData> guides)
    {
        this.catalogueName = catalogueName;
        this.defaultPath = defaultPath;
        this.checkPath = checkPath;
        this.guides = guides;
    }
}
