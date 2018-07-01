package antvk.tkms.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import antvk.tkms.Constants;
import antvk.tkms.R;
import antvk.tkms.Struct.Information.PlaceItem;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.Utils.ImageUtils;
import antvk.tkms.Utils.LocationUtils;
import antvk.tkms.Utils.UIUtils;

import static antvk.tkms.Activities.MapSelectorActivity.*;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditPlaceActivity extends AddStuffsActivity implements OnMapReadyCallback {

    PlaceAutocompleteFragment autocompleteFragment;

    GoogleMap gMap;
    Marker currentPlaceMarker;

    Spinner dropdown;
    EditText descriptionEditText, placeNameEditText;
    ImageView placeImageView;
//    EventViewAdapter adapter;

    Bundle b = new Bundle();

    LocationManager locationManager;
    List<PlaceItem.PlaceCategory> placeCategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_place_in_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment_edit_place);
        mapFragment.getMapAsync(this);

        descriptionEditText = (EditText) findViewById(R.id.place_description_edittext);
        placeNameEditText = (EditText) findViewById(R.id.place_name_edittext);
        placeImageView = findViewById(R.id.add_map_image_button);

        if (currentItem == null) {
            currentItem = new PlaceItem();
            placeNameEditText.requestFocus();
        } else {
            sortoutUI();
        }

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);

        dropdown = findViewById(R.id.place_category_dropdown);
        placeCategories = getPlaceCategory();
        String[] items = toStringName(placeCategories);
        ArrayAdapter<String> dropdown_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(dropdown_adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: 01/07/2018 set place background
                if(currentItem==null)
                    currentItem = new PlaceItem();

                currentItem.placeCategory = placeCategories.get(i);
                if(currentItem.placeCategory!=null)
                placeImageView.setImageBitmap(ImageUtils.getBitmapFromAsset(EditPlaceActivity.this,
                        Constants.PLACE_CATEGORY_PATH+"/"+currentItem.placeCategory.imagePath));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        LocationUtils locationUtils = new LocationUtils();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert locationManager != null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditPlaceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MapsActivity.MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            ActivityCompat.requestPermissions(EditPlaceActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MapsActivity.MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0,
                0, locationUtils.locationListener);


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                if(currentItem==null)
                    currentItem = new PlaceItem();
                currentItem.placeID = place.getId();
                currentItem.location = place.getLatLng();
                currentItem.placeRealName = place.getName().toString();
                if (gMap != null) {
                    currentPlaceMarker = setMarker(currentItem.header, currentItem.location);
                    CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(currentPlaceMarker.getPosition(), Constants.STREET_LEVEL_ZOOM);
                    gMap.moveCamera(cameraUpdateFactory);
                }

            }

            @Override
            public void onError(Status status) {

            }
        });


//        adapter = new EventViewAdapter(currentItem.events);
//        sortOutRecycleViews(R.id.eventListView, LinearLayoutManager.HORIZONTAL);

    }

    String[] toStringName(List<PlaceItem.PlaceCategory> placeCategories)
    {
        List<String> nameList = new ArrayList<>();

        for(PlaceItem.PlaceCategory category: placeCategories)
        {
            nameList.add(category.name);
        }

        return nameList.toArray(new String[]{});
    }

    List<PlaceItem.PlaceCategory> getPlaceCategory()
    {
        List<PlaceItem.PlaceCategory> categories = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("place_categories.json")));
            String st = "";
            StringBuffer buffer = new StringBuffer();

            while((st=reader.readLine())!=null)
            {
                buffer.append(st);
            }

            Type listType = new TypeToken<ArrayList<PlaceItem.PlaceCategory>>(){}.getType();
            categories = gson.fromJson(buffer.toString(), listType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }

//    @Override
//    void postRecycleViewSetup(RecyclerView recList) {
//        recList.setAdapter(adapter);
//        SnapHelper helper = new LinearSnapHelper();
//        helper.attachToRecyclerView(recList);
//    }
//
//    @Override
//    void itemClick(View view, int position) {
//// TODO: 27/06/2018 reuse the event page
//    }
//
//    @Override
//    void createContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//
//        if (v.getId() == R.id.eventListView) {
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.list_item_menu, menu);
//            menu.getItem(0).setVisible(true);
//            menu.getItem(1).setTitle("Delete Item");
//        }
//    }
//
//    @Override
//    protected void edit(int id) {
//        b = new Bundle();
//        b.putString(ClassMapper.classIntentKey, "EditPlaceActivity");
//        b.putString(MAP_KEY, gson.toJson(currentMap));
//        b.putString(PLACE_KEY, gson.toJson(currentItem));
//        b.putString(EVENT_KEY, gson.toJson(currentItem.events.get(id)));
//        Intent intent = new Intent(EditPlaceActivity.this, EditEventActivity.class);
//        intent.putExtras(b);
//        startActivity(intent);
//    }
//
//    @Override
//    protected void delete(int index) {
//        currentItem.events.remove(index);
//        adapter.notifyDataSetChanged();
//    }

    void sortoutUI() {
        placeNameEditText.setText(currentItem.header);
        descriptionEditText.setText(currentItem.placeDescription);

        if (currentItem.placeCategory != null)
            placeImageView.setImageURI(Uri.parse(currentItem.placeCategory.imagePath));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditPlaceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MapsActivity.MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        gMap.setMyLocationEnabled(true);

        if(currentItem.location!=null) {
            currentPlaceMarker = setMarker(currentItem.header,currentItem.location);
            CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(currentPlaceMarker.getPosition(), Constants.STREET_LEVEL_ZOOM);
            gMap.moveCamera(cameraUpdateFactory);
        }

        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location==null)
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), Constants.STREET_LEVEL_ZOOM));

            }
        }
        gMap.setOnMapLongClickListener(latLng -> {
            // TODO: 01/07/2018 reverse geolocation if possible
            currentPlaceMarker = setMarker("random spot", latLng);
        });
    }

    public Marker setMarker(String header, LatLng location)
    {
        if(currentPlaceMarker!=null)
            currentPlaceMarker.remove();

        MarkerOptions options = new MarkerOptions()
                .title(header)
                .position(location);

       return gMap.addMarker(options);
    }

    @Override
    Bundle setFurtherExtra(Bundle b) {
        b.putString(MAP_KEY,gson.toJson(currentMap));

        // TODO: 27/06/2018 we want to check the case that it enters from description page as well, or not?
        b.putString(ClassMapper.classIntentKey, "MapSelectorActivity");
        return b;
    }

    public void onSubmitButtonClick(View view) {

        if(currentItem==null)
            currentItem = new PlaceItem();
        currentItem.header = placeNameEditText.getText().toString();
        currentItem.placeCategory = placeCategories.get(dropdown.getSelectedItemPosition());

        if(currentPlaceMarker!=null)
            currentItem.location = currentPlaceMarker.getPosition();

        else{
            UIUtils.createAndShowAlertDialog(
                    EditPlaceActivity.this,
                    "Warning!",
                    "No location selected. Please specify your place location!"
                    ,
                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    },null
            );
        }

        currentItem.userNote = descriptionEditText.getText().toString();

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

//    public void onAddEventButtonClick(View view) {
//        Intent intent = new Intent(EditPlaceActivity.this, EditEventActivity.class);
//        Bundle b = new Bundle();
//
//        b.putString(MAP_KEY,gson.toJson(currentMap));
//        b.putString(PLACE_KEY,gson.toJson(currentItem));
//
//        b.putString(ClassMapper.classIntentKey, "EditPlaceActivity");
//
//        intent.putExtras(b);
//        startActivity(intent);
//    }

    public void onImageViewClick(View view) {
        super.onImageViewClick(view);
    }

    @Override
    public void selectPicAction(String picturePath) {
    }

}
