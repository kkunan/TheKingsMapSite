package antvk.tkms;

import android.location.Address;
import android.location.Geocoder;
import android.test.suitebuilder.annotation.MediumTest;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import antvk.tkms.Utils.GooglePlaceUtils;

public class GeocodeTest {

    @Test
    public void test(){
        System.out.println("split "+GooglePlaceUtils.getPlaceID(new LatLng(13.748170, 100.533846),"AIzaSyBJQQlTagxTCriUnnDG8RD_wZcVdBE3cPI"));
    }
}
