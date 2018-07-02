package antvk.tkms.Activities.Edit;

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

import java.util.concurrent.atomic.AtomicBoolean;

import antvk.tkms.Activities.ListItemContextMenuActivity;
import antvk.tkms.Activities.Show.MapSelectorActivity;
import antvk.tkms.R;
import antvk.tkms.Struct.AvailableMap;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.Utils.UIUtils;
import antvk.tkms.ViewManager.InfoItemListView.InfoItemAdapter;

import static antvk.tkms.Activities.Show.MapSelectorActivity.*;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditMapActivity extends ListItemContextMenuActivity {

    EditText mapNameBox;
    TextView placeListLabel;
    ImageView mapImageView;

    InfoItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_map);

        mapNameBox = (EditText)findViewById(R.id.map_name_edittext);
        placeListLabel = (TextView)findViewById(R.id.place_list_header);
        mapImageView = (ImageView)findViewById(R.id.add_map_image_button);

        if(localMaps==null)
            localMaps = MapSelectorActivity.getLocalMaps(getApplicationContext());

        if(currentMap==null) {
            currentMap = new AvailableMap();
            mapNameBox.requestFocus();
        }

        else {
            mapNameBox.setText(currentMap.mapName);
            if(currentMap.imageLogo!=null)
                mapImageView.setImageURI(Uri.parse(currentMap.imageLogo));
        }

        adapter = new InfoItemAdapter(EditMapActivity.this,currentMap.placeItems);
        sortOutRecycleViews(R.id.placeListView,LinearLayoutManager.VERTICAL);
    }

    @Override
    public Bundle setFurtherExtra(Bundle b) {
        return b;
    }

    @Override
    public void postRecycleViewSetup(RecyclerView recList) {
        recList.setAdapter(adapter);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recList);
    }

    @Override
    public void itemClick(View view, int position) {

        // TODO: 27/06/2018 fix description page to show this without ID
//        Intent intent = new Intent(EditMapActivity.this,EventDescriptionActivity.class);
//        b.putString(ClassMapper.classIntentKey,"EditMapActivity");
//        b.putInt(MARKER_KEY,position);
//        intent.putExtras(b);
//        startActivity(intent);
    }

    @Override
    public void createContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

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
        currentMap.mapName = mapNameBox.getText().toString();

        b.putString(MAP_KEY, gson.toJson(currentMap));
        b.putString(PLACE_KEY, gson.toJson(currentMap.placeItems.get(id)));
        Intent intent = new Intent(EditMapActivity.this, EditPlaceActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void delete(int index)
    {
        UIUtils.createAndShowAlertDialog(
                EditMapActivity.this,
                "Confirm deleting the place ",
                "Delete place " + currentMap.placeItems.get(index).header,
                (dialogInterface, i) -> {
                    ListItemContextMenuActivity.defaultDelete(currentMap.placeItems,adapter,index);
                    for(int ind=0;ind<currentMap.placeItems.size();ind++)
                    {
                        currentMap.placeItems.get(ind).id = ind;
                    }
//                    currentMap.placeItems.remove(index);
//                    adapter.notifyDataSetChanged();
                },
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }
        );
    }

    public void onSubmitButtonClick(View view)
    {
        currentMap.mapName = mapNameBox.getText().toString();

        if (mapNameBox.getText().toString().trim().equalsIgnoreCase("")) {
            mapNameBox.setError("Name can not be blank");
            return;
        }

        if(adapter.placeItems == null || adapter.placeItems.size() ==0)
        {
            placeListLabel.setError("No places added");
            return;
        }

        currentMap.local = true;
        if(currentMap.mapID < 0)
        {
            currentMap.mapID = localMaps.size();
            localMaps.add(currentMap);
        }

        else
        {
            localMaps.set(currentMap.mapID,currentMap);
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
        String mapImagePath = resizeAndGet(picturePath,"map",id);
        mapImageView.setImageURI(Uri.parse(mapImagePath));
        currentMap.imageLogo = mapImagePath;
    }


    public void onAddNewPlaceClick(View view) {
        currentMap.mapName = mapNameBox.getText().toString();
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
