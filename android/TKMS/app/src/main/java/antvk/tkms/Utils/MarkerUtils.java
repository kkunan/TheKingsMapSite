package antvk.tkms.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Map;

import antvk.tkms.Constants;
import antvk.tkms.Activities.MapsActivity;
import antvk.tkms.R;
import antvk.tkms.Struct.Information.InformationItem;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MarkerUtils {

    public static final String INACTIVE_NAME = "pin_inactive.png";
    public static final String ACTIVE_NOVISIT_NAME = "pin_active_no.png";
    public static final String ACTIVE_VISITED_NAME = "pin_active_on.png";


    public static void enableMarker(LayoutInflater inflater, Context context, Marker marker)
    {
        List<Marker> markerList = MapsActivity.markerList;

        String fileName;
        InformationItem item = MapsActivity.markerInformationItemMap.get(marker);
        if(MapsActivity.mapVisitedInformation.getVisitedAt(item.placeID))
            fileName = ACTIVE_VISITED_NAME;
        else
            fileName = ACTIVE_NOVISIT_NAME;
        //todo: pick active/inactive
        marker.setIcon(
                BitmapDescriptorFactory.fromBitmap(
                        createStoreMarker(inflater,context
                                ,fileName,
                                MapsActivity.markerInformationItemMap
                                        .get(marker)
                                        .header)
                )
        );
        for(Marker mk : markerList)
        {
            if(marker.equals(mk))
                continue;

            disableMarker(inflater, context, mk);
        }
    }

    public static void disableMarker(LayoutInflater inflater, Context context, Marker marker)
    {
        marker.setIcon(
                BitmapDescriptorFactory.fromBitmap(
                        createStoreMarker(inflater,context
                                ,INACTIVE_NAME,
                                MapsActivity.markerInformationItemMap
                                        .get(marker)
                                        .header)
                )
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static Bitmap createStoreMarker(LayoutInflater inflater, Context context, String imageBG, String text) {
        View markerLayout = inflater.inflate(R.layout.marker_inactive, null);

        TextView markerRating = (TextView) markerLayout.findViewById(R.id.marker_text);

//        LinearLayout leftBackground = markerLayout.findViewById(R.id.left_background);
//        leftBackground.setBackground(
//                LocationUtils.getDrawable(context,Constants.PIN_FOLDER,imageName)
//        );

        LinearLayout markerImage = (LinearLayout) markerLayout.findViewById(R.id.marker_image);

        markerImage.setBackground(
                ImageUtils.getDrawable(context,Constants.PIN_FOLDER,imageBG)
        );


        text = text.replaceAll("\\ ","\n")+"\n";
        markerImage.setGravity(Gravity.CENTER_VERTICAL);
        markerRating.setText(text);
        if(!imageBG.equals(INACTIVE_NAME)) {

        }
        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }
}
