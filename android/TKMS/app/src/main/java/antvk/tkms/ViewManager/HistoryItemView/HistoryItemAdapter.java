package antvk.tkms.ViewManager.HistoryItemView;

import android.content.Context;
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

public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.VisitedInformationHolder> {

    public List<VisitedInformation> informationItems;
    public Context context;

    public HistoryItemAdapter(Context context, List<VisitedInformation> informationItems)
    {
        this.informationItems = informationItems;
        this.context = context;
    }

    @Override
    public VisitedInformationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.history_view, parent, false);

        return new VisitedInformationHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VisitedInformationHolder holder, int position) {
        VisitedInformation ci = informationItems.get(position);
        holder.setValue(ci.item,ci.visitedDate);
    }

    @Override
    public int getItemCount() {
        return informationItems.size();
    }


    public class VisitedInformationHolder extends RecyclerView.ViewHolder{

        public TextView vTitle;
        public TextView boldDateAndDescription;
        public ImageView imageView;

        public VisitedInformationHolder(View v) {
            super(v);
            vTitle = v.findViewById(R.id.place_name_text);
            boldDateAndDescription = v.findViewById(R.id.time_visit);
            imageView = v.findViewById(R.id.history_view_pic);
        }

        public void setValue(InformationItem item, Date visitedDate) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            vTitle.setText(item.header);
            String dateTimeText = Html.fromHtml(
                    String.format("<b>%s</b>",simpleDateFormat.format(visitedDate))
            )+ item.placeDescription;

            if(boldDateAndDescription!=null)
                boldDateAndDescription.setText(dateTimeText);

            String imageFolder = MapsActivity.mapIndex==0?Constants.KINGS_IMAGE_FOLDER:Constants.DESTINY_IMAGE_FOLDER;
            imageView.setImageDrawable(ImageUtils.getDrawable(context, imageFolder,item.placeImage));
        }
    }
}
