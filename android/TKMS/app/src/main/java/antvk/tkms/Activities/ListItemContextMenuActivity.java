package antvk.tkms.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import antvk.tkms.R;
import antvk.tkms.ViewManager.RecyclerItemClickListener;

public abstract class ListItemContextMenuActivity extends AddStuffsActivity {

    public int[] contextMenuPosition = new int[]{-1};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void sortOutRecycleViews(int id, int orientation){
        final RecyclerView recList = findViewById(id);

        recList.setHasFixedSize(true);
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                       itemClick(view,position);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                        contextMenuPosition[0] = position;
                        registerForContextMenu(recList);
                        openContextMenu(recList);
                    }
                }));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(orientation);
        recList.setLayoutManager(llm);

        postRecycleViewSetup(recList);

    }

    public abstract void postRecycleViewSetup(RecyclerView recList);
    public abstract void itemClick(View view, int position);

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        createContextMenu(menu, v, menuInfo);
    }

    public abstract void createContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);

    @Override
    public boolean onContextItemSelected (MenuItem item) {
        int index = contextMenuPosition[0];
        switch (item.getItemId()) {
            case R.id.edit_item: {
               edit(index);
            }
            break;
            case R.id.delete_item: {
                delete(index);
            }
        }
        return false;
    }

    protected abstract void edit(int id);
    protected abstract void delete(int id);

    public static void defaultDelete(List listToRemove, RecyclerView.Adapter adapter, int id){
        listToRemove.remove(id);
        adapter.notifyDataSetChanged();
    }
}
