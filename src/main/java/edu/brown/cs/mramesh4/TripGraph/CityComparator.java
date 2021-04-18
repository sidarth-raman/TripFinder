package edu.brown.cs.mramesh4.TripGraph;

import java.util.Comparator;
import java.util.List;

public class CityComparator implements Comparator<CityNode> {
  private CityNode start;
  private List<CityNode> toVisit;
  private double totalDistance;
  private double numerator;
  private double denominator;
  private double midpointX;
  private double midpointY;

  public CityComparator(double numerator, double denominator, CityNode start,
                        List<CityNode> toVisit, int ind, double random, double x, double y) {
    this.start = start;
    this.toVisit = toVisit;
    this.numerator = numerator;
    this.denominator = denominator;
    double ratio = numerator / denominator;
    System.out.println("ratio: " + numerator + "/" + denominator);
    double yTotals = 0;
    double xTotals = 0;

    //Calculating midpoint of lat/long by finding average
    for (CityNode n : toVisit) {
      yTotals += n.getLat();
      xTotals += n.getLong();
    }
    yTotals += start.getLat();
    xTotals += start.getLong();

    midpointY = (yTotals / (toVisit.size() + 1));
    midpointX = (xTotals / (toVisit.size() + 1));



    if (x != 0 || y != 0) {
      System.out.println("Hola " + x + " " + y);
      midpointX += x;
      midpointY += y;

    } else {

      double myd = Math.abs(midpointY - start.getLat());
      double myx = Math.abs(Math.abs(midpointX) - Math.abs(start.getLong()));

      if (ind % 2 == 0) {
        midpointX = midpointX + (ratio * myx) + (random * myx);
        System.out.println("offX: " + random * myx);

      } else {
        midpointY = midpointY + (ratio * myd) + (random * myd);
        System.out.println("offY" + random * myd);
      }


    }
    System.out.println("Midpoint x: " + midpointX);
    System.out.println("Midpoint y: " + midpointY);
  }

  @Override
  public int compare(CityNode a, CityNode b) {

    //Distances to the midpoint
    double aDist = Math.hypot(
        Math.abs(Math.abs((a.getLat()) - Math.abs(midpointY)) * (numerator / denominator)),
        Math.abs(Math.abs(a.getLong()) - Math.abs(midpointX)));
    double bDist = Math.hypot(
        Math.abs(Math.abs((b.getLat()) - Math.abs(midpointY)) * (numerator / denominator)),
        Math.abs(Math.abs(b.getLong()) - Math.abs(midpointX)));


//    if(Math.abs(ratio - 1) <= 0.2){
//      System.out.println("COMPARISON: " + ratio + " " + a.getName() + " :" + a.getPop() + " " +b.getName() + " :" + b.getPop());
////      return Double.compare(a.getPop(), b.getPop());
//      if(a.getPop() > b.getPop()){
//        return -1;
//      } else if (a.getPop() == b.getPop()){
//        return 0;
//      } else {
//        return 1;
//      }
//    }
    double hyp = 0.0000005;
    return Double.compare(aDist - (hyp * a.getPop()), bDist - (hyp * b.getPop()));
//    return Double.compare(Math.pow(1/a.getPop(), aDist), Math.pow(1/b.getPop(), bDist));
//    return Double.compare(Math.pow(aDist, -(0.05 * a.getPop())), Math.pow(bDist, -(0.05 * b.getPop())));
  }
}