package antvk.tkms.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import antvk.tkms.R;
import antvk.tkms.Struct.MapAttribute.AvailableMap;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.ViewManager.InfoItemListView.InfoItemAdapter;

import static antvk.tkms.Activities.MapSelectorActivity.*;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditMapActivity extends ListItemContextMenuActivity{

    EditText mapNameBox;
    TextView placeListLabel;
    String mapImagePath;
    ImageView mapImageView;

    InfoItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_map);

        mapNameBox = (EditText)findViewById(R.id.map_name_edittext);
        placeListLabel = (TextView)findViewById(R.id.place_list_header);
        mapImageView = (ImageView)findViewById(R.id.add_map_image_button);

        if(maps==null)
            maps = MapSelectorActivity.getAllItems(this);

        if(currentMap==null)
            currentMap = new AvailableMap();

        else {
            mapNameBox.setText(currentMap.mapName);
            if(currentMap.imageLogo!=null)
                mapImageView.setImageURI(Uri.parse(currentMap.imageLogo));
        }

        adapter = new InfoItemAdapter(EditMapActivity.this,currentMap.informationItems);
        sortOutRecycleViews(R.id.placeListView,LinearLayoutManager.VERTICAL);
    }

    @Override
    Bundle setFurtherExtra(Bundle b) {
        return b;
    }

    @Override
    void postRecycleViewSetup(RecyclerView recList) {
        recList.setAdapter(adapter);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recList);
    }

    @Override
    void itemClick(View view, int position) {

        // TODO: 27/06/2018 fix description page to show this without ID
//        Intent intent = new Intent(EditMapActivity.this,DescriptionActivity.class);
//        b.putString(ClassMapper.classIntentKey,"EditMapActivity");
//        b.putInt(MARKER_KEY,position);
//        intent.putExtras(b);
//        startActivity(intent);
    }

    @Override
    void createContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId()== R.id.placeListView) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.list_item_menu, menu);
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setTitle("Delete Item");

        }
    }

    @Override
    protected void edit(int id) {
        b = new Bundle();
        b.putString(ClassMapper.classIntentKey,"EditMapActivity");
        b.putString(MAP_KEY, gson.toJson(currentMap));
        b.putString(PLACE_KEY, gson.toJson(currentMap.informationItems.get(id)));
        Intent intent = new Intent(EditMapActivity.this, EditPlaceActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void delete(int index)
    {
        currentMap.informationItems.remove(index);
        adapter.notifyDataSetChanged();
    }

    public void onSubmitButtonClick(View view)
    {
        if(currentMap.mapID < 0)
        {
            currentMap = new AvailableMap(maps.size(),mapNameBox.getText().toString(), mapImagePath , currentMap.informationItems);
            maps.add(currentMap);
        }

        else
        {
            currentMap = new AvailableMap(currentMap.mapID,mapNameBox.getText().toString(), mapImagePath , currentMap.informationItems);
            maps.set(currentMap.mapID,currentMap);
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

    public void selectPicAction(String picturePath){
        int id = currentMap.mapID;
        mapImagePath = resizeAndGet(picturePath,"map",id);
        mapImageView.setImageURI(Uri.parse(mapImagePath));
    }



    public void onAddNewPlaceClick(View view) {
        Intent intent = new Intent(EditMapActivity.this, EditPlaceActivity.class);
        Bundle b = new Bundle();
        b.putString(MAP_KEY,gson.toJson(currentMap));
        b.putString(ClassMapper.classIntentKey, "EditMapActivity");

        intent.putExtras(b);
        startActivity(intent);
    }

    public void onImageViewClick(View view) {
     super.onImageViewClick(view);
    }
}
