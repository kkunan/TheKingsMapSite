package antvk.tkms.Utils;

import antvk.tkms.Activities.*;

public interface ClassMapper {

    static final String classIntentKey = "class";
    static Class get(String key)
    {
        switch (key)
        {
            case "DescriptionActivity": return DescriptionActivity.class;
            case "EditMapActivity": return EditMapActivity.class;
            case "EditPlaceActivity": return EditMapActivity.class;
            case "MapsActivity": return MapsActivity.class;
            case "MapSelectorActivity": return MapSelectorActivity.class;
            case "MarkerEventActivity": return MarkerEventListActivity.class;

            default: return null;
        }
    }
}
