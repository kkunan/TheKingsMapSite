package antvk.tkms.Struct;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import antvk.tkms.Activities.ActivityWithBackButton;
import antvk.tkms.Activities.Show.MapSelectorActivity;
import antvk.tkms.Constants;

import static antvk.tkms.Constants.mapFile;

public class AvailableMap {

    public static final String imageFolder = "footage_images/maps";

    public int mapID = -1;
    public String mapName;
    public String imageLogo;
    public boolean local;
    public String mapImageFolder;
    public List<PlaceItem> placeItems;

    static Type listType = new TypeToken<ArrayList<AvailableMap>>() {
    }.getType();

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


    public static List<AvailableMap> getExternalMaps(Context context)
    {
        List<AvailableMap> items = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(mapFile);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String st = "";

            StringBuilder buffer = new StringBuilder();
            while ((st = reader.readLine()) != null) {
                buffer.append(st).append("\n");
            }

            items = new Gson().fromJson(buffer.toString(), listType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static List<AvailableMap> getAllItems(SharedPreferences preferences,Context context) {


        List<AvailableMap> localMaps = AvailableMap.getLocalMaps(preferences,context);
        List<AvailableMap> items = getExternalMaps(context);

        localMaps.addAll(items);

        return localMaps;
    }

    public static List<AvailableMap> getLocalMaps(SharedPreferences preferences, Context context) {
        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String st = preferences.getString(ActivityWithBackButton.MAP_PREF, null);

        if (st != null && st.length() > 0) {

            return new Gson().fromJson(st, listType);
        }
        return new ArrayList<>();
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

}
