package antvk.tkms.Struct.MapAttribute;

import java.util.ArrayList;
import java.util.List;

import antvk.tkms.Struct.Information.PlaceItem;

public class AvailableMap {

    public static final String imageFolder = "footage_images/maps";

    public int mapID = -1;
    public String mapName;
    public String imageLogo;
    public boolean local;
    public String mapImageFolder;
    public List<PlaceItem> placeItems;

    public AvailableMap() {
        this.placeItems = new ArrayList<>();
    }

    public AvailableMap(int mapID, String mapName, String imageLogo, List<PlaceItem> placeItems) {
        this.mapID = mapID;
        this.mapName = mapName;
        this.imageLogo = imageLogo;
        this.placeItems = placeItems;
        this.local = true;
    }

    public double getVisitedRatio() {
        double total = (double) placeItems.size();

        if (total == 0)
            return 0;

        double visitedCount = 0.0;
        for (PlaceItem information : placeItems) {
            if (information.visited)
                visitedCount++;
        }

        return visitedCount / total;
    }

    public List<PlaceItem> getVisitedList()
    {
        List<PlaceItem> visitedList = new ArrayList<>();
        for (PlaceItem information : placeItems) {
            if (information.visited)
                visitedList.add(information);
        }
        return visitedList;
    }

    public String toPreferenceKey()
    {
        String local = this.local?"local":"external";
        return local+"_"+mapID;
    }

}
