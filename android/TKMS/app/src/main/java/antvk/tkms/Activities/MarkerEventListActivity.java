package antvk.tkms.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import antvk.tkms.InformationItem;
import antvk.tkms.R;
import antvk.tkms.ViewManager.EventView.EventViewAdapter;
import antvk.tkms.ViewManager.EventView.RecyclerItemClickListener;


public class MarkerEventListActivity extends ActivityWithBackButton{

    public static InformationItem item;
    int value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_itemlist);

        value = getExtra(MARKER_KEY);

        if(value!=-1) {
            item = MapsActivity.markerInformationItemMap.get(
                    MapsActivity.markerList.get(value)
            );

            RecyclerView recList = (RecyclerView) findViewById(R.id.eventListView);
            recList.setHasFixedSize(true);
            recList.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), recList ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {

                            Bundle b = setExtra(MARKER_KEY,value);
                            b = setExtra(EVENT_KEY,position, b);

                            Intent intent = new Intent(getApplicationContext(),DescriptionActivity.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }

                        @Override public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    }));

            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);
            EventViewAdapter adapter = new EventViewAdapter(item.events);
            recList.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
        Bundle b = setExtra(MARKER_KEY,value);
        intent.putExtras(b);
        startActivity(intent);
        return true;
    }


}


