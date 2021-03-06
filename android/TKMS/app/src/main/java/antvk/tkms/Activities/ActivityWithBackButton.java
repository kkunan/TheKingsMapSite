package antvk.tkms.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ActivityWithBackButton extends AppCompatActivity {
    public static final String MARKER_KEY = "markerKey";
    public static final String EVENT_KEY = "eventKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

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
