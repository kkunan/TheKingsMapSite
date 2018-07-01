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
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import antvk.tkms.Constants;
import antvk.tkms.R;
import antvk.tkms.Struct.MapAttribute.AvailableMap;
import antvk.tkms.Utils.UIUtils;
import antvk.tkms.ViewManager.RecyclerItemClickListener;
import antvk.tkms.ViewManager.MapSelectorView.MapSelectorAdapter;

import static antvk.tkms.Activities.ActivityWithBackButton.MAP_KEY;
import static antvk.tkms.Activities.MapsActivity.*;

import static antvk.tkms.Activities.ActivityWithBackButton.MAP_ID_KEY;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MapSelectorActivity extends ListItemContextMenuActivity {

    public static final String MAP_PREF = "maps";

   // public static List<AvailableMap> maps;

    public static List<AvailableMap> localMaps;
    public static List<AvailableMap> externalMaps;

    static String mapFile = "maps.json";
    static SharedPreferences preferences;

    static Type listType = new TypeToken<ArrayList<AvailableMap>>() {
    }.getType();

    Bundle b;
    static Gson gson = new Gson();

    MapSelectorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.showBackButton = false;
        setContentView(R.layout.map_selector_layout);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        maps = getAllItems(getApplicationContext());
        localMaps = getLocalMaps(getApplicationContext());

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

        sortOutRecycleViews(R.id.map_list_view, LinearLayoutManager.HORIZONTAL);
    }

    @Override
    Bundle setFurtherExtra(Bundle b) {
        return null;
    }

    @Override
    public void selectPicAction(String picturePath) {

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

            items = gson.fromJson(buffer.toString(), listType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static List<AvailableMap> getAllItems(Context context) {


        List<AvailableMap> localMaps = getLocalMaps(context);
        List<AvailableMap> items = getExternalMaps(context);

        localMaps.addAll(items);

        return localMaps;
    }

    public static List<AvailableMap> getLocalMaps(Context context) {
        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String st = preferences.getString(MAP_PREF, null);

        if (st != null && st.length() > 0) {
            return gson.fromJson(st, listType);
        }
        return new ArrayList<>();
    }

    AvailableMap getItemFromClick(int position)
    {
        if(position < localMaps.size())
        {
            return localMaps.get(position);
        }
        else
        {
            return externalMaps.get(position-localMaps.size());
        }
    }


    @Override
    void itemClick(View view, int position) {
        Intent intent = new Intent(MapSelectorActivity.this, MapsActivity.class);
        b = new Bundle();
        b.putString(MAP_KEY, gson.toJson(getItemFromClick(position)));
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    void postRecycleViewSetup(RecyclerView recList) {
        List<AvailableMap> combined = new ArrayList<>(localMaps);
        if(externalMaps!=null)
            combined.addAll(externalMaps);
        adapter = new MapSelectorAdapter(MapSelectorActivity.this, combined);
        recList.setAdapter(adapter);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recList);
    }

    @Override
    void  createContextMenu(ContextMenu menu,View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()== R.id.map_list_view) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.list_item_menu, menu);

            if(getItemFromClick(contextMenuPosition[0]).local) {
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setTitle("Delete Item");
            }
        }
    }

    @Override
    protected void edit(int index)
    {
        b = new Bundle();
        b.putString(MAP_KEY, gson.toJson(getItemFromClick(index)));
        Intent intent = new Intent(MapSelectorActivity.this, EditMapActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void delete(int index)
    {
        UIUtils.createAndShowAlertDialog(
                MapSelectorActivity.this,
                "Confirm deleting the map ",
                "Delete map " + getItemFromClick(index).mapName,
                (dialogInterface, i) -> {
                    List<AvailableMap> toDelete = index < localMaps.size()? localMaps: externalMaps;
                    ListItemContextMenuActivity.defaultDelete(toDelete,adapter,index);
                },
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }
        );
        preferences.edit().putString(MAP_PREF,gson.toJson(localMaps)).apply();
    }


    public void onAddNewMapClick(View view)
    {
        Intent intent = new Intent(MapSelectorActivity.this, EditMapActivity.class);
        startActivity(intent);
    }
}
