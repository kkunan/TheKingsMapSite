//package antvk.tkms.Activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.todddavies.components.progressbar.ProgressWheel;
//
//import java.util.Map;
//
//import antvk.tkms.R;
//import antvk.tkms.ViewManager.EventView.EventViewAdapter;
//import antvk.tkms.ViewManager.HistoryItemView.HistoryItemAdapter;
//import antvk.tkms.ViewManager.RecyclerItemClickListener;
//
//import static antvk.tkms.Activities.MapsActivity.mapIndex;
//
//public class HistoryActivity extends ActivityWithBackButton{
//
//    HistoryItemAdapter historyItemAdapter;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_history);
//
//        historyItemAdapter = new HistoryItemAdapter(getApplicationContext(),MapsActivity.mapVisitedInformation.getVisitedList());
//        initializeHeaderView();
//        initializeRecycleView();
//    }
//
//    private void initializeHeaderView() {
//
//        ProgressWheel progressBar = (ProgressWheel) findViewById(R.id.heart_spinner);
//        double percent = MapsActivity.mapVisitedInformation.getVisitedPercentage();
//        progressBar.incrementProgress((int)(Math.ceil(percent*360)));
//        progressBar.setText((int)(Math.ceil(percent*100))+"");
//
//        TextView textView = (TextView)findViewById(R.id.complete_counter);
//        textView.setText(
//                String.format("%s / %s events complete!"
//                ,historyItemAdapter.informationItems.size(), MapsActivity.mapVisitedInformation.informationList.size()
//                )
//        );
//
//    }
//
//
//    private void initializeRecycleView() {
//
//            RecyclerView recList = (RecyclerView) findViewById(R.id.history_list_view);
//            recList.setHasFixedSize(true);
//            recList.addOnItemTouchListener(
//                    new RecyclerItemClickListener(getApplicationContext(), recList ,new RecyclerItemClickListener.OnItemClickListener() {
//                        @Override public void onItemClick(View view, int position) {
//
////                            Intent intent = new Intent(getApplicationContext(),DescriptionActivity.class);
////
////                            Bundle b = setExtra(MARKER_KEY,value);
////                            b = setExtra(EVENT_KEY,position, b);
////                            b = setExtra(MAP_ID_KEY,mapIndex,b);
////                            intent.putExtras(b);
////                            startActivity(intent);
//                            // TODO: 06/05/2018 share achievements
//                        }
//
//                        @Override public void onLongItemClick(View view, int position) {
//                            // do whatever
//                        }
//                    }));
//
//            LinearLayoutManager llm = new LinearLayoutManager(this);
//            llm.setOrientation(LinearLayoutManager.VERTICAL);
//            recList.setLayoutManager(llm);
//            recList.setAdapter(historyItemAdapter);
//
//
//    }
//
//}
