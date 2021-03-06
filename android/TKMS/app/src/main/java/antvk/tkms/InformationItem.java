package antvk.tkms;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InformationItem {

    public String header;
    public List<Event> events;
    public LatLng location;
    public String placeImage;
    public String placeDescription;

    public InformationItem(String header, List<Event> events , LatLng location, String placeImage, String placeDescription)
    {
        this.header = header;
        this.events = events;
        this.location = location;
        this.placeImage = placeImage;
        this.placeDescription = placeDescription;
    }

    public static class Event {
        public String title;
        public String date;
        public String dateFormat;
        public String description;
        public String imageName;
        public String url;

        public Event (String title,String date, String dateFormat, String description, String imageName, String url)
        {
            this.title = title;
            this.date = date;
            this.dateFormat = dateFormat;
            this.description = description;
            this.imageName = imageName;
            this.url = url;
        }

        public Date getDate(){

            try {
                if(dateFormat==null)
                    return null;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);


                return new Date(formatter.parse(date).getTime());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

}
