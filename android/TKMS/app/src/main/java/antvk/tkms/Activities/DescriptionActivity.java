package antvk.tkms.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import java.util.Map;

import antvk.tkms.InformationItem;
import antvk.tkms.R;
import antvk.tkms.Utils.ImageUtils;
import antvk.tkms.InformationItem.Event;

public class DescriptionActivity extends ActivityWithBackButton{

    Event event;
    int markerId, eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        markerId = getExtra(MARKER_KEY);
        eventId = getExtra(EVENT_KEY);

        if(markerId!=-1)
        {
            Marker marker = MapsActivity.markerList.get(markerId);

            if(eventId!=-1)
            {
                event = MapsActivity.markerInformationItemMap.get(marker).events.get(eventId);
            }
        }

        setupContentView();
    }

    void setupContentView()
    {
        if(event!=null) {
            TextView titleView = findViewById(R.id.title_text);
            titleView.setText(event.title);

            TextView descriptionText = findViewById(R.id.description_text);
            descriptionText.setText(event.description);

            if(event.imageName.length()>0) {
                Drawable drawable = ImageUtils.getDrawable(this.getApplicationContext(), MapsActivity.imageFolder, event.imageName);
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageDrawable(drawable);
            }
        }
    }

    public void onVideoClick(View v) {

//        if(item.url!=null && item.url.length()>0) {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.url)));
//            Log.i("Video", "Video Playing....");
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(getApplicationContext(),MarkerEventListActivity.class);
        Bundle b = setExtra(MARKER_KEY,markerId);
        intent.putExtras(b);
        startActivity(intent);
        return true;
    }
}
