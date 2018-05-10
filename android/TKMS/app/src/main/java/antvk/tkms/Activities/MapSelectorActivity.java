package antvk.tkms.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import antvk.tkms.R;
import antvk.tkms.Struct.MapAttribute.AvailableMap;
import antvk.tkms.ViewManager.RecyclerItemClickListener;
import antvk.tkms.ViewManager.MapSelectorView.MapSelectorAdapter;

import static antvk.tkms.Activities.ActivityWithBackButton.MAP_ID_KEY;

public class MapSelectorActivity extends AppCompatActivity{

    public static final String MAP_PREF = "maps";

    public static List<AvailableMap> maps;
    static String mapFile = "maps.json";
    static SharedPreferences preferences;

    static Type listType = new TypeToken<ArrayList<AvailableMap>>() {
    }.getType();


    Bundle b;
    static Gson gson = new Gson();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.map_selector_layout);

    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    maps = getAllItems(getApplicationContext());

    MapsActivity.mapIndex = -1;

//    ArrayList<AvailableMap> leftViewList = new ArrayList<>();
//    ArrayList<AvailableMap> rightViewList = new ArrayList<>();

//    for(int i=0;i<maps.size();i++)
//    {
//        AvailableMap map = maps.get(i);
//
//        if(i%2==0) {
//            leftViewList.add(map);
//        }
//        else {
//            rightViewList.add(map);
//        }
//    }

//    sortOutRecycleViews(R.id.left_view, leftViewList);
//    sortOutRecycleViews(R.id.right_view, rightViewList);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        ImageView imageView = new ImageView(actionBar.getThemedContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(R.mipmap.logo_icon);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        layoutParams.leftMargin = 40;
        imageView.setLayoutParams(layoutParams);
        actionBar.setCustomView(imageView);
        actionBar.setTitle("");

        sortOutRecycleViews(R.id.map_list_view,maps);
    }

    public List<AvailableMap> getAllItems(Context context) {

        List<AvailableMap> items = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(mapFile);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String st = "";

            StringBuffer buffer = new StringBuffer();
            while ((st = reader.readLine()) != null) {
                buffer.append(st + "\n");
            }


            items = gson.fromJson(buffer.toString(), listType);

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<AvailableMap> localMaps = getLocalMaps();

        if(localMaps!=null)
        {
            items.addAll(localMaps);
        }

        return items;
    }

    public static List<AvailableMap> getLocalMaps()
    {
        String st = preferences.getString(MAP_PREF,null);

        if(st!=null && st.length()>0)
        {
            List<AvailableMap> localMaps = gson.fromJson(st,listType);
            return localMaps;
        }
        return null;
    }


    void sortOutRecycleViews(int id, List<AvailableMap> viewList)
    {
        final RecyclerView recList = (RecyclerView) findViewById(id);
        final List<AvailableMap> views = new ArrayList<>(viewList);
        recList.setHasFixedSize(true);
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recList ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                    //    System.out.println("position "+position+" "+views.size());

                        Intent intent = new Intent(MapSelectorActivity.this,MapsActivity.class);

                        b = new Bundle();
                        if(b!=null) {
                            b.putInt(MAP_ID_KEY, views.get(position).mapID);
                            intent.putExtras(b);
                            startActivity(intent);
                        }

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                        MapsActivity.mapIndex = position;
                        Intent intent = new Intent(MapSelectorActivity.this, EditMapActivity.class);
                        startActivity(intent);
                    }
                }));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recList.setLayoutManager(llm);
        MapSelectorAdapter adapter = new MapSelectorAdapter(MapSelectorActivity.this,viewList);
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

    public void onAddNewMapClick(View view)
    {
        Intent intent = new Intent(MapSelectorActivity.this, EditMapActivity.class);
        startActivity(intent);
    }
}
