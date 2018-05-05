package antvk.tkms.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOError;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import antvk.tkms.InformationItem;
import antvk.tkms.R;
import antvk.tkms.Utils.ImageUtils;
import antvk.tkms.ViewManager.EventView.EventViewAdapter;
import antvk.tkms.ViewManager.EventView.RecyclerItemClickListener;


public class MarkerEventListActivity extends ActivityWithBackButton{

    public static InformationItem item;
    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_itemlist);

        value = getExtra(MARKER_KEY);

        if(value!=-1) {
            item = MapsActivity.markerInformationItemMap.get(
                    MapsActivity.markerList.get(value)
            );

            setTitle(item.header);
            setPlaceContent();
            initializeRecycleView();

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
}


