package antvk.tkms;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.SimpleDateFormat;

public interface Constants {

    float STREET_LEVEL_ZOOM = 15.0f;

    static final String FOOTAGE_IMAGE = "footage_images";
    static final String PIN_FOLDER = FOOTAGE_IMAGE+"/marker";

    static final String KINGS_MAP_FILE = "info.json";
    static final String DESTINY_MAP_FILE = "destiny.json";
    static final String TENCENT_MAP_FILE = "tencent.json";

    static final String KINGS_IMAGE_FOLDER = "kings_images";
    static final String DESTINY_IMAGE_FOLDER = "destiny_images";
    static final String TENCENT_IMAGE_FOLDER = "tencent_images";

    String PLACE_CATEGORY_PATH = FOOTAGE_IMAGE+"/place_category";

    static final String DEFAULT_NEW_MAP_IMAGEPATH = FOOTAGE_IMAGE+"/icon_mymap_01.png";

    // TODO: 07/05/2018 fill local image place
    static final String LOCAL_IMAGE_FOLDER = "";

    static final SimpleDateFormat DMY_DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getFileName(int index, SharedPreferences preferences)
    {
        if(index==0)
           return KINGS_MAP_FILE;

        else if(index==1)
        {
            return DESTINY_MAP_FILE;
        }

        else if(index==2)
        {
            return TENCENT_MAP_FILE;
        }

        else
        {
            return preferences.getString("map"+index,null);
        }
    }

    int MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE = 5555;
    int PICK_IMAGE = 1;
}
