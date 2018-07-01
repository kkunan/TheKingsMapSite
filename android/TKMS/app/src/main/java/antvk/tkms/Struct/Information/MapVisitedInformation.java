//package antvk.tkms.Struct.Information;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//public class MapVisitedInformation {
//    public List<VisitedInformation> informationList;
//
//    public MapVisitedInformation(List<VisitedInformation> visitedInformations)
//    {
//        this.informationList = visitedInformations;
//    }
//
//    public List<VisitedInformation> getVisitedList()
//    {
//        List<VisitedInformation> visitedInformations = new ArrayList<>();
//        for(VisitedInformation information : informationList)
//        {
//            if(information.visited)
//                visitedInformations.add(information);
//        }
//        return visitedInformations;
//    }
//
//    public double getVisitedPercentage(){
//        double total = (double)informationList.size();
//
//        if(total==0)
//            return 0;
//
//        double visitedCount = 0.0;
//        for(VisitedInformation information : informationList)
//        {
//            if(information.visited)
//                visitedCount++;
//        }
//
//        return visitedCount/total;
//    }
//
//    // TODO: 27/06/2018 change this, maybe we should just use another boolean in place
//    public boolean getVisitedAt(int id)
//    {
//        for(VisitedInformation information : informationList)
//        {
//            if(information.item!=null && information.item.placeID!=null)
//            if(information.item.id == id)
//                return information.visited;
//        }
//        return false;
//    }
//
//    public static MapVisitedInformation getInitialMapVisitedInformation(
//            List<PlaceItem> placeItems) {
//
//        List<VisitedInformation> visitedInformations = new ArrayList<>();
//        for(PlaceItem item :placeItems)
//        {
//            VisitedInformation information = new VisitedInformation(item,null,false);
//            visitedInformations.add(information);
//        }
//        return new MapVisitedInformation(visitedInformations);
//    }
//
//    public void markVisit(String placeID) {
//        for(VisitedInformation information : informationList)
//        {
//            if(information.item.placeID.equals(placeID)) {
//                information.visited = true;
//                information.visitedDate = Calendar.getInstance().getTime();
//                break;
//            }
//        }
//    }
//
//    public void setVisit(String placeID, boolean b) {
//        for(VisitedInformation information : informationList)
//        {
//            if(information.item.placeID.equals(placeID)) {
//                information.visited = b;
//
//                if(b)
//                {
//                    information.visitedDate = Calendar.getInstance().getTime();
//                }
//
//                else information.visitedDate = null;
//
//                break;
//            }
//        }
//    }
//
//    public static class VisitedInformation {
//       public PlaceItem item;
//        public Date visitedDate;
//        public boolean visited;
//
//        public VisitedInformation(PlaceItem item, Date visitedDate, boolean visited)
//        {
//            this.item = item;
//            this.visitedDate = visitedDate;
//            this.visited = visited;
//        }
//    }
//}
