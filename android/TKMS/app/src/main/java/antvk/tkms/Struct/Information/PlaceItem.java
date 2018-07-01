package antvk.tkms.Struct.Information;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlaceItem {

    public int id = -1;
    public String header;
    public String placeRealName;
    public List<Event> events;
    public LatLng location;

    public PlaceCategory placeCategory;
    public String placeID;
    public String placeAddress;
    public double placeRating;
    public String placeDescription;
    public String userNote;


    public boolean visited = false;
    public Date visitedDate = null;

    public PlaceItem(String placeID, String header, List<Event> events , LatLng location, PlaceCategory placeCategory, String placeDescription)
    {
        this.placeID = placeID;
        this.header = header;
        this.events = events;
        this.location = location;
        this.placeCategory = placeCategory;
        this.placeDescription = placeDescription;
    }

    public PlaceItem() {
        this.events = new ArrayList<>();
    }

    public static class Event {
        public int id = -1;
        public String title;
        public String date;
        public String time;
        public String dateFormat;
        public String description;
        public String imageName;
        public String url;

        public Event(){}

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

    public void setVisit(Calendar calendar)
    {
        this.visitedDate = calendar.getTime();
        visited = true;
    }

    public static class PlaceCategory{

        public String name;
        public String imagePath;

        public PlaceCategory(String name, String imagePath){
            this.name = name;
            this.imagePath = imagePath;
        }
    }

}
