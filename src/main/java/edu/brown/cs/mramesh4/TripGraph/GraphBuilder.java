package edu.brown.cs.mramesh4.TripGraph;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class that builds the Graph from a User's input and preferences, calls the TS algorithm,
 * and returns that path (to Main.java where it's sent to the front end).
 */
public class GraphBuilder {

  private double idealDist;
  private int maxNumCities;
  private Connection conn;
  private CityNode originCity;
  private List<CityNode> cityNodesToVisit;
  private List<String> citiesToVisit;
  private List<CityNode> citiesInGraph;
  private CompleteTripGraph<CityNode, CityEdge> graph;
  private final double EARTH_RADIUS = 3960; //In miles
  private final double RAD_TO_DEGREE = (180 / Math.PI);
  private final double DEGREE_TO_RAD = (Math.PI / 180);
  private final double earthRadius = 3958.8; //miles
  private final Random random = new Random();
  private double r;
  private final double lowBound = 0.85;
  private final double highBound = 1.05;


  /**
   * Constructor for the GraphBuilder object, calls helper methods and TS algorithm.
   *
   * @param origin        is the string of where the user is starting their trip.
   * @param idealDist     is the maximum distance they are willing to travel.
   * @param maxNumCities  is the maximum number of cities they're willing to visit.
   * @param citiesToVisit is the cities they would like to visit in their travel.
   */
  public GraphBuilder(Connection conn, String origin, double idealDist, int maxNumCities,
                      List<String> citiesToVisit) {
    this.idealDist = idealDist;
    this.maxNumCities = maxNumCities;
    cityNodesToVisit = new ArrayList<>();
    citiesInGraph = new ArrayList<>();
    this.citiesToVisit = citiesToVisit;
    this.conn = conn;

    this.findOrigin(origin);
    this.pullCities();
    graph = new CompleteTripGraph<>(citiesInGraph);

  }

  /**
   * This helper method retrieves suggestions for a user's trip based on their location relative
   * to the user's starting location and the cities they would like to visit. Queries the
   * database for these cities and sorts them appropriately.
   */
  private void pullCities() {
    double maxDistLat = (idealDist / EARTH_RADIUS) * RAD_TO_DEGREE;
    r = EARTH_RADIUS * Math.cos(originCity.getLat() * DEGREE_TO_RAD);
    double maxDistLong = (idealDist / r) * RAD_TO_DEGREE;
    String maxLatBound = "\"" + Math.round((originCity.getLat() + maxDistLat)) + "\"";
    String minLatBound = "\"" + Math.round((originCity.getLat() - maxDistLat)) + "\"";
    String maxLongBound = "\"" + Math.round(originCity.getLong() + maxDistLong) + "\"";
    String minLongBound = "\"" + Math.round(originCity.getLong() - maxDistLong) + "\"";
//    System.out.println(
//      "LAT BOUNDS: " + minLatBound + "-" + maxLatBound + " LONG BOUNDS: " + minLongBound + "-" +
//        maxLongBound);
    List<CityNode> bestCities = new ArrayList<>();

    int temp = citiesInGraph.size();
    int numToAdd = maxNumCities - temp;


    if (numToAdd != 0) {
      PreparedStatement prep = null;
      try {
        prep = conn.prepareStatement(
            "SELECT city, state_id, lat, lng, population, id FROM cities where lat between ? and ? and lng between ? and ?;");
        prep.setDouble(1, Math.round((originCity.getLat() - maxDistLat)));
        prep.setDouble(2, Math.round((originCity.getLat() + maxDistLat)));
        prep.setDouble(3, Math.round(originCity.getLong() - maxDistLong));
        prep.setDouble(4, Math.round(originCity.getLong() + maxDistLong));

        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
          String name = rs.getString(1) + ", " + rs.getString(2);
          if (!citiesToVisit.contains(name) && !originCity.getName().equals(name)) {
            double lat = rs.getDouble(3);
            double lon = rs.getDouble(4);
            int pop = rs.getInt(5);
            bestCities.add(new CityNode(name, lat, lon, pop));
          }
        }
        rs.close();
        prep.close();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }

      boolean activatePlanB = false;
      double offFromTarget = 0;
      if (cityNodesToVisit.size() == 0) {
        offFromTarget = idealDist;
      } else {
        offFromTarget =
            (idealDist) - (Math.abs(this.haversineDist(originCity, cityNodesToVisit.get(0))));
      }
      if (offFromTarget > 0) {
        activatePlanB = true;
        offFromTarget /= (Math.pow(numToAdd, 2));
      }

      double random = this.randomInRange(-0.5, 0.5);

      double x = 0;
      double y = 0;

      if (numToAdd % 2 == 1) {
//        System.out.println("Triggered: " + numToAdd);
        if (numToAdd == 1) {
          if (activatePlanB) {

            x = this.newX(offFromTarget, numToAdd);
            y = this.newY(offFromTarget, numToAdd);
          }
          Collections.sort(bestCities,
              new CityComparator(0, 1, originCity, cityNodesToVisit, 0, x, y));
          citiesInGraph.add(bestCities.remove(0));
        } else {
          for (int i = 0 - ((numToAdd - 1) / 2), j = 1; i < ((numToAdd - 1) / 2) + 1; i++, j++) {
            if (!bestCities.isEmpty()) {

//              x = 0;
//              y = 0;
              if (activatePlanB) {
                x = this.newX(offFromTarget, numToAdd);
                y = this.newY(offFromTarget, numToAdd);
              }
              Collections.sort(bestCities,
                  new CityComparator(i, numToAdd - 1, originCity, cityNodesToVisit, random, x,
                      y));
              citiesInGraph.add(bestCities.remove(0));
//              activatePlanB = false;
            }
          }
        }
      } else {
        if (maxNumCities > temp) {
          for (int i = -1 * (numToAdd/2), j = 1; i < numToAdd + (numToAdd/2); i += 2, j++) {
            if (!bestCities.isEmpty()) {
              if (activatePlanB) {
                x = this.newX(offFromTarget, numToAdd);
                y = this.newY(offFromTarget, numToAdd);
              }
              Collections.sort(bestCities,
                  new CityComparator(i, numToAdd, originCity, cityNodesToVisit, random, x, y));
              citiesInGraph.add(bestCities.remove(0));
//              activatePlanB = false;
            }
          }
        }
      }
    }

  }

  /**
   * Helper method that creates the CityNode objects of the origin and the cities
   * that the user would like to visit.
   *
   * @param s is the string of the origin city.
   */
  private void findOrigin(String s) {
    String temp = s;
    originCity = null;
    PreparedStatement prep = null;
    List<String> citiesToSearch = new ArrayList<>();
    for (String city : citiesToVisit) {
      citiesToSearch.add(city.split(",")[0]);
    }
    citiesToSearch.add(s.split(",")[0]);

    //Building the query based on the cities to visit.
    StringBuilder sb = new StringBuilder(
        "SELECT city, state_id, lat, lng, population, id FROM cities where city in (");
    boolean added = false;
    for (String f : citiesToSearch) {
      if (added) {
        sb.append(",");
      }
      sb.append("'");
      sb.append(f);
      sb.append("'");
      added = true;
    }
    sb.append(")");

    try {
      prep = conn.prepareStatement(sb.toString());
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        String name = rs.getString(1) + ", " + rs.getString(2);
        double lat = rs.getDouble(3);
        double lon = rs.getDouble(4);
        int pop = rs.getInt(5);
        if (name.equals(temp)) {
          originCity = new CityNode(name, lat, lon, pop);
        } else if (citiesToVisit.contains(name) && !name.equals(temp)) {
          CityNode toVisit = new CityNode(name, lat, lon, pop);
          cityNodesToVisit.add(toVisit);
          citiesInGraph.add(toVisit);
        }
      }
      rs.close();
      prep.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    if (originCity == null) {
      System.out.println("ERROR: origin cannot be null");
    } else {
      citiesInGraph.add(originCity);
    }
  }


  /**
   * Getter for the String of the origin, mainly for testing purposes.
   *
   * @return string of origin city.
   */
  public String getOrigin() {
    return originCity.getName();
  }

  /**
   * Getter for the path of the user's trip, this will be called in Main.java and returned to the
   * user.
   *
   * @return list of route for user.
   */
  public List<CityNode> getPath() {
    return graph.christTSP(originCity);
  }

  /**
   * Retrieves the cities in the graph, mainly for testing purposes.
   *
   * @return list of cities in graph.
   */
  public List<CityNode> getCitiesOfGraph() {
    return citiesInGraph;
  }

  private double haversineDist(CityNode a, CityNode b) {
    // Find distance in radians between latitudes and longitudes

    double distLat = Math.toRadians(a.getLat() - b.getLat());
    double distLong = Math.toRadians(a.getLong() - b.getLong());

    // Convert latitudes to radians
    double startLat = Math.toRadians(b.getLat());
    double endLat = Math.toRadians(a.getLat());

    double calc = Math.pow(Math.sin(distLat / 2), 2)
        + Math.pow(Math.sin(distLong / 2), 2) * Math.cos(startLat) * Math.cos(endLat);
    double calc2 = 2 * Math.asin(Math.sqrt(calc));
    return earthRadius * calc2;
  }

  private double newX(double offFromTarget, double numToAdd) {
    return (((offFromTarget / r) * RAD_TO_DEGREE) * this.randomSignInRange(lowBound, highBound)) /
        numToAdd;
  }

  private double newY(double offFromTarget, double numToAdd) {
    return (((offFromTarget / EARTH_RADIUS) * RAD_TO_DEGREE) *
        this.randomSignInRange(lowBound, highBound)) / numToAdd;
  }

  private double randomInRange(double min, double max) {
    return (Math.random() * (max - min)) + min;
  }

  private double randomSignInRange(double min, double max) {
    double rand = (Math.random() * (max - min)) + min;
    if (random.nextBoolean()) {
      return rand * -1;
    }
    return rand;
  }

}