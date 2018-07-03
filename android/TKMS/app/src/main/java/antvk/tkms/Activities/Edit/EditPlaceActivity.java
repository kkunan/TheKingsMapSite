package antvk.tkms.Activities.Edit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import antvk.tkms.Activities.AddStuffsActivity;
import antvk.tkms.Activities.Show.MapsActivity;
import antvk.tkms.Activities.Show.PlaceDescriptionActivity;
import antvk.tkms.Constants;
import antvk.tkms.R;
import antvk.tkms.Struct.PlaceItem;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.Utils.ImageUtils;
import antvk.tkms.Utils.LocationUtils;
import antvk.tkms.Utils.UIUtils;
import antvk.tkms.ViewManager.CustomScrollView;

import static antvk.tkms.Activities.Show.MapSelectorActivity.*;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditPlaceActivity extends AddStuffsActivity implements OnMapReadyCallback {

    PlaceAutocompleteFragment autocompleteFragment;

    GoogleMap gMap;
    Marker currentPlaceMarker, radiusPickerMarker;
    Circle circleRadius;
    boolean dragMode = false;

    Spinner dropdown;
    EditText descriptionEditText, placeNameEditText;
    ImageView placeImageView;
    CustomScrollView scrollView;

    Bundle b = new Bundle();

    LocationManager locationManager;
    List<PlaceItem.PlaceCategory> placeCategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_place);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment_edit_place);
        mapFragment.getMapAsync(this);

        descriptionEditText = (EditText) findViewById(R.id.place_description_edittext);
        placeNameEditText = (EditText) findViewById(R.id.place_name_edittext);
        placeImageView = findViewById(R.id.add_map_image_button);

        ImageView guideImageView = findViewById(R.id.how_to_use_imageView);
        guideImageView.setImageBitmap(ImageUtils.getBitmapFromAsset(EditPlaceActivity.this,"not_fancy_guide.PNG"));

        scrollView = findViewById(R.id.scrollView);

        if (currentItem == null) {
            currentItem = new PlaceItem();
//            placeNameEditText.requestFocus();
        } else {
            sortoutUI();
        }

        placeNameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(isMapNameDuplicate(s.toString()))
                {
                    placeNameEditText.setError("You already have another place with this name in this map!");
                }
            }
        });

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

                    if (radiusPickerMarker != null) {
                        radiusPickerMarker.remove();
                    }

                    LatLng newPosition = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius, 90);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.add_map_icon))
                            .position(newPosition)
                            .draggable(true)
                            ;
                    radiusPickerMarker = gMap.addMarker(markerOptions);

                    if (circleRadius != null)
                    {
                        circleRadius.remove();
                    }

                    circleRadius = gMap.addCircle(new CircleOptions()
                            .radius(currentItem.radius)
                            .center(currentPlaceMarker.getPosition())
                            .strokeColor(Color.RED)
                            .fillColor(0x220000FF)
                            .strokeWidth(5)
                    );

                    LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
                    LatLng left = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius*2, 45);
                    LatLng up = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius*2, 225);


                    latlngBuilder.include(newPosition);
                    latlngBuilder.include(left);
                    latlngBuilder.include(up);

                    CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(),50);
                    gMap.moveCamera(cameraUpdateFactory);
                }
            }

            @Override
            public void onError(Status status) {

            }
        });

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
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.equals(radiusPickerMarker) && !dragMode)
                {
                    enableDragMode();
                    return false;
                }
                else if(marker.equals(currentPlaceMarker) && dragMode)
                {
                    disableDragMode();
                    return false;
                }
                return true;
            }
        });
        gMap.setMyLocationEnabled(true);
        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                System.out.println("drag start!");
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                if(marker.equals(radiusPickerMarker) && dragMode)
                {
                    double dist = SphericalUtil.computeDistanceBetween(radiusPickerMarker.getPosition(),currentPlaceMarker.getPosition());

                    if(dist< PlaceDescriptionActivity.CHECKIN_AVALABLE_RANGE) {
                        marker.setPosition(
                                SphericalUtil.computeOffset(currentPlaceMarker.getPosition(),
                                        PlaceDescriptionActivity.CHECKIN_AVALABLE_RANGE,
                                        90
                                )
                        );
                    }

                    else
                        circleRadius.setRadius(dist);


                }
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(marker.equals(radiusPickerMarker)) {
                    currentItem.radius = SphericalUtil.computeDistanceBetween(radiusPickerMarker.getPosition(), currentPlaceMarker.getPosition());

                    LatLng left = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius*2, 45);
                    LatLng up = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius*2, 225);

                    LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();

                    latlngBuilder.include(left);
                    latlngBuilder.include(up);

                    CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(),50);
                    gMap.moveCamera(cameraUpdateFactory);
                }
            }
        });


        if(currentItem.location!=null) {
            currentPlaceMarker = setMarker(currentItem.header,currentItem.location);

            if(circleRadius!=null)
                circleRadius.remove();

            circleRadius = gMap.addCircle(new CircleOptions()
                    .radius(currentItem.radius)
                    .center(currentPlaceMarker.getPosition())
                    .strokeColor(Color.RED)
                    .fillColor(0x220000FF)
                    .strokeWidth(5))
            ;
            LatLng newPosition = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius, 90);
            MarkerOptions markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.add_map_icon))
                    .position(newPosition)
                    .draggable(false)
                    ;

            if(radiusPickerMarker!=null)
                radiusPickerMarker.remove();
            radiusPickerMarker = gMap.addMarker(markerOptions);

            LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
            LatLng left = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius*2, 45);
            LatLng up = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius*2, 225);

            latlngBuilder.include(newPosition);
            latlngBuilder.include(left);
            latlngBuilder.include(up);

            try {
                CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(), 50);
                gMap.moveCamera(cameraUpdateFactory);
            }catch (Exception e){
                e.printStackTrace();
            }
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
            if(!dragMode) {

                currentPlaceMarker = setMarker("random spot", latLng);

                if(circleRadius!=null)
                    circleRadius.remove();

                circleRadius = gMap.addCircle(new CircleOptions()
                        .radius(currentItem.radius)
                        .center(currentPlaceMarker.getPosition())
                        .strokeColor(Color.RED)
                        .fillColor(0x220000FF)
                        .strokeWidth(5))
                        ;
                LatLng newPosition = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius, 90);
                MarkerOptions markerOptions = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.add_map_icon))
                        .position(newPosition)
                        .draggable(true)
                        ;

                if(radiusPickerMarker!=null)
                    radiusPickerMarker.remove();
                radiusPickerMarker = gMap.addMarker(markerOptions);

                LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
                LatLng left = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius*2, 45);
                LatLng up = SphericalUtil.computeOffset(currentPlaceMarker.getPosition(), currentItem.radius*2, 225);

                latlngBuilder.include(newPosition);
                latlngBuilder.include(left);
                latlngBuilder.include(up);

                CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(),50);
                gMap.animateCamera(cameraUpdateFactory);
            }
        });
    }

    private void enableDragMode() {
        gMap.getUiSettings().setScrollGesturesEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);
        gMap.getUiSettings().setTiltGesturesEnabled(false);
        dragMode = true;
        scrollView.setEnableScrolling(false);
        circleRadius.setStrokeColor(Color.GREEN);
        currentPlaceMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.tick_icon));
        radiusPickerMarker.setDraggable(true);
        circleRadius.setFillColor(0x2200FF00);
    }

    private void disableDragMode(){

        gMap.getUiSettings().setScrollGesturesEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setTiltGesturesEnabled(true);
        dragMode = false;
        scrollView.setEnableScrolling(true);
        radiusPickerMarker.setDraggable(false);
        currentPlaceMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
        circleRadius.setStrokeColor(Color.RED);
        circleRadius.setFillColor(0x220000FF);
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
    public Bundle setFurtherExtra(Bundle b) {
        b.putString(MAP_KEY,gson.toJson(currentMap));

        // TODO: 27/06/2018 we want to check the case that it enters from description page as well, or not?
        b.putString(ClassMapper.classIntentKey, "MapSelectorActivity");
        return b;
    }

    public void onSubmitButtonClick(View view) {

        if(placeNameEditText.getText().toString().trim().length()==0)
        {
            placeNameEditText.setError("Place name cannot be empty.");
            placeNameEditText.requestFocus();
            return;
        }


        if(currentItem==null)
            currentItem = new PlaceItem();

        if(currentItem.radius < PlaceDescriptionActivity.CHECKIN_AVALABLE_RANGE)
            currentItem.radius = PlaceDescriptionActivity.CHECKIN_AVALABLE_RANGE;

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
            return;
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

        gobackToPreviousScreen(true);
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


    public void onShowHideMapGuideClick(View view) {

        ImageView guide = findViewById(R.id.how_to_use_imageView);
        Button button = (Button)view;
        if(guide.getVisibility() == View.VISIBLE)
        {
            button.setText(R.string.show_how_to_use_map);
            guide.setVisibility(View.GONE);
        }

        else {
            button.setText(R.string.hide_how_to_use_map);
            guide.setVisibility(View.VISIBLE);
        }
    }
}
