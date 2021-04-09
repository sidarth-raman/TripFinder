package edu.brown.cs.mramesh4.TripGraph;

import java.sql.Connection;
import java.sql.DriverManager;
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
  private CompleteTripGraph graph;
  private String filepath = "data.sqlite";

  /**
   * Constructor for the GraphBuilder object, calls helper methods and TS algorithm.
   * @param origin is the string of where the user is starting their trip.
   * @param maxDist is the maximum distance they are willing to travel.
   * @param maxNumCities is the maximum number of cities they're willing to visit.
   * @param citiesToVisit is the cities they would like to visit in their travel.
   */
  public GraphBuilder(String origin, double maxDist, int maxNumCities, List<String> citiesToVisit) {
    this.maxDist = maxDist;
    this.maxNumCities = maxNumCities;
    cityNodesToVisit = new ArrayList<>();
    citiesInGraph = new ArrayList<>();
    this.citiesToVisit = citiesToVisit;

    this.setupConnection();
    this.findOrigin(origin);
    this.pullCities();

    for(CityNode n : citiesInGraph){
      System.out.println(n.getName());
    }

    graph = new CompleteTripGraph(citiesInGraph);
    graph.TwoOptTSP(originCity);
  }

  /**
   * This helper method retrieves suggestions for a user's trip based on their location relative
   * to the user's starting location and the cities they would like to visit. Queries the
   * database for these cities and sorts them appropriately.
   */
  private void pullCities() {
    String maxLatBound = "\"" + (originCity.getLat() + maxDist) + "\"";
    String minLatBound = "\"" +  (originCity.getLat() - maxDist) + "\"";
    String maxLongBound = "\"" + (originCity.getLong() + maxDist) + "\"";
    String minLongBound = "\"" + (originCity.getLong() - maxDist) + "\"";

    List<CityNode> bestCities = new ArrayList<>();

    //TODO: Change query such that we are searching within the above bounds.
    PreparedStatement prep = null;
    try {
      prep = conn.prepareStatement("SELECT city, state_id, lat, lng, population, id FROM cities where lat between ? and ? and lng between ? and ? limit 150;");
      prep.setString(1, minLatBound);
      prep.setString(2, maxLatBound);
      prep.setString(3, minLongBound);
      prep.setString(4, maxLongBound);

      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        String name = rs.getString(1) + ", " + rs.getString(2);
        double lat = rs.getDouble(3);
        double lon = rs.getDouble(4);
        bestCities.add(new CityNode(name, lat, lon));
        System.out.println(name);
      }
      rs.close();
      prep.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

    Collections.sort(bestCities, new CityComparator(originCity, cityNodesToVisit));

    if(maxNumCities > citiesInGraph.size()){
      for(int i = 0; i < maxNumCities - citiesInGraph.size(); i++){
        if(!bestCities.isEmpty()) {
          citiesInGraph.add(bestCities.remove(0));
        }
      }
    }

  }

  /**
   * Helper method that creates the CityNode objects of the origin and the cities
   * that the user would like to visit.
   * @param s is the string of the origin city.
   */
  private void findOrigin(String s) {

    originCity = null;
    PreparedStatement prep = null;
    List<String> citiesToSearch = citiesToVisit;
    citiesToSearch.add(s);

    //Building the query based on the cities to visit.
    StringBuilder sb = new StringBuilder("SELECT city, state_id, lat, lng, population, id FROM cities where city in (");
    boolean added = false;
    for(String f : citiesToSearch){
      if (added){
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
        String cityName = rs.getString(1);
        String name = rs.getString(1) + ", " + rs.getString(2);
        double lat = rs.getDouble(3);
        double lon = rs.getDouble(4);
        if (cityName.equals(s)){
          originCity = new CityNode(name, lat, lon);
        } else {
          CityNode toVisit = new CityNode(name, lat, lon);
          cityNodesToVisit.add(toVisit);
          citiesInGraph.add(toVisit);
        }
      }
      rs.close();
      prep.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    if(originCity == null){
      System.out.println("ERROR: origin cannot be null");
    } else {
      citiesInGraph.add(originCity);
    }
  }

  /**
   * Helper method that sets up the connection to the database.
   */
  private void setupConnection() {
    Connection c = null;
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.out.println("ERROR: connection failed");
    }
    String url = "jdbc:sqlite:" + filepath;
    try {
      c = DriverManager.getConnection(url);
    } catch (SQLException e) {
      System.out.println("ERROR: connection failed");
    }
    conn = c;
  }

  /**
   * Getter for the String of the origin, mainly for testing purposes.
   * @return string of origin city.
   */
  public String getOrigin() {
    return originCity.getName();
  }

  /**
   * Getter for the path of the user's trip, this will be called in Main.java and returned to the
   * user.
   * @return list of route for user.
   */
  public List<CityNode> getPath(){
    return graph.TwoOptTSP(originCity);
  }

  /**
   * Retrieves the cities in the graph, mainly for testing purposes.
   * @return list of cities in graph.
   */
  public List<CityNode> getCitiesOfGraph(){
    return citiesInGraph;
  }


}