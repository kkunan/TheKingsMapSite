package antvk.tkms.ViewManager.EventView;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import antvk.tkms.R;

public class EventViewHolder extends RecyclerView.ViewHolder{

        protected TextView vTitle;
        protected TextView vEventTime;

        public EventViewHolder(View v) {
            super(v);
            vTitle = v.findViewById(R.id.event_title_text);
            vEventTime = (TextView) v.findViewById(R.id.event_time_text);
        }

        public void setValue(String title, String time)
        {
            vTitle.setText(title);
            vEventTime.setText(time);
        }
}
