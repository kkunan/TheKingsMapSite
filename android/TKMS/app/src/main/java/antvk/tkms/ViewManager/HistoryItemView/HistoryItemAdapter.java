package antvk.tkms.ViewManager.HistoryItemView;

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

import antvk.tkms.Constants;
import antvk.tkms.R;
import antvk.tkms.Struct.PlaceItem.PlaceItem;
import antvk.tkms.Utils.ImageUtils;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.VisitedInformationHolder> {

    public List<PlaceItem> informationItems;
    public Context context;

    public HistoryItemAdapter(Context context, List<PlaceItem> informationItems)
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
        PlaceItem ci = informationItems.get(position);
        holder.setValue(ci,ci.visitedDate);
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


        public void setValue(PlaceItem item, Date visitedDate) {

            SimpleDateFormat simpleDateFormat = Constants.DMY_DATE_FORMATTER;

            vTitle.setText(item.header);
            String dateTimeText = Html.fromHtml("<b>"+simpleDateFormat.format(visitedDate)+"</b>") +
                    "\n";

            if(boldDateAndDescription!=null)
                boldDateAndDescription.setText(dateTimeText);

            if(item.placeCategory!=null)
            imageView.setImageBitmap(ImageUtils.getBitmapFromAsset(context,
                    Constants.PLACE_CATEGORY_PATH+"/"+item.placeCategory.imagePath));
        }
    }
}
