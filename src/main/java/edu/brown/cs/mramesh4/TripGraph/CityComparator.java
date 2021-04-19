package edu.brown.cs.mramesh4.TripGraph;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CityComparator implements Comparator<CityNode> {
  private CityNode start;
  private List<CityNode> toVisit;
  private double numerator;
  private double denominator;
  private double midpointX;
  private double midpointY;
  private final Random random1 = new Random();
  private double measure;
  private final double j = 0;
  private final double k = 0.1;

  public CityComparator(double numerator, double denominator, CityNode start,
                        List<CityNode> toVisit, double random, double x, double y, double fiveOh) {
    this.start = start;
    this.toVisit = toVisit;
    this.numerator = numerator;
    this.denominator = denominator;
    this.measure = fiveOh;
    double ratio = numerator / denominator;
//    System.out.println("ratio: " + numerator + "/" + denominator);
    double yTotals = 0;
    double xTotals = 0;

    CityNode base = start;
    if (!toVisit.isEmpty()) {
      if (random1.nextBoolean()) {
        base = toVisit.get(0);
      }
    }
    double myd = Math.abs(midpointY - base.getLat());
    double myx = Math.abs(Math.abs(midpointX) - Math.abs(base.getLong()));

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
//      midpointX += myx * this.randomSignInRange(j, k);
//      midpointY += myd * this.randomSignInRange(j, k);

//      System.out.println("Hola " + x + " " + y);
      midpointX += x ;
      midpointY += y ;
      double weirdo = (ratio * myx)/50;
//      midpointX += x + weirdo;
//      midpointY += y + weirdo;

    } else {
//      System.out.println("nonz");


      midpointX = midpointX + (ratio * myx * random) ;
      midpointY = midpointY + (ratio * myd * random) ;

//        System.out.println("c");

//        System.out.println("offX: " + random * myx);

//        System.out.println("d");
//        midpointY = midpointY + (ratio * myd) + (random * myd);
//        System.out.println("offY" + random * myd);


    }
//    System.out.println("Midpoint x: " + midpointX);
//    System.out.println("Midpoint y: " + midpointY);
  }

  @Override
  public int compare(CityNode a, CityNode b) {

    //Distances to the midpoint
    double aDist = Math.hypot(
        Math.abs(Math.abs((a.getLat()) - Math.abs(midpointY))),
        Math.abs(Math.abs(a.getLong()) - Math.abs(midpointX)));
    double bDist = Math.hypot(
        Math.abs(Math.abs((b.getLat()) - Math.abs(midpointY))),
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
//    if (measure == 3) {
//      hyp -= 0.0000001;
//    } else if (measure == 4) {
//      hyp -= 0.00000005;
//    }
    return Double.compare(aDist - (hyp * a.getPop()), bDist - (hyp * b.getPop()));
//    return Double.compare(Math.pow(1/a.getPop(), aDist), Math.pow(1/b.getPop(), bDist));
//    return Double.compare(Math.pow(aDist, -(0.05 * a.getPop())), Math.pow(bDist, -(0.05 * b.getPop())));
  }

  private double randomSignInRange(double min, double max) {
    double rand = (Math.random() * (max - min)) + min;
    if (random1.nextBoolean()) {
      return rand * -1;
    }
    return rand;
  }
}