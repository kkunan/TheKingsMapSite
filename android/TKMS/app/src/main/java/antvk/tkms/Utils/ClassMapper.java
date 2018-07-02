package antvk.tkms.Utils;

import antvk.tkms.Activities.Edit.EditMapActivity;
import antvk.tkms.Activities.Edit.EditPlaceActivity;
import antvk.tkms.Activities.Show.EventDescriptionActivity;
import antvk.tkms.Activities.Show.MapSelectorActivity;
import antvk.tkms.Activities.Show.MapsActivity;
import antvk.tkms.Activities.Show.PlaceDescriptionActivity;

public interface ClassMapper {

    static final String classIntentKey = "class";
    static Class get(String key)
    {
        switch (key)
        {
            case "EventDescriptionActivity": return EventDescriptionActivity.class;
            case "EditMapActivity": return EditMapActivity.class;
            case "EditPlaceActivity": return EditPlaceActivity.class;
            case "MapsActivity": return MapsActivity.class;
            case "MapSelectorActivity": return MapSelectorActivity.class;
            case "MarkerEventActivity": return PlaceDescriptionActivity.class;

            default: return null;
        }
    }
}
