package edu.brown.cs.mramesh4.maps;

/**
 * Class for passing user checkin data.
 */
public class UserCheckin {

  private int id;
  private String name;
  private double ts;
  private double lat;
  private double lon;

  /**
   * Instantiates a user checkin.
   * @param userId user id
   * @param username username
   * @param timestamp timestamp
   * @param latitude latitude
   * @param longitude longitude
   */
  public UserCheckin(
      int userId,
      String username,
      double timestamp,
      double latitude,
      double longitude) {

    id = userId;
    name = username;
    ts = timestamp;
    lat = latitude;
    lon = longitude;
  }

  /**
   * Getter for user's id.
   * @return id
   */
  public int getId() {
    return id;
  }

  /**
   * Getter for user's name.
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for user's timestamp.
   * @return timestamp
   */
  public double getTimestamp() {
    return ts;
  }

  /**
   * Getter for user's lat.
   * @return latitude
   */
  public double getLat() {
    return lat;
  }

  /**
   * Getter for user's longitude.
   * @return longitude
   */
  public double getLon() {
    return lon;
  }
}
