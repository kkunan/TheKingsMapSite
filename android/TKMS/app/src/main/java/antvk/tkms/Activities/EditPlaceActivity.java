package antvk.tkms.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import antvk.tkms.R;
import antvk.tkms.Struct.Information.InformationItem;
import antvk.tkms.Struct.MapAttribute.AvailableMap;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.Utils.MarkerUtils;

import static antvk.tkms.Activities.MapSelectorActivity.*;
import static antvk.tkms.Activities.MapsActivity.*;

public class EditPlaceActivity extends ActivityWithBackButton implements OnMapReadyCallback{

    PlaceAutocompleteFragment autocompleteFragment;
    AvailableMap map;
    InformationItem item;
    GoogleMap gMap;
    Marker currentPlaceMarker;
    int mapIndex = -1, itemID = -1;

    EditText descriptionEditText, placeNameEditText;

    Bundle b = new Bundle();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place_in_map);

        itemID = getExtra(MARKER_KEY);
        mapIndex = getExtra(MAP_ID_KEY);

        if(mapIndex >= 0 && mapIndex < maps.size())
        {
            map = maps.get(mapIndex);
        }

        if(itemID > 0)
        {
            item = map.informationItems.get(itemID);
        }
        else
        {
            item = new InformationItem();
            item.header = "";
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment_edit_place);
        mapFragment.getMapAsync(this);

        descriptionEditText = (EditText) findViewById(R.id.place_description_edittext);
        placeNameEditText = (EditText)findViewById(R.id.place_name_edittext);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                item.placeID = place.getId();
                item.location = place.getLatLng();
                System.out.println(item.location+" ::: "+gMap);
                if(gMap!=null)
                    setMarker();
            }

            @Override
            public void onError(Status status) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        if(item.location!=null)
            setMarker();
    }

    public void setMarker()
    {
        MarkerOptions options = new MarkerOptions()
                .title(item.header)
                .position(item.location);

        currentPlaceMarker = gMap.addMarker(options);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(currentPlaceMarker.getPosition()));
    }
    @Override
    public void onBackPressed() {
        gobackToPreviousScreen();
    }

    private void gobackToPreviousScreen() {
        String previousClass = getIntent().getStringExtra(ClassMapper.classIntentKey);
        Class cl = ClassMapper.get(previousClass);

        b.putInt(MAP_ID_KEY,mapIndex);

        Intent intent = new Intent(getApplicationContext(),cl);
        intent.putExtras(b);
        startActivity(intent);
    }


    public void OnEditPlaceSubmitButtonClick(View view) {
        item.placeDescription = descriptionEditText.getText().toString();
        item.header = placeNameEditText.getText().toString();

        String itemJson = new Gson().toJson(item);

        // TODO: 07/06/2018 set event up

        if(itemID>=0)
            b.putInt(MARKER_KEY,itemID);

        b.putString(SUB_ITEM, itemJson);

        System.out.println(itemJson);
        gobackToPreviousScreen();
    }
}
