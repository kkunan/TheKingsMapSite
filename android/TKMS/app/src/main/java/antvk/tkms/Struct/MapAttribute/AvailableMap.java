package antvk.tkms.Struct.MapAttribute;

import java.util.List;

import antvk.tkms.Struct.Information.InformationItem;

public class AvailableMap {

    public static final String imageFolder = "footage_images/maps";

    public int mapID;
    public String mapName;
    public String imageLogo;
    public boolean local;
    public String mapImageFolder;
    public List<InformationItem> informationItems;

    public AvailableMap(int mapID, String mapName, String imageLogo, List<InformationItem> informationItems)
    {
        this.mapID = mapID;
        this.mapName = mapName;
        this.imageLogo = imageLogo;
        this.informationItems = informationItems;
    }
}
