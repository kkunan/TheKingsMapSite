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
import antvk.tkms.Struct.Information.InformationItem;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.ViewManager.EventView.EventViewAdapter;

public class EditPlaceActivity extends ListItemContextMenuActivity implements OnMapReadyCallback{

    PlaceAutocompleteFragment autocompleteFragment;

    GoogleMap gMap;
    Marker currentPlaceMarker;

    EditText descriptionEditText, placeNameEditText;
    ImageView placeImageView;
    EventViewAdapter adapter;

    Bundle b = new Bundle();
    @RequiresApi(api = Build.VERSION_CODES.N)
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

        if(currentItem == null)
            currentItem = new InformationItem();

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

    public void OnEditPlaceSubmitButtonClick(View view) {

        currentItem.placeDescription = descriptionEditText.getText().toString();
        currentItem.header = placeNameEditText.getText().toString();

        if(currentItem.id < 0)
        {
            currentItem.id = currentMap.informationItems.size();
            currentMap.informationItems.add(currentItem);
        }

        else{
            currentMap.informationItems.set(currentItem.id,currentItem);
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
