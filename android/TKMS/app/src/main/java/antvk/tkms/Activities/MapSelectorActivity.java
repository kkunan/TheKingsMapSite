package antvk.tkms.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import antvk.tkms.R;
import antvk.tkms.Struct.MapAttribute.AvailableMap;
import antvk.tkms.ViewManager.RecyclerItemClickListener;
import antvk.tkms.ViewManager.MapSelectorView.MapSelectorAdapter;

import static antvk.tkms.Activities.ActivityWithBackButton.MAP_ID_KEY;

public class MapSelectorActivity extends AppCompatActivity{

    List<AvailableMap> maps;
    static String mapFile = "maps.json";

    Bundle b;
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.map_selector_layout);

    maps = getAllItems(getApplicationContext());


//    ArrayList<AvailableMap> leftViewList = new ArrayList<>();
//    ArrayList<AvailableMap> rightViewList = new ArrayList<>();

//    for(int i=0;i<maps.size();i++)
//    {
//        AvailableMap map = maps.get(i);
//
//        if(i%2==0) {
//            leftViewList.add(map);
//        }
//        else {
//            rightViewList.add(map);
//        }
//    }

//    sortOutRecycleViews(R.id.left_view, leftViewList);
//    sortOutRecycleViews(R.id.right_view, rightViewList);

        sortOutRecycleViews(R.id.map_list_view,maps);
    }

    public List<AvailableMap> getAllItems(Context context) {

        List<AvailableMap> items = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(mapFile);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String st = "";

            StringBuffer buffer = new StringBuffer();
            while ((st = reader.readLine()) != null) {
                buffer.append(st + "\n");
            }

            Type listType = new TypeToken<ArrayList<AvailableMap>>() {
            }.getType();
            items = gson.fromJson(buffer.toString(), listType);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }


    void sortOutRecycleViews(int id, List<AvailableMap> viewList)
    {
        final RecyclerView recList = (RecyclerView) findViewById(id);
        final List<AvailableMap> views = new ArrayList<>(viewList);
        recList.setHasFixedSize(true);
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recList ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                    //    System.out.println("position "+position+" "+views.size());

                        Intent intent = new Intent(MapSelectorActivity.this,MapsActivity.class);

                        b = new Bundle();
                        if(b!=null) {
                            b.putInt(MAP_ID_KEY, views.get(position).mapID);
                            intent.putExtras(b);
                            startActivity(intent);
                        }

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                }));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recList.setLayoutManager(llm);
        MapSelectorAdapter adapter = new MapSelectorAdapter(MapSelectorActivity.this,viewList);
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
}
