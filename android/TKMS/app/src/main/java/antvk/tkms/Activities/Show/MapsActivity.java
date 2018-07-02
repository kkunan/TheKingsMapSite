package antvk.tkms.Activities.Show;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
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
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import antvk.tkms.Activities.ActivityWithBackButton;
import antvk.tkms.Activities.Edit.EditMapActivity;
import antvk.tkms.Constants;
import antvk.tkms.Struct.PlaceItem;
import antvk.tkms.R;
import antvk.tkms.Struct.AvailableMap;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.Utils.ImageUtils;
import antvk.tkms.Utils.LocationUtils;
import antvk.tkms.Utils.MarkerUtils;
import antvk.tkms.Utils.UIUtils;
import antvk.tkms.ViewManager.HistoryItemView.HistoryItemAdapter;
import antvk.tkms.ViewManager.RecyclerItemClickListener;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {


    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1111;
    public static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1112;

    public static GoogleMap mMap;
    public static List<Marker> markerList;
    public static LinkedHashMap<Marker, PlaceItem> markerInformationItemMap;

    LocationManager locationManager;

    static LocationUtils locationUtils;

    public static Gson gson;
    static Marker selectedMarker;

    int value;

    AvailableMap currentMap;

    HistoryItemAdapter historyItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationUtils = new LocationUtils();
        setupLocationService();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gson = new GsonBuilder().setPrettyPrinting().create();
        markerInformationItemMap = new LinkedHashMap<>();

        Bundle b = getIntent().getExtras();
        currentMap = gson.fromJson(getIntent().getStringExtra(ActivityWithBackButton.MAP_KEY), AvailableMap.class);
        value = -1; // or other values
        if (b != null) {
            value = b.getInt(ActivityWithBackButton.MARKER_KEY);
            //System.out.println("mapIndex received: "+mapIndex);
        } else {
            // TODO: 01/07/2018 PROMPT USER THAT SOMETHING IS WRONG
        }

        sortoutUI();
    }

    void sortoutUI() {
        ActionBar actionBar = getSupportActionBar();

        if (selectedMarker == null) {
            findViewById(R.id.header_content).setVisibility(View.GONE);
        }

        if (actionBar != null)
            actionBar.setTitle(currentMap.mapName);

        if (selectedMarker == null) {
            LinearLayout headerLayout = (LinearLayout) findViewById(R.id.header_content);
            headerLayout.setVisibility(View.GONE);
        }

    }

    private void initializeHeaderView() {

        ProgressWheel progressBar = (ProgressWheel) findViewById(R.id.heart_spinner);
        double percent = currentMap.getVisitedRatio();
        progressBar.incrementProgress((int) (Math.ceil(percent * 360)));
        progressBar.setText((int) (Math.ceil(percent * 100)) + "");

        TextView textView = (TextView) findViewById(R.id.complete_counter);
        textView.setText(
                String.format("%s / %s events complete!"
                        , historyItemAdapter.informationItems.size(), currentMap.placeItems.size()
                )
        );

    }


    private void initializeRecycleView() {

        RecyclerView recList = (RecyclerView) findViewById(R.id.history_list_view);
        recList.setHasFixedSize(true);
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

//                            Intent intent = new Intent(getApplicationContext(),EventDescriptionActivity.class);
//
//                            Bundle b = setExtra(MARKER_KEY,value);
//                            b = setExtra(EVENT_KEY,position, b);
//                            b = setExtra(MAP_ID_KEY,mapIndex,b);
//                            intent.putExtras(b);
//                            startActivity(intent);
                        // TODO: 06/05/2018 share achievements
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                }));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.setAdapter(historyItemAdapter);


    }


    void setupLocationService() {
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
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0,
                0, locationUtils.locationListener);

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bottombaritem_mymap:
                                // TODO
                                findViewById(R.id.mapActivity_layout).setVisibility(View.VISIBLE);
                                findViewById(R.id.history_activity_view).setVisibility(View.GONE);

                                findViewById(R.id.header_content).setVisibility(View.GONE);
                                selectedMarker = null;
                                sortoutUI();
                                return true;
                            case R.id.bottombaritem_history:
                                // TODO
//                                Intent intent = new Intent(MapsActivity.this,HistoryActivity.class);
//                                startActivity(intent);
                                findViewById(R.id.history_activity_view).setVisibility(View.VISIBLE);
                                findViewById(R.id.header_content).setVisibility(View.GONE);
                                findViewById(R.id.mapActivity_layout).setVisibility(View.GONE);
                                return true;
//                            case R.id.bottombaritem_profile:
//                                // TODO
//                                return true;
//                            case R.id.bottombaritem_settings:
//                                // TODO
//                                return true;
                        }
                        return false;
                    }
                });
    }

    public void showHistory(View view) {

        View view2 = findViewById(R.id.bottombaritem_history);
        view2.performClick();
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
                View v = getLayoutInflater().inflate(R.layout.layout_info_window, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                TextView header = (TextView) v.findViewById(R.id.header_text);
                //   TextView latlngtext = (TextView) v.findViewById(R.id.location_text);

                ImageView imageView = v.findViewById(R.id.image_text);
                PlaceItem item = markerInformationItemMap.get(arg0);
                header.setText(item.header);
                if (item.placeCategory != null)
                    imageView.setImageBitmap(ImageUtils.getBitmapFromAsset(MapsActivity.this,
                            Constants.PLACE_CATEGORY_PATH + "/" + item.placeCategory.imagePath));

                return v;

            }
        };
    }

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


    public void selectMarker(Marker marker) {
        selectedMarker = marker;
        // selectedMarker.showInfoWindow();
        MarkerUtils.enableMarker(getLayoutInflater(),
                getApplicationContext(),
                marker);

        animateCameraTo(selectedMarker.getPosition(), Constants.STREET_LEVEL_ZOOM);
        showInfoWindowBelow(marker);
    }

    static void animateCameraTo(LatLng latLng, float zoom) {
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
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        List<PlaceItem> placeItems = currentMap.placeItems;

        boolean isAnyLocation = false;
        for (PlaceItem item : placeItems) {
            // TODO: 27/06/2018 check if no place has location then prompt user to input them. 
            if (item.location != null) {
                Marker marker = createMarker(item);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                isAnyLocation = true;
            }
        }

        if(!isAnyLocation)
        {
            UIUtils.createAndShowAlertDialog(
                    MapsActivity.this,
                    "No place on map!",
                    "Seems like you haven't added any places on the map." +
                            "\nPlease do so first.",
                    (DialogInterface.OnClickListener) (dialogInterface, i) -> {
                        Intent intent = new Intent(MapsActivity.this, EditMapActivity.class);
                        intent.putExtra(ActivityWithBackButton.MAP_KEY, gson.toJson(currentMap));
                        startActivity(intent);
                        finish();
                    },
                    null
            );

        }

        markerList = new ArrayList<>(markerInformationItemMap.keySet());
//
//        String checkinInfo = MapSelectorActivity.preferences.getString(mapIndex + "", null);
//        if (checkinInfo == null) {
//            mapVisitedInformation = MapVisitedInformation.getInitialMapVisitedInformation(
//                    placeItems);
//        } else {
//            mapVisitedInformation = gson.fromJson(checkinInfo, MapVisitedInformation.class);
//
//        }

        historyItemAdapter = new HistoryItemAdapter(getApplicationContext(), currentMap.getVisitedList());
        initializeHeaderView();
        initializeRecycleView();

        // System.out.println("progress: "+mapVisitedInformation.getVisitedPercentage());

        ProgressWheel pw = (ProgressWheel) findViewById(R.id.pw_spinner);
        pw.incrementProgress((int) (currentMap.getVisitedRatio() * 360));
        pw.setText((int) Math.ceil(100 * currentMap.getVisitedRatio()) + "");

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedMarker = marker;
                MarkerUtils.enableMarker(getLayoutInflater(), getApplicationContext(), selectedMarker);
                animateCameraTo(marker.getPosition(), Constants.STREET_LEVEL_ZOOM);
                showInfoWindowBelow(marker);
                return true;
            }
        });

        ;

//        if (value != -1)
//            selectMarker(markerList.get((value + markerList.size()) % markerList.size()));

        enableLocationOnMap();
    }

    void gotoDescriptionPage(Marker marker) {
        Intent intent = new Intent(MapsActivity.this, PlaceDescriptionActivity.class);
        Bundle b = new Bundle();

        b.putInt(ActivityWithBackButton.MARKER_KEY, markerList.indexOf(marker)); //Your id
        b.putString(ActivityWithBackButton.MAP_KEY, gson.toJson(currentMap));
        b.putString(ClassMapper.classIntentKey, "MapsActivity");

        //    System.out.println("marker index " + markerList.indexOf(marker));
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }

    public void OnBelowWindowClick(View view) {

        gotoDescriptionPage(selectedMarker);
    }

    void showInfoWindowBelow(Marker marker) {

        LinearLayout headerLayout = (LinearLayout) findViewById(R.id.header_content);
        headerLayout.setVisibility(View.VISIBLE);

        TextView header = (TextView) findViewById(R.id.below_info_header_text);
        PlaceItem item = markerInformationItemMap.get(marker);
        header.setText(item.header.replaceAll("\\ ", "\n"));

    }

    void enableLocationOnMap() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            return;
        }
        mMap.setMyLocationEnabled(true);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location==null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        1: World
//        5: Landmass/continent
//        10: City
//        15: Streets
//        20: Buildings
        //     mMap.setMinZoomPreference(15);
        if (location != null)
            animateCameraTo(new LatLng(location.getLatitude(), location.getLongitude()), Constants.STREET_LEVEL_ZOOM);
    }


    public Marker createMarker(PlaceItem placeItem) {
        MarkerOptions options = new MarkerOptions()
                .title(placeItem.header)
                .position(placeItem.location)
                .icon(
                        BitmapDescriptorFactory.fromBitmap(MarkerUtils
                                .createStoreMarker(getLayoutInflater(), this.getApplicationContext(),
                                        null, MarkerUtils.INACTIVE_NAME, placeItem.header))
                );

        Marker marker = mMap.addMarker(options);

        markerInformationItemMap.put(marker, placeItem);
        return marker;
    }

    public void animateToClosestMarker(LatLng currentLatlng) {
        double distance = Double.MAX_VALUE;
        Marker mk = null;

        for (Marker marker : markerList) {
            double quickDistance = LocationUtils.quickDistance(currentLatlng, marker.getPosition());
            if (quickDistance < distance) {
                mk = marker;
                distance = quickDistance;
            }
        }

        if (mk != null) {
            selectMarker(mk);
        }
    }

    public void onGotoClosestPlaceClick(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location==null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        System.out.println("goto closest place click! "+location);
        if(location!=null)
        {
            animateToClosestMarker(new LatLng(location.getLatitude(),location.getLongitude()));
        }
    }


//    public List<PlaceItem> getAllItems(Context context, String file) {
//
//        List<PlaceItem> items = new ArrayList<>();
//        try {
//            InputStream inputStream = context.getAssets().open(file);
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String st = "";
//
//            StringBuffer buffer = new StringBuffer();
//            while ((st = reader.readLine()) != null) {
//                buffer.append(st + "\n");
//            }
//
//            Type listType = new TypeToken<ArrayList<PlaceItem>>() {
//            }.getType();
//            items = gson.fromJson(buffer.toString(), listType);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return items;
//    }

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

    public void OnMapSelectionClick(View view) {
        Intent intent = new Intent(MapsActivity.this, MapSelectorActivity.class);
        startActivity(intent);
    }
}