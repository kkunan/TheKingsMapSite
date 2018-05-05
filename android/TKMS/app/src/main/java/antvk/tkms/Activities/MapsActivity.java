package antvk.tkms.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import antvk.tkms.InformationItem;
import antvk.tkms.R;
import antvk.tkms.Utils.ImageUtils;
import antvk.tkms.Utils.LocationUtils;
import antvk.tkms.Utils.MarkerUtils;

import static antvk.tkms.Activities.MarkerEventListActivity.MARKER_KEY;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {


    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1111;
    private final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1112;

    public static GoogleMap mMap;
    public static Map<String, Drawable> imageDrawables;
    public static List<Marker> markerList;
    public static LinkedHashMap<Marker, InformationItem> markerInformationItemMap;

    LocationManager locationManager;

    GoogleMap.InfoWindowAdapter infoWindowAdapter;
    static LocationUtils locationUtils;

    Gson gson;

    static double belowPortion = 0.3;

    static final String imageFolder = "kings_images";
    static final String infoFile = "info.json";
    static Marker selectedMarker;

    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationUtils = new LocationUtils();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gson = new GsonBuilder().setPrettyPrinting().create();
        markerInformationItemMap = new LinkedHashMap<>();

        Bundle b = getIntent().getExtras();
        value = -1; // or other values
        if (b != null) {
            value = b.getInt(MARKER_KEY);
        }


    }

    private GoogleMap.InfoWindowAdapter setInfoWindowAdapter() {
        return new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                System.out.println("get info window");
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                System.out.println("get info contents");
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                TextView header = (TextView) v.findViewById(R.id.header_text);
                //   TextView latlngtext = (TextView) v.findViewById(R.id.location_text);

                ImageView imageView = v.findViewById(R.id.image_text);
                InformationItem item = markerInformationItemMap.get(arg0);
                header.setText(item.header);
                if (item.placeImage.length() > 0)
                    imageView.setImageDrawable(imageDrawables.get(item.placeImage));

                return v;

            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void OnNavigateButtonClick(View view) {
        if (selectedMarker == null && markerList.size() > 0) {
            selectMarker(markerList.get(0));
        } else {
            int offset = 0;
            if (view.getId() == R.id.right_button) {
                offset = 1;
            } else if (view.getId() == R.id.left_button) {
                offset = -1;
            }
            int markerIndex = (markerList.indexOf(selectedMarker) + markerList.size() + offset) % markerList.size();
            if (markerIndex < markerList.size())
                selectMarker(markerList.get(markerIndex));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void selectMarker(Marker marker) {
        selectedMarker = marker;
        // selectedMarker.showInfoWindow();
        MarkerUtils.enableMarker(getLayoutInflater(),
                getApplicationContext(),
                marker);

        animateCameraTo(selectedMarker.getPosition(), 15);
        showInfoWindowBelow(marker);
    }

    static void animateCameraTo(LatLng latLng, int zoom) {
        if (mMap != null && latLng != null) {
            CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            mMap.animateCamera(cameraUpdateFactory);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        List<InformationItem> informationItems = getAllItems(this);
        for (InformationItem item : informationItems) {
            Marker marker = createMarker(item);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        }

        markerList = new ArrayList<>(markerInformationItemMap.keySet());

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //System.out.println("Marker Click!");
                selectedMarker = marker;

                MarkerUtils.enableMarker(getLayoutInflater(), getApplicationContext(), selectedMarker);
                //selectedMarker.showInfoWindow();
                showInfoWindowBelow(marker);
                return true;
            }
        });

        try {
            String[] imageFile = getAssets().list(imageFolder);
            imageDrawables = ImageUtils.getDrawables(this, imageFolder, imageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
//        infoWindowAdapter = setInfoWindowAdapter();
//        mMap.setInfoWindowAdapter(infoWindowAdapter);
//
//        GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//
//                gotoDescriptionPage(marker);
//
//            }
//        };
//
//        mMap.setOnInfoWindowClickListener(infoWindowClickListener);

        if (value != -1)
            selectMarker(markerList.get((value + markerList.size()) % markerList.size()));

        enableLocationOnMap();
    }

    void gotoDescriptionPage(Marker marker) {
        Intent intent = new Intent(MapsActivity.this, MarkerEventListActivity.class);
        Bundle b = new Bundle();

        b.putInt(MARKER_KEY, markerList.indexOf(marker)); //Your id

    //    System.out.println("marker index " + markerList.indexOf(marker));
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }

    public void OnBelowWindowClick(View view) {

        gotoDescriptionPage(selectedMarker);
    }

    void showInfoWindowBelow(Marker marker) {
        View mapView = findViewById(R.id.map);
        LinearLayout.LayoutParams mapViewParams = (LinearLayout.LayoutParams) mapView.getLayoutParams();

        mapViewParams.weight = (float) (1f - belowPortion);
        mapView.setLayoutParams(mapViewParams);

        TextView header = (TextView) findViewById(R.id.below_info_header_text);
//        ImageView imageView = findViewById(R.id.image_text);
        InformationItem item = markerInformationItemMap.get(marker);
        header.setText(item.header.replaceAll("\\ ", "\n"));


//        imageView.setImageDrawable(imageDrawables.get(item.imageName));

    }

    void enableLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityActivityCompat.requestPermissions(WarningActivity.this,
            //                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            //                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);Compat#requestPermissions for more details.
        }


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0,
                0, locationUtils.locationListener);

        mMap.setMyLocationEnabled(true);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        mMap.setOnMapClickListener(this);
//        1: World
//        5: Landmass/continent
//        10: City
//        15: Streets
//        20: Buildings
        if(value==-1)
            animateCameraTo(new LatLng(location.getLatitude(), location.getLongitude()), 10);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Marker createMarker(InformationItem informationItem) {
        MarkerOptions options = new MarkerOptions()
                .title(informationItem.header)
                .position(informationItem.location)
                .icon(
                        BitmapDescriptorFactory.fromBitmap(MarkerUtils
                                .createStoreMarker(getLayoutInflater(), this.getApplicationContext()
                                        , MarkerUtils.INACTIVE_NAME, MarkerUtils.INACTIVE_BG_NAME, informationItem.header))
                );

        Marker marker = mMap.addMarker(options);

        markerInformationItemMap.put(marker, informationItem);
        return marker;
    }


    public List<InformationItem> getAllItems(Context context) {

        List<InformationItem> items = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(infoFile);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String st = "";

            StringBuffer buffer = new StringBuffer();
            while ((st = reader.readLine()) != null) {
                buffer.append(st + "\n");
            }

            Type listType = new TypeToken<ArrayList<InformationItem>>() {
            }.getType();
            items = gson.fromJson(buffer.toString(), listType);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Do what you want on obtained latLng

                View mapLayout = findViewById(R.id.map);
                LinearLayout.LayoutParams mapParams = (LinearLayout.LayoutParams) mapLayout.getLayoutParams();
                mapParams.weight = 1;
                mapLayout.setLayoutParams(mapParams);

            }
        });
    }

    public void onLocationIconClick(View view)
    {
        // Create a Uri from an intent string. Use the result to create an Intent.
        InformationItem item = markerInformationItemMap.get(selectedMarker);
        Uri gmmIntentUri = Uri.parse("geo:"+item.location.latitude+","+item.location.longitude+"?q="+item.header);
        //       Uri gmmIntentUri = Uri.parse("google.navigation:q="+item.header);
// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");
// Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);
    }


}