package antvk.tkms.ViewManager.EventView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import antvk.tkms.Constants;
import antvk.tkms.Struct.Information.PlaceItem.Event;
import antvk.tkms.R;

public class EventViewAdapter extends RecyclerView.Adapter<EventViewHolder> {

        private List<Event> contactList;

        public EventViewAdapter(List<Event> contactList) {
            this.contactList = contactList;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
            Event ci = contactList.get(i);
            eventViewHolder.setValue(ci.title,
                    Constants.DMY_DATE_FORMATTER.format(ci.getDate())
            );
        }

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.cardview, viewGroup, false);

            return new EventViewHolder(itemView);
        }

}
