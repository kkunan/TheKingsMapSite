package antvk.tkms;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class InformationItem {

    public String imageName;
    public String header;
    public String description;
    public Date eventDate;
    public LatLng location;
    public String url;

    public InformationItem(String header, String description, Date eventDate, LatLng location, String imageName, String url)
    {
        this.imageName = imageName;
        this.header = header;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
        this.url = url;
    }

}
