package edu.brown.cs.student.tripfinder;


import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * MapsDatabase class handles the connection to the SQL database.
 */
public class CityDatabase {

  private static Connection conn = null;
  private PreparedStatement currentStatement;
  private ResultSet currentResultSet;
  private Map<String, City> cityMap;


  /**
   * Instantiates the database, creating tables if necessary.
   * Automatically loads files.
   *
   * @param filename file name of SQLite3 database to open.
   * @throws SQLException - when sqlite database is improperly formatted
   *                      or otherwise reading causes and error
   * @throws IOException  - When invalid inputs are given, usually when sqlite file cannot befound
   */
  public CityDatabase(String filename) {
    cityMap = new HashMap<>();
    // this line loads the driver manager class, and must be
    // present for everything else to work properly
    try {
      Class.forName("org.sqlite.JDBC");
      String urlToDB = "jdbc:sqlite:" + filename;
      conn = DriverManager.getConnection(urlToDB);
      this.currentStatement = null;
      this.currentResultSet = null;
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
    try {
      this.readAllCities();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }

  /**
   * Reads all the nodes from the database into the nodes list.
   *
   * @throws SQLException if something goes wrong with the SQL
   */
  private void readAllCities() throws SQLException {

    PreparedStatement prep = conn.prepareStatement("SELECT city, state_id, lat, lng, population FROM cities;");
    ResultSet rs = prep.executeQuery();
    int i = 0;
    while (rs.next()) {
      String name = rs.getString(1);
      String state = rs.getString(2);
      double lat = rs.getDouble(3);
      double lon = rs.getDouble(4);
      double pop = rs.getDouble(5);
      cityMap.put(name, new City(name, state, lat, lon, pop));
      i++;
    }
    rs.close();
    prep.close();
  }

  /**
   * Run a given query string in the database.
   * If runQuery(...) is called before finishQuery() closes,
   * the most recent runQuery, finishQuery() is called first.
   *
   * @param queryString - Exact string of query to run
   * @return ResultSet of returned rows.
   * Iterate over the "rs" return object with "while(rs.next()) {...}"
   * @throws SQLException on error
   */
  public ResultSet runQuery(String queryString) throws SQLException {

    if (this.currentResultSet != null || this.currentStatement != null) {
      this.finishQuery();
    }
    this.currentStatement = conn.prepareStatement(queryString);
    this.currentResultSet = this.currentStatement.executeQuery();
    return this.currentResultSet;
  }

  /**
   * Run this after executing query to close query connections.
   *
   * @throws SQLException on error.
   */
  public void finishQuery() throws SQLException {
    if (this.currentResultSet != null) {
      this.currentResultSet.close();
      this.currentResultSet = null;
    }
    if (this.currentStatement != null) {
      this.currentStatement.close();
      this.currentStatement = null;
    }
  }

}
