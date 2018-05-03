package antvk.tkms;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;


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

            Drawable drawable = Utils.getDrawable(this.getApplicationContext(), MapsActivity.imageFolder, item.imageName);
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

}
