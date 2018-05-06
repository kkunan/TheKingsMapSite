package antvk.tkms.Struct.Information;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapVisitedInformation {
    public List<VisitedInformation> informationList;

    public MapVisitedInformation(List<VisitedInformation> visitedInformations)
    {
        this.informationList = visitedInformations;
    }

    public double getVisitedPercentage(){
        double total = (double)informationList.size();

        if(total==0)
            return 0;

        double visitedCount = 0.0;
        for(VisitedInformation information : informationList)
        {
            if(information.visited)
                visitedCount++;
        }

        return visitedCount/total;
    }

    public boolean getVisitedAt(String placeID)
    {
        for(VisitedInformation information : informationList)
        {
            if(information.placeID.equals(placeID))
                return information.visited;
        }
        return false;
    }

    public static MapVisitedInformation getInitialMapVisitedInformation(
            List<InformationItem> informationItems) {

        List<VisitedInformation> visitedInformations = new ArrayList<>();
        for(InformationItem item :informationItems)
        {
            VisitedInformation information = new VisitedInformation(item.placeID,null,false);
            visitedInformations.add(information);
        }
        return new MapVisitedInformation(visitedInformations);
    }

    public void markVisit(String placeID) {
        for(VisitedInformation information : informationList)
        {
            if(information.placeID.equals(placeID)) {
                information.visited = true;
                information.visitedDate = Calendar.getInstance().getTime();
                break;
            }
        }
    }

    public void setVisit(String placeID, boolean b) {
        for(VisitedInformation information : informationList)
        {
            if(information.placeID.equals(placeID)) {
                information.visited = b;

                if(b)
                {
                    information.visitedDate = Calendar.getInstance().getTime();
                }

                else information.visitedDate = null;

                break;
            }
        }
    }

    public static class VisitedInformation {
        String placeID;
        Date visitedDate;
        boolean visited;

        public VisitedInformation(String placeID, Date visitedDate, boolean visited)
        {
            this.placeID = placeID;
            this.visitedDate = visitedDate;
            this.visited = visited;
        }
    }
}
