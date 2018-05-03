package antvk.tkms;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static antvk.tkms.DescriptionActivity.MARKER_KEY;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static Map<String, Drawable> imageDrawables;
    public static List<Marker> markerList;
    public static LinkedHashMap<Marker, InformationItem> markerInformationItemMap;

    GoogleMap.InfoWindowAdapter infoWindowAdapter;

    Gson gson;

    static final String imageFolder = "kings_images";
    static final String infoFile = "info.json";
    static Marker selectedMarker;

    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gson = new GsonBuilder().setPrettyPrinting().create();
        markerInformationItemMap = new LinkedHashMap<>();

        Bundle b = getIntent().getExtras();
        value = -1; // or other values
        if(b != null) {
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
                imageView.setImageDrawable(imageDrawables.get(item.imageName));

                return v;

            }
        };
    }

    public void OnNavigateButtonClick(View view)
    {
        if(selectedMarker==null && markerList.size()>0)
        {
            selectMarker(markerList.get(0));
        }

        else
        {
            int offset = 0;
            if(view.getId() == R.id.right_button)
            {
                offset =1;
            }
            else if(view.getId() == R.id.left_button)
            {
                offset = -1;
            }
            int markerIndex = (markerList.indexOf(selectedMarker)+markerList.size()+offset)%markerList.size();
            if(markerIndex < markerList.size())
                selectMarker(markerList.get(markerIndex));
        }
    }

    public void selectMarker(Marker marker)
    {
        selectedMarker = marker;
        selectedMarker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        List<InformationItem> informationItems = getAllItems(this);
        for(InformationItem item : informationItems)
        {
            Marker marker = createMarker(item);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        }

        markerList = new ArrayList<>(markerInformationItemMap.keySet());

        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //System.out.println("Marker Click!");
                selectedMarker = marker;
                marker.showInfoWindow();
                return false;
            }
        });

        try {
            String[] imageFile = getAssets().list(imageFolder);
            imageDrawables = Utils.getDrawables(this,imageFolder,imageFile);

        } catch (IOException e) {
            e.printStackTrace();
        };
        infoWindowAdapter = setInfoWindowAdapter();
        mMap.setInfoWindowAdapter(infoWindowAdapter);

        GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent intent = new Intent(MapsActivity.this, DescriptionActivity.class);
                Bundle b = new Bundle();

                b.putInt(MARKER_KEY, markerList.indexOf(marker)); //Your id

                System.out.println("marker index "+markerList.indexOf(marker));

                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
            }
        };

        mMap.setOnInfoWindowClickListener(infoWindowClickListener);

        if(value!=-1)
            selectMarker(markerList.get((value+markerList.size())%markerList.size()));
    }

    public Marker createMarker(InformationItem informationItem)
    {
        MarkerOptions options = new MarkerOptions()
                .title(informationItem.header)
                .position(informationItem.location)

                ;

        Marker marker = mMap.addMarker(options);

        markerInformationItemMap.put(marker,informationItem);
        return marker;
    }

    public List<InformationItem> getAllItems(Context context)
    {

        List<InformationItem> items = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(infoFile);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String st = "";

            StringBuffer buffer = new StringBuffer();
            while((st=reader.readLine())!=null)
            {
                buffer.append(st+"\n");
            }

            Type listType = new TypeToken<ArrayList<InformationItem>>(){}.getType();
            items = gson.fromJson(buffer.toString(),listType);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }



}
