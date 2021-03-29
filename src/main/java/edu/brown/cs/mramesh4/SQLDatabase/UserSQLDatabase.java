package edu.brown.cs.mramesh4.SQLDatabase;

import edu.brown.cs.mramesh4.maps.UserCheckin;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class allows us to create and access our user database.
 */
public class UserSQLDatabase {
  private String url;
  private Connection conn;

  /**
   * This is a constructor to access our database.
   * @param datafile This is the datafile to access
   */
  public UserSQLDatabase(String datafile)  {
    try {
      url = datafile;
      setConnection();
      Statement stat = conn.createStatement();
      stat.executeUpdate("PRAGMA foreign_keys=ON;");
      stat.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
          + "id INTEGER NOT NULL,"
          + "name VARCHAR(255) NOT NULL, "
          + "ts FLOAT NOT NULL,"
          + "lat FLOAT NOT NULL,"
          + "lon FLOAT NOT NULL);");
      stat.close();
    } catch (SQLException e) {
      System.err.println(e);
    }

  }

  /**
   * Returns a connection.
   * @return conn
   */
  public Connection getConnection() {
    return conn;
  }

  /**
   * This is a method to set a connection our database.
   */
  public void setConnection() {
    try {
      Class.forName("org.sqlite.JDBC");
      String urlToDB = "jdbc:sqlite:" + url;
      conn = DriverManager.getConnection(urlToDB);
      //return conn;
    } catch (ClassNotFoundException | SQLException e) {
      System.err.println(e);
    }
  }

  /**
   * This is a method to add a user to the database.
   * @param userData this is a List of parameters to put on the
   */
  public void addUser(List<String> userData) {
    try {
      PreparedStatement insertionPrep = conn.prepareStatement("INSERT OR IGNORE INTO users "
          + "(id, name, ts, lat, lon)"
          + "VALUES (?, ?, ?, ?, ?)");
      int id = Integer.parseInt(userData.get(0));
      String name = userData.get(1);
      Double big = new BigDecimal(userData.get(2)).doubleValue();
      Double lat = Double.parseDouble(userData.get(3));
      Double lon = Double.parseDouble(userData.get(4));
      insertionPrep.setInt(1, id);
      insertionPrep.setString(2, name);
      insertionPrep.setDouble(3, big);
      insertionPrep.setDouble(4, lat);
      insertionPrep.setDouble(5, lon);
      insertionPrep.executeUpdate();
      insertionPrep.close();
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException | SQLException e) {
      System.err.println(e);
    }
  }

  /**.
   * This is our method to delete a user.
   * @param id This is the id of the user to delete
   * @throws SQLException exception thrown if problems deleting
   */
  public void deleteUser(String id) throws SQLException {
    PreparedStatement deletionPrep = conn.prepareStatement("DELETE FROM users WHERE id = ?");
    deletionPrep.setString(1, id);
    deletionPrep.executeUpdate();
    deletionPrep.close();
  }

  /**
   * This method gets all a users checkins.
   * @param id the id of the user
   * @return a list of checkins
   * @throws SQLException sql exception
   */
  public List<UserCheckin> getUserInfo(String id) throws SQLException {
    PreparedStatement queryPrep = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
    queryPrep.setString(1, id);
    ResultSet rs = queryPrep.executeQuery();
    List<UserCheckin> ret = new ArrayList<>();
    while (rs.next()) {
      UserCheckin curr;
      try {
        int getID = rs.getInt(1);
        String getName = rs.getString(2);
        Double ts = rs.getBigDecimal(3).doubleValue();
        Double lat = rs.getDouble(4);
        Double lon = rs.getDouble(5);
        curr = new UserCheckin(getID, getName, ts, lat, lon);
        ret.add(curr);
      } catch (NumberFormatException | SQLException | NullPointerException e) {
        System.err.println("Can't get user info");
      }
    }
    rs.close();
    queryPrep.close();
    return ret;
  }




}
