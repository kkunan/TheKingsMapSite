package antvk.tkms.Activities.Show;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
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

import antvk.tkms.Activities.Edit.EditMapActivity;
import antvk.tkms.Activities.ListItemContextMenuActivity;
import antvk.tkms.R;
import antvk.tkms.Struct.AvailableMap;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.Utils.UIUtils;
import antvk.tkms.ViewManager.MapSelectorView.MapSelectorAdapter;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MapSelectorActivity extends ListItemContextMenuActivity {

    public static List<AvailableMap> localMaps;

    Bundle b;
    static Gson gson = new Gson();

    MapSelectorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.showBackButton = false;
        setContentView(R.layout.activity_map_selector);

//        maps = getAllItems(getApplicationContext());
        localMaps = AvailableMap.getLocalMaps(preferences,getApplicationContext());

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
    public Bundle setFurtherExtra(Bundle b) {
        return null;
    }

    @Override
    public void selectPicAction(String picturePath) {

    }

    AvailableMap getItemFromClick(int position)
    {
        return localMaps.get(position);
    }


    @Override
    public void itemClick(View view, int position) {
        Intent intent = new Intent(MapSelectorActivity.this, MapsActivity.class);
        b = new Bundle();
        b.putString(MAP_KEY, gson.toJson(getItemFromClick(position)));
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void postRecycleViewSetup(RecyclerView recList) {
        adapter = new MapSelectorAdapter(MapSelectorActivity.this, localMaps);
        recList.setAdapter(adapter);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recList);
    }

    @Override
    public void  createContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
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
        b.putString(ClassMapper.classIntentKey,"MapSelectorActivity");
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
                    localMaps.remove(index);
                    adapter.notifyDataSetChanged();

                    for(int in = 0;in<localMaps.size();in++)
                    {
                        localMaps.get(in).mapID = in;
                    }

                    preferences.edit().putString(MAP_PREF,gson.toJson(localMaps)).apply();
                    dialogInterface.dismiss();
                },
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }
        );

    }


    public void onAddNewMapClick(View view)
    {
        Intent intent = new Intent(MapSelectorActivity.this, EditMapActivity.class);
        intent.putExtra(ClassMapper.classIntentKey,"MapSelectorActivity");
        startActivity(intent);
    }
}
