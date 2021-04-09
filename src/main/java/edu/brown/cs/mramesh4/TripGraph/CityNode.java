package edu.brown.cs.mramesh4.TripGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * This is a class to model our CityNodes: CityNodes are nodes that represent
 * a point on our Graph. Each point on the graph can calculate distances
 * between other cities as well as store information about itself.
 */
public class CityNode implements TripGraphNode<CityNode, CityEdge> {
  private String name;
  private double lat;
  private double longit;
  private HashMap<String, CityNode> connectingNodes;
  private HashMap<String, CityEdge> connectingEdges;
  private double weight;
  private double distance;
  private static final double EARTH_RADIUS_IN_KM = 6371;

  /**
   * This is a constructor for a cityNode. A cityNode right now takes in
   * a name, a latitude and a longitude.
   * @param name  Name of city that will be stored in graph
   * @param lat  Latitude of city
   * @param lon  Longitude of city
   */
  public CityNode(String name, double lat, double lon) {
    this.name = name;
    this.lat = lat;
    this.longit = lon;
    connectingNodes = new HashMap<>();
    connectingEdges = new HashMap<>();
    this.weight = Double.MAX_VALUE;
    this.distance = Double.MAX_VALUE;
  }
  /**
   * Returns the name of the cityNode.
   * @return The name of the City
   */
  public String getName() {
    return name;
  }
  /**
   * Returns the latitude of the city.
   * @return The lat of the city
   */
  public double getLat() {
    return lat;
  }
  /**
   * Returns the longitude of the city.
   * @return The longitude of the city
   */

  public double getLong() {
    return longit;
  }

  /**
   * Returns the weight of the node for algorithms.
   * @return weight
   */
  @Override
  public double getWeight() {
    return weight;
  }

  /**
   * Setter method for distance.
   * @param dist distance to set the distance to.
   */
  @Override
  public void setDistance(double dist) {
    distance = dist;
  }

  @Override
  public double getDistance() {
    return distance;
  }

  /**
   * Setter method for weight.
   * @param weight weight to set the weight to.
   */
  public void setWeight(double weight) {
    this.weight = weight;
  }

  /**
   * Accessor method for the nodes map.
   * @return map of nodes
   */
  public HashMap<String, CityNode> getConnectingNodes() {
    return connectingNodes;
  }

  /**
   * Accessor method for edges between nodes (undirected).
   * @return a Map of edges
   */
  public HashMap<String, CityEdge> getConnectingEdges() {
    return connectingEdges;
  }
  /**
   * This is a comparator method that takes in a GraphNode and tells us if the two cities are equal.
   * If the GraphNode is not a cityNode we return false due to comparison errors.
   * @return Boolean representing if nodes are the same node
   */
  @Override
  public boolean equals(CityNode node) {
    return name.equals(node.getName()) && (lat == node.getLat())
      && (longit ==  node.getLong());
  }

  /**
   * This is a method that insertsEdges between the node. Since
   * we have an undirected graph it will reinsert.
   * @param node node to insert edges between
   */
  @Override
  public void insertEdges(CityNode node) {
    if (node != null && !node.equals(this)) {
      String name = node.getName();
      String edgeName = this.name + "->" + node.getName();
      CityEdge edge = new CityEdge(this, node, edgeName);
      CityEdge edge2 = new CityEdge(node, this, edgeName);
      connectingNodes.put(name, node);
      connectingEdges.put(name, edge);
      node.getConnectingNodes().put(this.name, this);
      node.getConnectingEdges().put(this.name, edge2);
    }
  }

  /**
   * This is a method that deletes an edge between two nodes.
   * @param node edge to delete between two edges
   */
  @Override
  public void deleteEdge(CityNode node) {
    String name = node.getName();
    connectingNodes.remove(name);
    connectingEdges.remove(name);
    node.getConnectingNodes().remove(this.name);
    node.getConnectingEdges().remove(this.name);
  }

  @Override
  public void clearGraphEdges() {
    connectingNodes.clear();
    connectingEdges.clear();
  }


  //TODO: Change this to an A* or more significant path-finding version
  /**
   * Returns the euclidean distance between two city nodes.
   * @param a a node to check distance from
   * @return distance between two cities
   */
  @Override
  public double distanceBetween(CityNode a) {
    double aX = a.getLong();
    double aY = a.getLat();
    return Math.sqrt(Math.pow(Math.abs((aX - this.longit)), 2)
      + Math.pow(Math.abs((aY - this.lat)), 2));
  }

  /**
   * Returns a list of outgoing edges.
   * @return a list of outgoing edges.
   */
  @Override
  public List<CityEdge> getOutgoingEdges() {
    Collection<CityEdge> c  = this.getConnectingEdges().values();
    List<CityEdge> ret = new ArrayList<>();
    for (CityEdge edge: c) {
      ret.add(edge);
    }
    return ret;
  }

  /**
   * Returns a list of neighbors.
   * @return nodes of neighbors.
   */
  @Override
  public List<CityNode> getNeighbors() {
    Collection<CityNode> c  = this.getConnectingNodes().values();
    List<CityNode> ret = new ArrayList<>();
    for (CityNode node: c) {
      ret.add(node);
    }
    return ret;
  }

  /**
   * This is for the aStar heuristic, using haversine distance.
   * @param goal goal to check from
   * @return a double for the A* function
   */
  @Override
  public double toGoal(CityNode goal) {
    double thisLat = Math.toRadians(lat);
    double thisLon = Math.toRadians(longit);
    double goalLat = Math.toRadians(goal.getLat());
    double goalLon = Math.toRadians(goal.getLong());
    /**
     * Haversine formula: I got this from
     * https://www.geeksforgeeks.org/program-distance-two-points-earth/
     */
    double dlon = goalLon - thisLon;
    double dlat = goalLat - thisLat;
    double havernmath = Math.pow(Math.sin(dlat / 2), 2)
        + Math.cos(thisLat) * Math.cos(goalLat)
        * Math.pow(Math.sin(dlon / 2), 2);
    double finalanswer = 2 * Math.asin(Math.sqrt(havernmath));
    // Radius of earth in kilometers. Use 3956 for miles
    return (finalanswer * EARTH_RADIUS_IN_KM);
  }


  //TODO: Code a getActivities() method

}
