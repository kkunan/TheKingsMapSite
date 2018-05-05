package antvk.tkms.Utils;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GooglePlaceUtils {

    static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    static final String SUFFIX_GEOCODE = "&sensor=true&key=";

    static final String PLACEDETAIL_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";

    static final String PLACE_ID_TAG = "place_id";

    public static String getPlaceID(LatLng latLng, String key)
    {
        try {
            URL requestURL = new URL(String.format("%s%s,%s%s%s",
                    GEOCODE_URL,
                    latLng.latitude,
                    latLng.longitude,
                    SUFFIX_GEOCODE
                    ,key
                    ));

            System.out.println(requestURL);

            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String st = "";
           // StringBuffer jsonResult = new StringBuffer();
            while((st=reader.readLine())!=null)
            {
                if(st.contains(PLACE_ID_TAG))
                {
                    String[] split = st.split(" ");
                    return split[split.length-1].replaceAll("\\\"","").replace(",","");
                }
                System.out.println("line "+st);
                //jsonResult.append(st);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }



}
