package antvk.tkms;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import antvk.tkms.Utils.LocationUtils;


public class DescriptionActivity extends AppCompatActivity{
    public static final String MARKER_KEY = "markerKey";
    public static InformationItem item;
    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        try {
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        Bundle b = getIntent().getExtras();
        value = -1; // or other values
        if(b != null)
            value = b.getInt(MARKER_KEY);

        System.out.println("value "+value);

        if(value!=-1)
            item = MapsActivity.markerInformationItemMap.get(
                    MapsActivity.markerList.get(value)
            );

        setupContentView();
    }

    void setupContentView()
    {
        if(item!=null) {
            TextView titleView = findViewById(R.id.title_text);
            titleView.setText(item.header);

            TextView descriptionText = findViewById(R.id.description_text);
            descriptionText.setText(item.description);

            Drawable drawable = LocationUtils.getDrawable(this.getApplicationContext(), MapsActivity.imageFolder, item.imageName);
            ImageView imageView = findViewById(R.id.imageView);

            imageView.setImageDrawable(drawable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);

        Bundle b = new Bundle();
        b.putInt(DescriptionActivity.MARKER_KEY, value); //Your id
        intent.putExtras(b);

        startActivity(intent);
        return true;
    }

    public void onVideoClick(View v) {

        if(item.url!=null && item.url.length()>0) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.url)));
            Log.i("Video", "Video Playing....");
        }
    }
}


