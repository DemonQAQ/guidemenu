package yy.guidemenu.datamodule;

import java.util.ArrayList;

public class CatalogueData
{
    public String catalogueName;//用来区分不同教程分类
    public ArrayList<GuideData> guides;//用来存储读取进来的GuideData对象

    //这个位置的两个数据是用来切换分类的图标图片的 所以icon里的数据条数是固定的
    //直接用String储存
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
