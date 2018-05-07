package antvk.tkms.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.Window;
//
//import com.facebook.share.model.SharePhoto;
//import com.facebook.share.model.SharePhotoContent;
//import com.facebook.share.widget.ShareButton;
//import com.facebook.share.widget.ShareDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import antvk.tkms.Constants;

public class ImageUtils extends Activity{

    private void takeScreenshot(Window window) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = window.getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    public void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

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

//    public static void sharePicToFB(Bitmap image, ShareButton shareButton, ShareDialog shareDialog)
//    {
//        SharePhoto photo = new SharePhoto.Builder()
//                .setBitmap(image)
//                .build();
//        SharePhotoContent content = new SharePhotoContent.Builder()
//                .addPhoto(photo)
//                .build();
//
//        shareButton.setShareContent(content);
//        shareDialog.show(content);
//    }

    public static Bitmap createBitmapFromView(View view)
    {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
       return view.getDrawingCache();
    }

    public static Intent shareBitmap(Context context, ContentResolver resolver, Bitmap bitmap)
    {
        try {
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imagePath = new File(context.getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(context, "com.example.myapp.fileprovider", newFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, resolver.getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                return shareIntent;
            }
        }catch (Exception e){}
        return null;
    }

    public static String getImageFolderByMapType(int type)
    {
        switch (type)
        {
            case 0 : return Constants.KINGS_IMAGE_FOLDER;
            case 1 : return Constants.DESTINY_IMAGE_FOLDER;
            case 2 : return Constants.TENCENT_IMAGE_FOLDER;

            default: return Constants.LOCAL_IMAGE_FOLDER;
        }
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
