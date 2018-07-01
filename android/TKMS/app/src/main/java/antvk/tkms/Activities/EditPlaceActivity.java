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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import antvk.tkms.Constants;
import antvk.tkms.R;
import antvk.tkms.Struct.Information.PlaceItem;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.ViewManager.EventView.EventViewAdapter;

import static antvk.tkms.Activities.MapSelectorActivity.*;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditPlaceActivity extends ListItemContextMenuActivity implements OnMapReadyCallback{

    PlaceAutocompleteFragment autocompleteFragment;

    GoogleMap gMap;
    Marker currentPlaceMarker;

    EditText descriptionEditText, placeNameEditText;
    ImageView placeImageView;
    EventViewAdapter adapter;

    Bundle b = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_place_in_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment_edit_place);
        mapFragment.getMapAsync(this);

        descriptionEditText = (EditText) findViewById(R.id.place_description_edittext);
        placeNameEditText = (EditText)findViewById(R.id.place_name_edittext);
        placeImageView = findViewById(R.id.add_map_image_button);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);

        Spinner dropdown = findViewById(R.id.place_category_dropdown);
        String[] items = new String[]{"category1", "category2", "category3"};//,"add your category..."};
        ArrayAdapter<String> dropdown_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(dropdown_adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: 01/07/2018 set place background
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                currentItem.placeID = place.getId();
                currentItem.location = place.getLatLng();
                System.out.println(currentItem.location+" ::: "+gMap);
                if(gMap!=null)
                    setMarker();
            }

            @Override
            public void onError(Status status) {

            }
        });

        if(currentItem == null) {
            currentItem = new PlaceItem();
            placeNameEditText.requestFocus();
        }

        else{
            sortoutUI();
        }

        adapter = new EventViewAdapter(currentItem.events);
        sortOutRecycleViews(R.id.eventListView, LinearLayoutManager.HORIZONTAL);
    }

    @Override
    void postRecycleViewSetup(RecyclerView recList) {
        recList.setAdapter(adapter);
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recList);
    }

    @Override
    void itemClick(View view, int position) {
// TODO: 27/06/2018 reuse the event page
    }

    @Override
    void createContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId()== R.id.eventListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.list_item_menu, menu);
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setTitle("Delete Item");
        }
    }

    @Override
    protected void edit(int id) {
        b = new Bundle();
        b.putString(ClassMapper.classIntentKey,"EditPlaceActivity");
        b.putString(MAP_KEY, gson.toJson(currentMap));
        b.putString(PLACE_KEY, gson.toJson(currentItem));
        b.putString(EVENT_KEY,gson.toJson(currentItem.events.get(id)));
        Intent intent = new Intent(EditPlaceActivity.this, EditEventActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void delete(int index)
    {
        currentItem.events.remove(index);
        adapter.notifyDataSetChanged();
    }

    void sortoutUI()
    {
        placeNameEditText.setText(currentItem.header);
        descriptionEditText.setText(currentItem.placeDescription);

        if(currentItem.placeImage!=null)
            placeImageView.setImageURI(Uri.parse(currentItem.placeImage));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        if(currentItem.location!=null)
            setMarker();
    }

    public void setMarker()
    {
        MarkerOptions options = new MarkerOptions()
                .title(currentItem.header)
                .position(currentItem.location);

        currentPlaceMarker = gMap.addMarker(options);
        CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(currentPlaceMarker.getPosition(), Constants.STREET_LEVEL_ZOOM);
        gMap.moveCamera(cameraUpdateFactory);
    }

    @Override
    Bundle setFurtherExtra(Bundle b) {
        b.putString(MAP_KEY,gson.toJson(currentMap));

        // TODO: 27/06/2018 we want to check the case that it enters from description page as well, or not?
        b.putString(ClassMapper.classIntentKey, "MapSelectorActivity");
        return b;
    }

    public void onSubmitButtonClick(View view) {

        currentItem.placeDescription = descriptionEditText.getText().toString();
        currentItem.header = placeNameEditText.getText().toString();

        if(currentItem.id < 0)
        {
            currentItem.id = currentMap.placeItems.size();
            currentMap.placeItems.add(currentItem);
        }

        else{
            currentMap.placeItems.set(currentItem.id,currentItem);
        }

        if(currentMap.mapID>0)
        {
            localMaps.set(currentMap.mapID,currentMap);
            preferences.edit().putString(MAP_PREF,gson.toJson(localMaps)).apply();
        }

        gobackToPreviousScreen();
    }

    public void selectPicAction(String picturePath){
        int id = currentItem.id;
        currentItem.placeImage = resizeAndGet(picturePath,"image",id);
        placeImageView.setImageURI(Uri.parse(currentItem.placeImage));
    }

    public void onAddEventButtonClick(View view) {
        Intent intent = new Intent(EditPlaceActivity.this, EditEventActivity.class);
        Bundle b = new Bundle();

        b.putString(MAP_KEY,gson.toJson(currentMap));
        b.putString(PLACE_KEY,gson.toJson(currentItem));

        b.putString(ClassMapper.classIntentKey, "EditPlaceActivity");

        intent.putExtras(b);
        startActivity(intent);
    }

    public void onImageViewClick(View view) {
        super.onImageViewClick(view);
    }


}
