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

    for(CityNode n : toVisit){
      yTotals += n.getLat();
      xTotals += n.getLong();
    }
    yTotals += start.getLat();
    xTotals += start.getLong();

    double midpointY = yTotals / (toVisit.size() + 1);
    double midpointX = xTotals / (toVisit.size() + 1);

    double aDist = Math.hypot(Math.abs(Math.abs(a.getLat()) - Math.abs(midpointY)), Math.abs(Math.abs(a.getLong()) - Math.abs(midpointX)));
    double bDist = Math.hypot(Math.abs(Math.abs(b.getLat()) - Math.abs(midpointY)), Math.abs(Math.abs(b.getLong()) - Math.abs(midpointX)));

    return Double.compare(aDist, bDist);
  }
}
