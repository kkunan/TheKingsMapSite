package antvk.tkms;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Map<String, Drawable> imageDrawables;
    Map<Marker, InformationItem> markerInformationItemMap;

    GoogleMap.InfoWindowAdapter infoWindowAdapter;

    Gson gson;

    static final String imageFolder = "kings_images";
    static final String infoFile = "info.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gson = new GsonBuilder().setPrettyPrinting().create();
        markerInformationItemMap = new HashMap<>();
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
                TextView latlngtext = (TextView) v.findViewById(R.id.location_text);

                TextView description = v.findViewById(R.id.description_text);
                ImageView imageView = v.findViewById(R.id.image_text);

                InformationItem item = markerInformationItemMap.get(arg0);

                header.setText(item.header);
                latlngtext.setText(latLng.latitude+", "+latLng.longitude);
                description.setText(item.description);

                System.out.println("image drawable: "+imageDrawables.get(item.imageName));

                imageView.setImageDrawable(imageDrawables.get(item.imageName));
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50,50);
//                imageView.setLayoutParams(params);

                return v;

            }
        };
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

        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                System.out.println("Marker Click!");
                marker.showInfoWindow();
                return false;
            }
        });

        try {
            String[] imageFile = getAssets().list(imageFolder);
            imageDrawables = getDrawables(this,imageFile);

        } catch (IOException e) {
            e.printStackTrace();
        };
        infoWindowAdapter = setInfoWindowAdapter();
        mMap.setInfoWindowAdapter(infoWindowAdapter);
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


    public Map<String, Drawable> getDrawables(Context context, String[] names) {
        Map<String, Drawable> da = new HashMap<>();
        for (String name : names) {
            try {
                Drawable d = getDrawable(context, name);
                if (d != null) da.put(name, d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return da;
    }

    // this is just a temporary method put here to load in the images
    public Drawable getDrawable(Context context, String name) {
        try {
            InputStream inputstream = context.getAssets().open(imageFolder + "/" + name);
            System.out.println("input stream: "+inputstream);
            Drawable drawable = Drawable.createFromStream(inputstream, null);
            System.out.println("drawable: "+drawable);            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
