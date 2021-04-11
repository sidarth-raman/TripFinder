package edu.brown.cs.mramesh4.TripGraph;

import java.util.Comparator;
import java.util.List;

public class CityComparator implements Comparator<CityNode> {
  private CityNode start;
  private List<CityNode> toVisit;

  public CityComparator(CityNode start, List<CityNode> toVisit) {
    this.start = start;
    this.toVisit = toVisit;

  }

  @Override
  public int compare(CityNode a, CityNode b) {
    double yTotals = 0;
    double xTotals = 0;

    //Calculating midpoint of lat/long by finding average
    for(CityNode n : toVisit){
      yTotals += n.getLat();
      xTotals += n.getLong();
    }
    yTotals += start.getLat();
    xTotals += start.getLong();

    double midpointY = yTotals / (toVisit.size() + 1);
    double midpointX = xTotals / (toVisit.size() + 1);

    //Distances to the midpoint
    double aDist = Math.hypot(Math.abs(Math.abs(a.getLat()) - Math.abs(midpointY)), Math.abs(Math.abs(a.getLong()) - Math.abs(midpointX)));
    double bDist = Math.hypot(Math.abs(Math.abs(b.getLat()) - Math.abs(midpointY)), Math.abs(Math.abs(b.getLong()) - Math.abs(midpointX)));

    if(Math.min(aDist, bDist)/Math.max(aDist,bDist) >= 0.8){
//      System.out.println("COMPARISON: " + Math.min(aDist, bDist)/Math.max(aDist,bDist) + " " + a.getName() + b.getName());
      return Integer.compare(a.getPop(), b.getPop());
    }

    return Double.compare(aDist, bDist);
  }
}
