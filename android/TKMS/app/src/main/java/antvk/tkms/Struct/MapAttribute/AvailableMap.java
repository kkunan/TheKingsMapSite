package antvk.tkms.Struct.MapAttribute;

public class AvailableMap {

    public static final String imageFolder = "footage_images/maps";

    public int mapID;
    public String mapName;
    public String imageLogo;

    public AvailableMap(int mapID, String mapName, String imageLogo)
    {
        this.mapID = mapID;
        this.mapName = mapName;
        this.imageLogo = imageLogo;
    }
}
