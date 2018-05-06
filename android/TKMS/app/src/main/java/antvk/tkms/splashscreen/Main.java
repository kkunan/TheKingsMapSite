package antvk.tkms.splashscreen;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import antvk.tkms.R;

public class Main extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map_selector_layout);
    }
}