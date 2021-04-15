package edu.brown.cs.mramesh4.TripGraph;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that builds the Graph from a User's input and preferences, calls the TS algorithm,
 * and returns that path (to Main.java where it's sent to the front end).
 */
public class GraphBuilder {

  private double maxDist;
  private int maxNumCities;
  private Connection conn;
  private CityNode originCity;
  private List<CityNode> cityNodesToVisit;
  private List<String> citiesToVisit;
  private List<CityNode> citiesInGraph;
  private CompleteTripGraph<CityNode, CityEdge> graph;
  private String filepath = "data.sqlite";
  private final double EARTH_RADIUS = 3960; //In miles
  private final double RAD_TO_DEGREE = (180 / Math.PI);
  private final double DEGREE_TO_RAD = (Math.PI / 180);

  /**
   * Constructor for the GraphBuilder object, calls helper methods and TS algorithm.
   *
   * @param origin        is the string of where the user is starting their trip.
   * @param maxDist       is the maximum distance they are willing to travel.
   * @param maxNumCities  is the maximum number of cities they're willing to visit.
   * @param citiesToVisit is the cities they would like to visit in their travel.
   * @param conn          is the connection to the database

   */
  public GraphBuilder(Connection conn, String origin, double maxDist, int maxNumCities,
                      List<String> citiesToVisit) {
    this.maxDist = maxDist;
    this.maxNumCities = maxNumCities;
    cityNodesToVisit = new ArrayList<>();
    citiesInGraph = new ArrayList<>();
    this.citiesToVisit = citiesToVisit;
    this.conn = conn;

    this.findOrigin(origin);
    this.pullCities();
    System.out.println("cities pulled");
    graph = new CompleteTripGraph<>(citiesInGraph);

  }

  /**
   * This helper method retrieves suggestions for a user's trip based on their location relative
   * to the user's starting location and the cities they would like to visit. Queries the
   * database for these cities and sorts them appropriately.
   */
  private void pullCities() {
    double maxDistLat = (maxDist / EARTH_RADIUS) * RAD_TO_DEGREE;
    double r = EARTH_RADIUS * Math.cos(originCity.getLat() * DEGREE_TO_RAD);
    double maxDistLong = (maxDist / r) * RAD_TO_DEGREE;
    String maxLatBound = "\"" + Math.round((originCity.getLat() + maxDistLat)) + "\"";
    String minLatBound = "\"" + Math.round((originCity.getLat() - maxDistLat)) + "\"";
    String maxLongBound = "\"" + Math.round(originCity.getLong() + maxDistLong) + "\"";
    String minLongBound = "\"" + Math.round(originCity.getLong() - maxDistLong) + "\"";
    System.out.println(
        "LAT BOUNDS: " + minLatBound + "-" + maxLatBound + " LONG BOUNDS: " + minLongBound + "-"
          + maxLongBound);
    List<CityNode> bestCities = new ArrayList<>();


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

    Collections.sort(bestCities, new CityComparator(originCity, cityNodesToVisit, maxDist));

    int temp = citiesInGraph.size();
    if (maxNumCities > temp) {
      for (int i = 0; i < maxNumCities - temp; i++) {
        if (!bestCities.isEmpty()) {
          citiesInGraph.add(bestCities.remove(0));
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
          System.out.println("Adding city to visit: " + name);
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
    //return graph.TwoOptTSP(originCity);
  }

  /**
   * Retrieves the cities in the graph, mainly for testing purposes.
   *
   * @return list of cities in graph.
   */
  public List<CityNode> getCitiesOfGraph() {
    return citiesInGraph;
  }


}
