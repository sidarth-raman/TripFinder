package edu.brown.cs.mramesh4.TripGraph;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CityDatabaseReader {

  private Connection conn;
  private String filepath;
  private List<String> cityList;
  private List<CityNode> cityNodes;


  public CityDatabaseReader(String filepath){
    cityList = new ArrayList<>();
    cityNodes = new ArrayList<>();
    this.filepath = filepath;
    this.setupConnection();
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

  public Connection connect(){
    return conn;
  }

  public void readDB(){
    PreparedStatement prep = null;

    try {
      prep = conn.prepareStatement("select city, state_id from cities;");
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        String name = rs.getString(1) + ", " + rs.getString(2);
        cityList.add(name);
      }
      rs.close();
      prep.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }

  public void loadCityDB(){
    PreparedStatement prep = null;

    try {
      prep = conn.prepareStatement("select city, state_id, lat, lng, population from cities;");
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        String name = rs.getString(1) + ", " + rs.getString(2);
        double lat = rs.getDouble(3);
        double longitude = rs.getDouble(4);
        int pop = rs.getInt(5);
        CityNode curr = new CityNode(name, lat, longitude, pop);
        cityNodes.add(curr);
      }
      rs.close();
      prep.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }

  public double[] getCoordinates(String city, String state) {
    PreparedStatement prep = null;
    double lat = 0;
    double lon = 0;
    try {
      prep = conn.prepareStatement("select lat, lng from cities WHERE city = ? AND state_id = ?;");
      prep.setString(1, city);
      prep.setString(2, state);
      ResultSet rs = prep.executeQuery();
      if (rs.next()) {
        lat = rs.getDouble(1);
        lon = rs.getDouble(2);
      }
      rs.close();
      prep.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return new double[]{lat, lon};
  }

  public List<String> getCities(){
    return cityList;
  }

  public List<CityNode> getNodes(){
    return cityNodes;
  }
}
