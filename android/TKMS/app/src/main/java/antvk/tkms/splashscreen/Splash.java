package antvk.tkms.splashscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;

import antvk.tkms.Activities.MapSelectorActivity;
import antvk.tkms.Constants;
import antvk.tkms.R;
import antvk.tkms.Utils.ImageUtils;

public class Splash extends Activity {
    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 3000L;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);

        ImageView imageView = (ImageView)findViewById(R.id.imageView1);
        imageView.setImageDrawable(ImageUtils.getDrawable(Splash.this, Constants.FOOTAGE_IMAGE+"/splash2.png"));
        handler = new Handler();
//
        runnable = new Runnable() {
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MapSelectorActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }
}