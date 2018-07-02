package antvk.tkms.ViewManager.InfoItemListView;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import antvk.tkms.Constants;
import antvk.tkms.R;
import antvk.tkms.Struct.PlaceItem;
import antvk.tkms.Utils.ImageUtils;

@RequiresApi(api = Build.VERSION_CODES.N)
public class InfoItemAdapter extends RecyclerView.Adapter<InfoItemAdapter.InformationItemHolder> {

    public List<PlaceItem> placeItems;
    public Context context;

    public InfoItemAdapter(Context context, List<PlaceItem> placeItems)
    {
        this.placeItems = placeItems;
        this.context = context;
    }

    @Override
    public InformationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.listview_place, parent, false);

        return new InformationItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InformationItemHolder holder, int position) {
        PlaceItem ci = placeItems.get(position);
        holder.setValue(ci);

        System.out.println(new Gson().toJson(ci));
    }

    @Override
    public int getItemCount() {
        return placeItems.size();
    }


    public class InformationItemHolder extends RecyclerView.ViewHolder{

        public ImageView placeImage;
        public TextView placeName;
        public TextView address;
        public TextView placeDescription;

        public InformationItemHolder(View v) {
            super(v);
            placeImage = v.findViewById(R.id.place_image);
            placeName = v.findViewById(R.id.place_name_overlay);
            address = v.findViewById(R.id.place_address);
            placeDescription = v.findViewById(R.id.place_description);

            v.findViewById(R.id.read_more).setVisibility(View.GONE);
            placeDescription.setVisibility(View.VISIBLE);
        }


        public void setValue(PlaceItem item) {

            if(item.placeCategory!=null)
            placeImage.setImageBitmap(ImageUtils.getBitmapFromAsset(context,
                    Constants.PLACE_CATEGORY_PATH+"/"+item.placeCategory.imagePath));
            placeName.setText(item.header);
            address.setText(item.placeAddress);
            placeDescription.setText(item.placeDescription);



        }
    }
}
