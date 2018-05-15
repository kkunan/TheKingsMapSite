package antvk.tkms.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import antvk.tkms.R;
import antvk.tkms.Struct.Information.InformationItem;
import antvk.tkms.Struct.MapAttribute.AvailableMap;

import static antvk.tkms.Activities.MapSelectorActivity.*;

public class EditPlaceActivity extends ActivityWithBackButton{

    PlaceAutocompleteFragment autocompleteFragment;
    AvailableMap map;
    InformationItem item;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place_in_map);

        int itemID = getExtra(MARKER_KEY);
        int mapIndex = getExtra(MAP_ID_KEY);

        if(mapIndex >= 0 && mapIndex < maps.size())
        {
            map = maps.get(mapIndex);
        }

        if(itemID > 0)
        {
            item = map.informationItems.get(itemID);
        }

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {

            }

            @Override
            public void onError(Status status) {

            }
        });

    }
}
