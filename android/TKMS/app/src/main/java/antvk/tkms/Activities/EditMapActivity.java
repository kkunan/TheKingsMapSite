package antvk.tkms.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import antvk.tkms.R;
import antvk.tkms.Struct.Information.InformationItem;
import antvk.tkms.Struct.MapAttribute.AvailableMap;
import antvk.tkms.ViewManager.InfoItemListView.InfoItemAdapter;
import antvk.tkms.ViewManager.MapSelectorView.MapSelectorAdapter;
import antvk.tkms.ViewManager.RecyclerItemClickListener;

import static antvk.tkms.Activities.MapSelectorActivity.*;

public class EditMapActivity extends ActivityWithBackButton{

    int mapID;
    EditText mapNameBox;
    TextView placeListLabel;
    String mapImagePath;
    ImageView mapImageView;

    InfoItemAdapter adapter;

    AvailableMap map;

    List<InformationItem> placeList;
    private int MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE = 5555;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_map);

        mapNameBox = (EditText)findViewById(R.id.map_name_edittext);
        placeListLabel = (TextView)findViewById(R.id.place_list_header);
        mapImageView = (ImageView)findViewById(R.id.add_map_image_button);

        mapID = getExtra(MAP_ID_KEY);
        placeList = new ArrayList<>();

        if(mapID >= 0)
        {
            map = maps.get(mapID);
            mapNameBox.setText(map.mapName);
            placeList = map.informationItems;
            try {
                mapImageView.setImageURI(Uri.parse(map.imageLogo));
            }catch (Exception e)
            {
                mapImageView.setImageResource(R.mipmap.mymap_icon01);
            }
        }


        adapter = new InfoItemAdapter(EditMapActivity.this,placeList);

        sortoutRecycleView(R.id.placeListView,placeList);
    }

    void sortoutRecycleView(int id, List<InformationItem> viewList)
    {
        final RecyclerView recList = (RecyclerView) findViewById(id);
        final List<InformationItem> views = new ArrayList<>(viewList);
        recList.setHasFixedSize(true);
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recList ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        //    System.out.println("position "+position+" "+views.size());

//                        Intent intent = new Intent(EditMapActivity.this,MapsActivity.class);
//
//                        b = new Bundle();
//                        if(b!=null) {
//                            b.putInt(MAP_ID_KEY, views.get(position).mapID);
//                            intent.putExtras(b);
//                            startActivity(intent);
//                        }

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                }));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.setAdapter(adapter);


//        for(int i=0;i<viewList.size();i++)
//        {
//
//            ImageView imageView = adapter.mapViewHolders.get(i).imageView;
//            String imagePath = viewList.get(i).imageLogo;
//            imageView.setImageDrawable(ImageUtils.getDrawable(getApplicationContext(),AvailableMap.imageFolder,imagePath
//                   ));
//        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onSubmitButtonClick(View view)
    {

        if(mapID < 0)
        {
            map = new AvailableMap(maps.size(),mapNameBox.getText().toString(), mapImagePath , placeList);
            map.local = true;
            maps.add(map);
        }

        else
        {
            map = new AvailableMap(mapID,mapNameBox.getText().toString(), mapImagePath , placeList);
            map.local = true;
            maps.set(mapID,map);
        }

        List<AvailableMap> localMaps = new ArrayList<>();

        for(AvailableMap availableMap : maps)
        {
            if(availableMap.local)
            {
                localMaps.add(availableMap);
            }
        }

        MapSelectorActivity.preferences.edit().
                putString(MAP_PREF, gson.toJson(localMaps))
                .apply();

        Intent intent = new Intent(EditMapActivity.this,MapSelectorActivity.class);
        startActivity(intent);
        finish();
    }

    public void OnImageMapClick(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(EditMapActivity.this,
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

    public static final int PICK_IMAGE = 1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            //TODO: action
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData( );
                String picturePath = getPath(EditMapActivity.this, selectedImageUri );
                System.out.println("Picture Path"+ picturePath);

                int id = mapID>=0?mapID:maps.size();

                mapImagePath = resizeAndGet(picturePath,id);
                mapImageView.setImageURI(Uri.parse(mapImagePath));
            }

        }
    }

    public String resizeAndGet(String realPath, int mapID)
    {
        Bitmap b= BitmapFactory.decodeFile(realPath);
        int width = b.getWidth();
        int height = b.getHeight();

        int newHeight = 200;
        int newWidth = (int)((double)(width)/(double)(height) * newHeight);
        Bitmap out = Bitmap.createScaledBitmap(b, newWidth, newHeight, false);
        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        File file = new File(dir, mapID+".png");
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {}
        return file.getAbsolutePath();
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
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }
}
