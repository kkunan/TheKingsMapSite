package antvk.tkms.Activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


import com.google.gson.Gson;

import java.util.List;

import antvk.tkms.Struct.AvailableMap;
import antvk.tkms.Struct.PlaceItem;
import antvk.tkms.Utils.ClassMapper;
import antvk.tkms.Utils.UIUtils;


public abstract class ActivityWithBackButton extends AppCompatActivity {

    protected boolean hasEditableStuffs = true;

    public static final String MAP_PREF = "maps";
    public static final String MAP_KEY = "map";
    public static final String PLACE_KEY = "placeKey";
    public static final String EVENT_KEY = "eventKey";

    public static final String MAP_ID_KEY = "mapIDKey";
    public static final String MARKER_KEY = "markerKey";

    public static Gson gson = new Gson();

    public Bundle b;
    public boolean showBackButton = true;

    public List<AvailableMap> mapList;
    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = new Bundle();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mapList = AvailableMap.getLocalMaps(preferences,getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(showBackButton)
            try {
                ActionBar actionBar = getSupportActionBar();
                assert actionBar != null;
                actionBar.setDisplayHomeAsUpEnabled(true);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
    }

    public boolean isPlaceNameDuplicate(AvailableMap map, String name)
    {
        for(PlaceItem item : map.placeItems)
        {
            if(item.header.equals(name))
                return true;
        }
        return false;
    }

    public boolean isMapNameDuplicate(String name){
        for(AvailableMap map : mapList)
        {
            if(map.mapName.equals(name))
                return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            gobackToPreviousScreen(false);
        }catch (Exception e)
        {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    protected void gobackToPreviousScreen(boolean fromSubmit) {

        String previousClass = getIntent().getStringExtra(ClassMapper.classIntentKey);
        Class cl = ClassMapper.get(previousClass);
        b = setFurtherExtra(b);

        if(fromSubmit)
        {
            Intent intent = new Intent(getApplicationContext(),cl);
            intent.putExtras(b);
            startActivity(intent);
        }

        else if(hasEditableStuffs)
        UIUtils.createAndShowAlertDialog(
                ActivityWithBackButton.this,
                "Warning!",
                "Ignore all your changes?"
                , (dialogInterface, i) -> {

                    Intent intent = new Intent(getApplicationContext(),cl);
                    intent.putExtras(b);
                    startActivity(intent);
                },
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();

                }
        );
        else
            super.onBackPressed();


    }

    public abstract Bundle setFurtherExtra(Bundle b);

    protected int getExtra(String key)
    {
        Bundle b = getIntent().getExtras();

        if(b != null)
            return b.getInt(key);
        return -1;
    }

    protected Bundle setExtra(String key, int value)
    {
        Bundle b = getIntent().getExtras();
        return setExtra(key,value,b);
    }

    protected Bundle setExtra(String key, int value, Bundle b)
    {

        if(b!=null)
        {
            b.putInt(key,value);
        }

        return b;
    }

}
