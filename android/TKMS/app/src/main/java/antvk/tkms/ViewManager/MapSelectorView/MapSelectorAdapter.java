package antvk.tkms.ViewManager.MapSelectorView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import antvk.tkms.Activities.MapsActivity;
import antvk.tkms.R;
import antvk.tkms.Struct.Information.InformationItem;
import antvk.tkms.Struct.MapAttribute.AvailableMap;
import antvk.tkms.Utils.ImageUtils;
import antvk.tkms.ViewManager.EventView.EventViewHolder;

public class MapSelectorAdapter extends RecyclerView.Adapter<MapSelectorAdapter.MapViewHolder> {

    public List<AvailableMap> availableMaps;
    public Context context;

    public MapSelectorAdapter(Context context,List<AvailableMap> availableMaps) {
        this.context = context;
        this.availableMaps = availableMaps;
    }

    @Override
    public int getItemCount() {
        return availableMaps.size();
    }

    @Override
    public void onBindViewHolder(MapViewHolder mapViewHolder, int i) {
        AvailableMap ci = availableMaps.get(i);
        mapViewHolder.setValue(ci.mapID,ci.mapName,ci.imageLogo,context);
    }

    @Override
    public MapViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.halfview, viewGroup, false);

        return new MapViewHolder(itemView);
    }

    public class MapViewHolder extends RecyclerView.ViewHolder {

        public TextView vTitle;
        public String imageName;
        public ImageView imageView;

        public MapViewHolder(View v) {
            super(v);
            vTitle = v.findViewById(R.id.map_title_text);
            imageView = v.findViewById(R.id.halfview_image);
        }

        public void setValue(int mapID, String mapName, String imageLogo,Context context) {
            this.vTitle.setText(mapName);
            this.imageName = imageLogo;

            imageView.setImageDrawable(
                    ImageUtils.getDrawable(context,AvailableMap.imageFolder,imageLogo)
            );
        }


    }
}