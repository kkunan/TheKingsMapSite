package antvk.tkms;

import java.util.ArrayList;
import java.util.List;

public class MapVisitedInformation {
    public List<VisitedInformation> informationList;

    public MapVisitedInformation(List<VisitedInformation> visitedInformations)
    {
        this.informationList = visitedInformations;
    }

    public double getVisitedPercentage(){
        double total = (double)informationList.size();

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
            VisitedInformation information = new VisitedInformation(item.placeID,false);
            visitedInformations.add(information);
        }
        return new MapVisitedInformation(visitedInformations);
    }

    public void markVisit(String placeID) {
        for(VisitedInformation information : informationList)
        {
            if(information.placeID.equals(placeID)) {
                information.visited = true;
                break;
            }
        }
    }

    public void setVisit(String placeID, boolean b) {
        for(VisitedInformation information : informationList)
        {
            if(information.placeID.equals(placeID)) {
                information.visited = b;
                break;
            }
        }
    }

    public static class VisitedInformation {
        String placeID;
        boolean visited;

        public VisitedInformation(String placeID, boolean visited)
        {
            this.placeID = placeID;
            this.visited = visited;
        }
    }
}
