package edu.brown.cs.mramesh4.maps;

import edu.brown.cs.mramesh4.Graph.GraphEdge;

/**
 * This class represents a Way object from the database. It implements
 * GraphEdge so that it can be used in our generic Graph class.
 */
public class Way implements GraphEdge<WayNodes, Way> {

  private final String id;
  private WayNodes from;
  private WayNodes to;
  private transient double distance;
  private static final double EARTH_RADIUS_IN_KM = 6371;
  private String type;

  /**
   * In the constructor, we intialize our instance variables using the
   * arguments passed in.
   * @param idArg representing id of way.
   * @param fromArg representing starting node of way
   * @param toArg representing ending node of way
   * @param type Type of node that we are using
   */
  public Way(String idArg, WayNodes fromArg, WayNodes toArg, String type) {
    id = idArg;
    from = fromArg;
    to = toArg;
    this.type = type;
    //use helper method to calculate distance, or weight, of Way
    distance = this.getHaversineDistance(from, to);
  }

  /**
   * Accesor method for type.
   * @return a type variable
   */
  public String getType() {
    return this.type;
  }

  /**
   * Calculates the distance between two waynodes objects.
   * The code for this uses the haversine formula from:
   * https://www.geeksforgeeks.org/program-distance-two-points-earth/
   * @param a first Waynode
   * @param b second Waynode
   * @return double representing haversine distance
   */
  public double getHaversineDistance(WayNodes a, WayNodes b) {
    if (a == null || b == null) {
      return -1;
    }
    double lat1 = Math.toRadians(a.getLat());
    double lon1 = Math.toRadians(a.getLong());
    double lat2 = Math.toRadians(b.getLat());
    double lon2 = Math.toRadians(b.getLong());
    /**
     * Haversine formula: I got this from
     * https://www.geeksforgeeks.org/program-distance-two-points-earth/
     */
    double dlon = lon2 - lon1;
    double dlat = lat2 - lat1;
    double havernmath = Math.pow(Math.sin(dlat / 2), 2)
            + Math.cos(lat1) * Math.cos(lat2)
            * Math.pow(Math.sin(dlon / 2), 2);
    double finalanswer = 2 * Math.asin(Math.sqrt(havernmath));
    // Represents radius of earth in kilometers. Use 3956 for miles
    return (finalanswer * EARTH_RADIUS_IN_KM);
  }

  /**
   * Returns the from WayNode.
   * @return WayNode representing from WayNode
   */
  @Override
  public WayNodes getFrom() {
    return from;
  }

  /**
   * Returns the to WayNode.
   * @return WayNode representing to WayNode
   */
  @Override
  public WayNodes getTo() {
    return to;
  }

  /**
   * Returns WayNode distance, or weight.
   * @return double representing distance between WayNodes
   */
  @Override
  public double getWeight() {
    return distance;
  }

  /**
   * Getter method for id.
   * @return String reprenting ID
   */
  public String getId() {
    return id;
  }

  /**
   * Setter method for from.
   * @param w waynode to set
   */
  public void setFrom(WayNodes w) {
    from = w;
    distance = getHaversineDistance(to, from);
  }

  /**
   * Setter method for to.
   * @param w waynode to set
   */
  public void setTo(WayNodes w) {
    to = w;
    distance = getHaversineDistance(to, from);
  }

}
