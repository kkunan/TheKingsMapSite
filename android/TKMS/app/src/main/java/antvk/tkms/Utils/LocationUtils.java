package antvk.tkms.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class LocationUtils {

    public LocationListener locationListener;
    static Location currentLocation;

    public LocationUtils(){
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLocation = location;
                System.out.println("current location: "+currentLocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }


    public Location getCurrentLocation(){
        return currentLocation;
    }

    public static double quickDistance(LatLng pointA, LatLng pointB) {
        double R = 6371000;
        double x = (pointA.latitude* Math.PI/180) - (pointB.latitude* Math.PI/180);
        double meanLat = (pointA.latitude + pointB.latitude)/2;
        double y = ((pointA.longitude* Math.PI/180) - (pointB.longitude* Math.PI/180)) * Math.cos(meanLat* Math.PI/180);
        return R * Math.sqrt(x*x + y*y);
    }
}
