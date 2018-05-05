package antvk.tkms.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.todddavies.components.progressbar.ProgressWheel;

import java.io.IOError;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import antvk.tkms.InformationItem;
import antvk.tkms.MapVisitedInformation;
import antvk.tkms.R;
import antvk.tkms.Utils.ImageUtils;
import antvk.tkms.Utils.LocationUtils;
import antvk.tkms.ViewManager.EventView.EventViewAdapter;
import antvk.tkms.ViewManager.EventView.RecyclerItemClickListener;

import static antvk.tkms.Activities.MapsActivity.gson;
import static antvk.tkms.Activities.MapsActivity.locationUtils;
import static antvk.tkms.Activities.MapsActivity.mapIndex;
import static antvk.tkms.Activities.MapsActivity.mapVisitedInformation;
import static antvk.tkms.Activities.MapsActivity.preferences;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MarkerEventListActivity extends ActivityWithBackButton {

    public static InformationItem item;
    LocationManager manager;
    int value;

    static final double CHECKIN_AVALABLE_RANGE = 20.0;

    boolean prevShow = false;
    boolean prevPink = false;
    boolean inRange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_itemlist);

        value = getExtra(MARKER_KEY);

//        ProgressWheel pw = findViewById(R.id.pw_spinner);
//        pw.incrementProgress((int)(0.6 * 360));

        if (value != -1) {
            item = MapsActivity.markerInformationItemMap.get(
                    MapsActivity.markerList.get(value)
            );

            setTitle(item.header);
            setPlaceContent();
            initializeRecycleView();
            sortOutLocationStuff();
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

                        boolean visited = mapVisitedInformation.getVisitedAt(item.placeID);
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

        ImageView heartView = findViewById(R.id.heart_item_page);
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
                    heartView.setImageDrawable(getDrawable(R.drawable.grey_heart));
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
                heartView.setImageDrawable(getDrawable(R.drawable.pink_heart));
                prevPink = true;
            }
        }
    }


    public void setPlaceContent()
    {
        ImageView placeImageView = findViewById(R.id.place_image);
        placeImageView.setImageDrawable(ImageUtils.getDrawable(getApplicationContext(),
                MapsActivity.imageFolder,
                item.placeImage));

        String address = "Address unavailable";
        try {
            String ret = getPlaceAddress(item.location);
           if(ret.length()>0)
               address = ret;
        }catch (IOException e){}
        TextView addressView = findViewById(R.id.place_address);
        addressView.setText(address);

        ImageView heartView = findViewById(R.id.heart_item_page);

        if(mapVisitedInformation.getVisitedAt(item.placeID))
        {
            heartView.setImageDrawable(getDrawable(R.drawable.pink_heart));
            heartView.setVisibility(View.VISIBLE);
            prevShow = true;
            prevPink = true;
        }

        else
        {
            heartView.setImageDrawable(getDrawable(R.drawable.grey_heart));
        }

        TextView descriptionView = findViewById(R.id.place_description);
        descriptionView.setText(item.placeDescription);

        TextView placeNameOverlay = findViewById(R.id.place_name_overlay);
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

                        Bundle b = setExtra(MARKER_KEY,value);
                        b = setExtra(EVENT_KEY,position, b);

                        Intent intent = new Intent(getApplicationContext(),DescriptionActivity.class);
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
        EventViewAdapter adapter = new EventViewAdapter(item.events);
        recList.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
        Bundle b = setExtra(MARKER_KEY,value);
        intent.putExtras(b);
        startActivity(intent);
        return true;
    }


    public void OnMoreDetailsClick(View view) {
        ImageView arrow = findViewById(R.id.arrow);
        TextView placeDescriptionView = findViewById(R.id.place_description);
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
        boolean visited = mapVisitedInformation.getVisitedAt(item.placeID);
        if(!visited)
        {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            mapVisitedInformation.setVisit(item.placeID,true);
                            preferences.edit().putString(mapIndex+"",
                                    gson.toJson(mapVisitedInformation)
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
                    getString(R.string.checkin)+item.header+"?"
            ).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        else
        {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            mapVisitedInformation.setVisit(item.placeID,false);
                            preferences.edit().putString(mapIndex+"",
                                    gson.toJson(mapVisitedInformation)
                            ).apply();

                            sortoutUIByStatus(false,inRange);

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MarkerEventListActivity.this);
            builder.setMessage(
                    getString(R.string.uncheck)+item.header+"?"
            ).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }


}


