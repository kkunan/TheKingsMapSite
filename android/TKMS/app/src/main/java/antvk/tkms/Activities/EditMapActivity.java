package antvk.tkms.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import antvk.tkms.R;
import antvk.tkms.Struct.Information.InformationItem;
import antvk.tkms.Struct.MapAttribute.AvailableMap;
import antvk.tkms.ViewManager.InfoItemListView.InfoItemAdapter;
import antvk.tkms.ViewManager.MapSelectorView.MapSelectorAdapter;
import antvk.tkms.ViewManager.RecyclerItemClickListener;

public class EditMapActivity extends ActivityWithBackButton{

    EditText mapNameBox;
    TextView placeListLabel;

    InfoItemAdapter adapter;

    List<InformationItem> placeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_map);

        mapNameBox = (EditText)findViewById(R.id.map_name_edittext);
        placeListLabel = (TextView)findViewById(R.id.place_list_header);

        int mapID = getExtra(MAP_ID_KEY);
        placeList = new ArrayList<>();

        if(mapID >= 0)
        {
            AvailableMap map = MapSelectorActivity.maps.get(mapID);
            mapNameBox.setText(map.mapName);
            placeList = map.informationItems;
        }

        adapter = new InfoItemAdapter(EditMapActivity.this,placeList);

        sortoutRecycleView(R.id.placeListView,placeList);
    }

    void sortoutRecycleView(int id, List<InformationItem> viewList)
    {
        final RecyclerView recList = (RecyclerView) findViewById(id);
        final List<InformationItem> views = new ArrayList<>(viewList);
        recList.setHasFixedSize(true);
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recList ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        //    System.out.println("position "+position+" "+views.size());

//                        Intent intent = new Intent(EditMapActivity.this,MapsActivity.class);
//
//                        b = new Bundle();
//                        if(b!=null) {
//                            b.putInt(MAP_ID_KEY, views.get(position).mapID);
//                            intent.putExtras(b);
//                            startActivity(intent);
//                        }

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                }));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.setAdapter(adapter);


//        for(int i=0;i<viewList.size();i++)
//        {
//
//            ImageView imageView = adapter.mapViewHolders.get(i).imageView;
//            String imagePath = viewList.get(i).imageLogo;
//            imageView.setImageDrawable(ImageUtils.getDrawable(getApplicationContext(),AvailableMap.imageFolder,imagePath
//                   ));
//        }
    }



    public void onSubmitButtonClick(View view)
    {

    }
}
