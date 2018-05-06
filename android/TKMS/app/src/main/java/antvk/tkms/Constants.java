package antvk.tkms;

import android.os.Build;
import android.support.annotation.RequiresApi;

public interface Constants {

    static final String FOOTAGE_IMAGE = "footage_images";
    static final String PIN_FOLDER = FOOTAGE_IMAGE+"/marker";

    static final String KINGS_MAP_FILE = "info.json";
    static final String DESTINY_MAP_FILE = "destiny.json";

    static final String KINGS_IMAGE_FOLDER = "kings_images";
    static final String DESTINY_IMAGE_FOLDER = "destiny_images";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getFileName(int index)
    {
        if(index==0)
           return KINGS_MAP_FILE;

        else if(index==1)
        {
            return DESTINY_MAP_FILE;
        }

        return "";
    }
}
