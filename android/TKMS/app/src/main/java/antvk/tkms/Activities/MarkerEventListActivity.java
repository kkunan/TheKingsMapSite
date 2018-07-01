package antvk.tkms.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import antvk.tkms.Struct.Information.PlaceItem;
import antvk.tkms.R;
import antvk.tkms.Struct.MapAttribute.AvailableMap;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.Utils.ImageUtils;
import antvk.tkms.Utils.LocationUtils;
import antvk.tkms.ViewManager.EventView.EventViewAdapter;
import antvk.tkms.ViewManager.RecyclerItemClickListener;

import static antvk.tkms.Activities.MapSelectorActivity.*;
import static antvk.tkms.Activities.MapsActivity.*;


@RequiresApi(api = Build.VERSION_CODES.N)
public class MarkerEventListActivity extends ActivityWithBackButton {

    public static PlaceItem item;
    LocationManager manager;
    int value;
    Gson gson = new Gson();

    static final double CHECKIN_AVALABLE_RANGE = 100.0;

    boolean prevShow = false;
    boolean prevPink = false;
    boolean inRange = false;

    AvailableMap currentMap;

    List<AvailableMap> localMaps = MapSelectorActivity.localMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_itemlist);

        value = getExtra(MARKER_KEY);

        currentMap = gson.fromJson(getIntent().getStringExtra(MAP_KEY),AvailableMap.class);

        if (value != -1) {
            item = markerInformationItemMap.get(
                    markerList.get(value)
            );


            sortOutLocationStuff();

            setTitle(item.header);
            setPlaceContent();
            initializeRecycleView();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location!=null) {
                boolean visited = item.visited;
                double distance = LocationUtils.quickDistance(
                        new LatLng(location.getLatitude(), location.getLongitude()
                        ), item.location
                );
                boolean inDistance = distance <= CHECKIN_AVALABLE_RANGE;
                sortoutUIByStatus(visited, inDistance);
            }
        }
    }

    private void sortOutLocationStuff()
    {
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0,
                0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double distance = LocationUtils.quickDistance(
                                new LatLng(location.getLatitude(),location.getLongitude()
                                ), item.location
                        );

                        boolean visited = item.visited;
                        boolean inDistance = distance <= CHECKIN_AVALABLE_RANGE;
                        inRange = inDistance;
                        sortoutUIByStatus(visited,inDistance);
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });

    }

    private void sortoutUIByStatus(boolean visited, boolean inDistance) {

        ImageView heartView = (ImageView) findViewById(R.id.heart_item_page);
        if(!visited)
        {
            if(!inDistance)
            {
                heartView.setVisibility(View.GONE);
                prevShow = false;
            }

            else
            {
                if(!prevShow)
                {
                    heartView.setVisibility(View.VISIBLE);
                    prevShow = true;

                }

                if(prevPink)
                {
                    Drawable imageDrawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.grey_heart);
                    heartView.setImageDrawable(imageDrawable);
                    prevPink = false;
                }
            }
        }

        else
        {
            if(prevPink && prevShow);

            else if(prevPink)
            {
                heartView.setVisibility(View.VISIBLE);
                prevShow = true;
            }

            else if(prevShow){
                Drawable imageDrawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.pink_heart);
                heartView.setImageDrawable(imageDrawable);
                prevPink = true;
            }
        }
    }


    public void setPlaceContent()
    {
        ImageView placeImageView = (ImageView) findViewById(R.id.place_image);

            try {
                placeImageView.setImageURI(
                        Uri.parse(item.placeImage)
                );
            }catch (Exception e)
            {
                placeImageView.setImageResource(R.mipmap.mymap_icon02);
            }
        final String[] address = {"Address unavailable"};
//        try {
//            String ret = getPlaceAddress(item.location);
//           if(ret.length()>0)
//               address = ret;
//        }catch (IOException e){}


        final TextView descriptionView = (TextView) findViewById(R.id.place_description);

        GeoDataClient mGeoDataClient = Places.getGeoDataClient(getApplicationContext());
        final Place[] myPlace = new Place[1];
        final PlaceBufferResponse[] places = {null};
        mGeoDataClient.getPlaceById(item.placeID).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                        places[0] = task.getResult();
                    try {
                        myPlace[0] = places[0].get(0);
                        System.out.println("Place found: " + myPlace[0].getName());
                        Place place = myPlace[0];
                        if (place != null) {
                            item.placeDescription =
                                    "Name: " + place.getName() +
                                            "\nPhone number: " + place.getPhoneNumber()
                                            + "\nrating: " + place.getRating()
                            ;
                            address[0] = place.getAddress().toString();
                        }
                    }catch (Exception e){
                        System.out.println("places: "+ places[0]);
                        e.printStackTrace();
                    }
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        descriptionView.setText(item.placeDescription);
                                        TextView addressView = (TextView) findViewById(R.id.place_address);
                                        addressView.setText(address[0]);
                                    }
                                }
                        );

                        places[0].release();


                } else {

                }
            }
        });

        ImageView heartView = (ImageView) findViewById(R.id.heart_item_page);

        if(item.visited)
        {
            Drawable imageDrawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.pink_heart);
            heartView.setImageDrawable(imageDrawable);
            heartView.setVisibility(View.VISIBLE);
            prevShow = true;
            prevPink = true;
        }

        else
        {
            Drawable imageDrawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.grey_heart);
            heartView.setImageDrawable(imageDrawable);
        }


        TextView placeNameOverlay = (TextView) findViewById(R.id.place_name_overlay);
        placeNameOverlay.setText(item.header);
    }

    public String getPlaceAddress(LatLng location) throws IOException
    {
        StringBuffer buffer = new StringBuffer();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
        if(addresses.size()>0) {
           for(int i=0;i<=addresses.get(0).getMaxAddressLineIndex();i++)
           {
               buffer.append(addresses.get(0).getAddressLine(i));
           }
        }
        return buffer.toString();
    }

    public void initializeRecycleView()
    {
        RecyclerView recList = (RecyclerView) findViewById(R.id.eventListView);
        recList.setHasFixedSize(true);
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recList ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        Intent intent = new Intent(getApplicationContext(),DescriptionActivity.class);

                        Bundle b = setExtra(MARKER_KEY,value);
                        b = setExtra(EVENT_KEY,position, b);
                        b .putString(MAP_KEY,gson.toJson(currentMap));
                        b.putString(ClassMapper.classIntentKey, "MarkerEventListActivity");
                        intent.putExtras(b);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                }));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        if(item.events==null)
            item.events = new ArrayList<>();
        EventViewAdapter adapter = new EventViewAdapter(item.events);
        recList.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        gobackToPreviousScreen();
        return true;
    }


    public void OnMoreDetailsClick(View view) {
        ImageView arrow = (ImageView) findViewById(R.id.arrow);
        TextView placeDescriptionView = (TextView) findViewById(R.id.place_description);
        if(placeDescriptionView.getVisibility() == View.GONE)
        {
            placeDescriptionView.setVisibility(View.VISIBLE);
            arrow.setRotation(90);
        }

        else{
            placeDescriptionView.setVisibility(View.GONE);
            arrow.setRotation(0);
        }
    }

    public void onHeartClick(View view) {
        boolean visited = item.visited;
        if(!visited)
        {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {


                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            item.visited = true;
                            localMaps.set(currentMap.mapID,currentMap);

                            MapSelectorActivity.preferences.edit().putString(
                                    MAP_PREF,
                                    gson.toJson(localMaps)
                                    ).apply();

                            sortoutUIByStatus(true,true);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MarkerEventListActivity.this);
            builder.setMessage(
                    getString(R.string.checkin)+" "+item.header+"?"
            ).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        else
        {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        item.visited = false;
                        localMaps.set(currentMap.mapID,currentMap);
                        MapSelectorActivity.preferences.edit().putString(MAP_PREF,
                                gson.toJson(localMaps)
                        ).apply();

                        sortoutUIByStatus(false,inRange);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MarkerEventListActivity.this);
            builder.setMessage(
                    getString(R.string.uncheck)+" "+item.header+"?"
            ).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    @Override
    public void onBackPressed() {
        gobackToPreviousScreen();
    }

    @Override
    Bundle setFurtherExtra(Bundle b) {
        b.putInt(MARKER_KEY,value);
        b.putString(MAP_KEY,gson.toJson(currentMap));
        return b;
    }

}


