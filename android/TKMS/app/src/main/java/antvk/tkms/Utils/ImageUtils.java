package antvk.tkms.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {


    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }


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
