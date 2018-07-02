package antvk.tkms.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

import antvk.tkms.R;
import antvk.tkms.Struct.PlaceItem.PlaceItem;
import antvk.tkms.Struct.MapAttribute.AvailableMap;

import static antvk.tkms.Constants.MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE;
import static antvk.tkms.Constants.PICK_IMAGE;

public abstract class AddStuffsActivity extends ActivityWithBackButton{

    public AvailableMap currentMap;
    public PlaceItem currentItem;
    public PlaceItem.Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            String mapJson = getIntent().getStringExtra(MAP_KEY);
            currentMap = gson.fromJson(mapJson,AvailableMap.class);

            String placeJson = getIntent().getStringExtra(PLACE_KEY);
            currentItem = gson.fromJson(placeJson,PlaceItem.class);

            String eventJson = getIntent().getStringExtra(EVENT_KEY);
            currentEvent = gson.fromJson(eventJson,PlaceItem.Event.class);

        }catch (Exception e){e.printStackTrace();}
    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close();
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    public void onImageViewClick(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddStuffsActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE);
        }

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Map Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            //TODO: action
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData( );
                String picturePath = getPath(AddStuffsActivity.this, selectedImageUri );
                System.out.println("Picture Path"+ picturePath);

                selectPicAction(picturePath);
            }

        }
    }

    public abstract void selectPicAction(String picturePath);

    public String resizeAndGet(String realPath,String type, int id)
    {
        Bitmap b= BitmapFactory.decodeFile(realPath);
        int width = b.getWidth();
        int height = b.getHeight();

        int newHeight = 200;
        int newWidth = (int)((double)(width)/(double)(height) * newHeight);
        Bitmap out = Bitmap.createScaledBitmap(b, newWidth, newHeight, false);

        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/"+getResources().getString(R.string.app_name));
        System.out.println("folder: "+dir);
        dir.mkdir();
        File file = new File(dir, type+id+".png");
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

}
