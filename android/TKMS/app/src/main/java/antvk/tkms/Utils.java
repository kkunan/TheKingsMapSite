package antvk.tkms;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static Map<String, Drawable> getDrawables(Context context, String imageFolder, String[] names) {
        Map<String, Drawable> da = new HashMap<>();
        for (String name : names) {
            try {
                Drawable d = getDrawable(context, imageFolder, name);
                if (d != null) da.put(name, d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return da;
    }

    // this is just a temporary method put here to load in the images
    public static Drawable getDrawable(Context context, String imageFolder, String name) {
        try {
            InputStream inputstream = context.getAssets().open(imageFolder + "/" + name);
            System.out.println("input stream: "+inputstream);
            Drawable drawable = Drawable.createFromStream(inputstream, null);
            System.out.println("drawable: "+drawable);            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
