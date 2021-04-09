package edu.brown.cs.mramesh4.TripGraph;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CityDatabaseReader {

  private Connection conn;
  private String filepath;
  private List<String> cityList;


  public CityDatabaseReader(String filepath){
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
      prep = conn.prepareStatement("select * from cities;");
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

  public List<String> getCities(){
    return cityList;
  }
}
