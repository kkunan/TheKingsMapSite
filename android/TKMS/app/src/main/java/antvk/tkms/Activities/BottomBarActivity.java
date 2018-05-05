package antvk.tkms.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import antvk.tkms.R;

import java.util.ArrayList;
import java.util.List;

public class BottomBarActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_bar);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_bottombar_mymap:
                                // TODO
                                return true;
                            case R.id.action_bottombar_recents:
                                // TODO
                                return true;
                            case R.id.action_bottombar_trips:
                                // TODO
                                return true;
                        }
                        return false;
                    }
                });
    }
}
