package antvk.tkms.ViewManager.InfoItemListView;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import antvk.tkms.Activities.MapsActivity;
import antvk.tkms.Constants;
import antvk.tkms.R;
import antvk.tkms.Struct.Information.InformationItem;
import antvk.tkms.Struct.Information.MapVisitedInformation.VisitedInformation;
import antvk.tkms.Utils.ImageUtils;

@RequiresApi(api = Build.VERSION_CODES.N)
public class InfoItemAdapter extends RecyclerView.Adapter<InfoItemAdapter.InformationItemHolder> {

    public List<InformationItem> informationItems;
    public Context context;

    public InfoItemAdapter(Context context, List<InformationItem> informationItems)
    {
        this.informationItems = informationItems;
        this.context = context;
    }

    @Override
    public InformationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.place_info_view, parent, false);

        return new InformationItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InformationItemHolder holder, int position) {
        InformationItem ci = informationItems.get(position);
        holder.setValue(ci);
    }

    @Override
    public int getItemCount() {
        return informationItems.size();
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
        }


        public void setValue(InformationItem item) {

            placeImage.setImageDrawable(ImageUtils.getDrawableFromFile(item.placeImage));
            placeName.setText(item.placeNickName);
            address.setText(item.placeAddress);
            placeDescription.setText(item.placeDescription);
        }
    }
}
