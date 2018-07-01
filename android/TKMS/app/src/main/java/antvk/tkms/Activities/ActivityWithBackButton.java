package antvk.tkms.Activities;


import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


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
    public boolean showBackButton = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = new Bundle();
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
            gobackToPreviousScreen();
        }catch (Exception e)
        {
            super.onBackPressed();
        }
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
