package antvk.tkms.Activities;


import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.google.gson.Gson;

import antvk.tkms.Utils.ClassMapper;


public abstract class ActivityWithBackButton extends AppCompatActivity {
    public static final String MAP_KEY = "map";
    public static final String PLACE_KEY = "placeKey";
    public static final String EVENT_KEY = "eventKey";

    public static final String MAP_ID_KEY = "mapIDKey";
    public static final String MARKER_KEY = "markerKey";

    public static Gson gson = new Gson();

    public Bundle b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = new Bundle();

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        gobackToPreviousScreen();
    }

    protected void gobackToPreviousScreen() {
        String previousClass = getIntent().getStringExtra(ClassMapper.classIntentKey);
        Class cl = ClassMapper.get(previousClass);

        b = setFurtherExtra(b);

        Intent intent = new Intent(getApplicationContext(),cl);
        intent.putExtras(b);
        startActivity(intent);
    }

    abstract Bundle setFurtherExtra(Bundle b);

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
