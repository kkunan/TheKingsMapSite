//package antvk.tkms.Utils;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.PlaceBufferResponse;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.internal.PlaceEntity;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.lang.reflect.Type;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import antvk.tkms.Activities.MapsActivity;
//
//public class GooglePlaceUtils {
//
//    static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
//    static final String SUFFIX_GEOCODE = "&sensor=true&key=";
//
//    static final String PLACE_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
//    static final String PLACEDETAIL_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
//
//    static final String PLACE_ID_TAG = "place_id";
//
//    public static Place getPlaceDescription(Context context, final String placeID) {
//        StringBuffer jsonResult = new StringBuffer();
//        try {
////            URL requestURL = new URL(
////                    String.format("%s%s&%s=%s"
////                            , PLACE_URL
////                            , placeID,
////                            "key",
////                            apiKey
////                    )
////            );
////
////            System.out.println(requestURL);
////            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
////
////            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
////            String st = "";
////
////            while ((st = reader.readLine()) != null) {
////                System.out.println(st);
////                jsonResult.append(st);
////            }
////
////            Type listType = new TypeToken<Task<PlaceBufferResponse>>(){}.getType();
////            Task<PlaceBufferResponse> task = (new Gson()).fromJson(jsonResult.toString(), listType);
////
////            PlaceBufferResponse response = task.getResult();
////            Place place = response.get(0);
////            return place;
//
//
//
//
//            return null;
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
////    public static String getPlaceID(LatLng latLng, String key)
////    {
////        try {
////            URL requestURL = new URL(String.format("%s%s,%s%s%s",
////                    GEOCODE_URL,
////                    latLng.latitude,
////                    latLng.longitude,
////                    SUFFIX_GEOCODE
////                    ,key
////                    ));
////
////            System.out.println(requestURL);
////
////            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
////            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
////
////            String st = "";
////           // StringBuffer jsonResult = new StringBuffer();
////            while((st=reader.readLine())!=null)
////            {
////                if(st.contains(PLACE_ID_TAG))
////                {
////                    String[] split = st.split(" ");
////                    return split[split.length-1].replaceAll("\\\"","").replace(",","");
////                }
////                System.out.println("line "+st);
////                //jsonResult.append(st);
////            }
////
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        return  null;
////    }
//
//
//
//
//}
