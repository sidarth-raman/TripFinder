package edu.brown.cs.mramesh4.TripGraph;

import java.util.Comparator;
import java.util.List;

/**
 * This is a comparator class for cityNodes.
 */
public class CityComparator implements Comparator<CityNode> {
  private CityNode start;
  private List<CityNode> toVisit;
  private double totalDistance;
  private double distanceWeight;
  private static double popWeight = 0.0000001;

  /**
   * This is a constructor for a city comparator.
   * @param start start node
   * @param toVisit list of cities to visit
   * @param totalDistance distance
   */
  public CityComparator(CityNode start, List<CityNode> toVisit, double totalDistance) {
    this.start = start;
    this.toVisit = toVisit;
    this.totalDistance = totalDistance;
  }

  /**
   * This is a comparator method to decide how to sort the cities.
   * @param a node to compare
   * @param b node to compare
   * @return integer representing whether a < b | a == b | a > b
   */
  @Override
  public int compare(CityNode a, CityNode b) {
    double yTotals = 0;
    double xTotals = 0;
    //Calculating midpoint of lat/long by finding average
    for (CityNode n : toVisit) {
      yTotals += n.getLat();
      xTotals += n.getLong();
    }
    yTotals += start.getLat();
    xTotals += start.getLong();
    double midpointY = yTotals / (toVisit.size() + 1);
    double midpointX = xTotals / (toVisit.size() + 1);
    //Distances to the midpoint
    double aDist = Math.hypot(Math.abs(Math.abs(a.getLat())
        - Math.abs(midpointY)),
        Math.abs(Math.abs(a.getLong())
          - Math.abs(midpointX)));
    double bDist = Math.hypot(Math.abs(Math.abs(b.getLat())
        - Math.abs(midpointY)),
        Math.abs(Math.abs(b.getLong())
          - Math.abs(midpointX)));
//    if(Math.abs(ratio - 1) <= 0.2){
////      return Double.compare(a.getPop(), b.getPop());
//      if(a.getPop() > b.getPop()){
//        return -1;
//      } else if (a.getPop() == b.getPop()){
//        return 0;
//      } else {
//        return 1;
//      }
//    }
//    System.out.println("non pop comparison");
    double ratio = Math.abs((aDist / bDist) - 1);

    return Double.compare(aDist - popWeight * a.getPop(),  bDist - popWeight * b.getPop());
//    return Double.compare(Math.pow(1/a.getPop(), aDist), Math.pow(1/b.getPop(), bDist));
//    return Double.compare(Math.pow(aDist, -(0.05 * a.getPop())), Math.pow(bDist,
//    -(0.05 * b.getPop())));
  }
}
