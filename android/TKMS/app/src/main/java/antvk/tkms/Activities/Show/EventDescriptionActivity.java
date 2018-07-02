package antvk.tkms.Activities.Show;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import antvk.tkms.Activities.ActivityWithBackButton;
import antvk.tkms.R;
import antvk.tkms.Utils.ImageUtils;
import antvk.tkms.Struct.PlaceItem.Event;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EventDescriptionActivity extends ActivityWithBackButton {


    Event event;
    int markerId, eventId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        hasEditableStuffs = false;
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
            TextView titleView = (TextView) findViewById(R.id.title_text);
            titleView.setText(event.title);

            TextView descriptionText = (TextView) findViewById(R.id.description_text);
            descriptionText.setText(event.description);

            if(event.imageName.length()>0) {
                Drawable drawable = ImageUtils.getDrawable(this.getApplicationContext(), event.imageName);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
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
    public Bundle setFurtherExtra(Bundle b) {
        b.putInt(MARKER_KEY,markerId);
        return b;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        gobackToPreviousScreen();
        return true;
    }

    @Override
    public void onBackPressed() {
        gobackToPreviousScreen();
    }

    public void onShareButtonClick(View view) {

        Bitmap bitmap = ImageUtils.createBitmapFromView(findViewById(R.id.whole_content_view));
        Intent share = ImageUtils.shareBitmap(EventDescriptionActivity.this,getContentResolver(),bitmap);
        startActivity(Intent.createChooser(share, "Share Image"));
    }
}
