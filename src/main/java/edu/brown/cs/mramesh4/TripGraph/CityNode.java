package edu.brown.cs.mramesh4.TripGraph;

import java.util.HashMap;

/**
 * This is a class to model our CityNodes: CityNodes are nodes that represent
 * a point on our Graph. Each point on the graph can calculate distances
 * between other cities as well as store information about itself.
 */
public class CityNode implements GraphNode {
  private String name;
  private double lat;
  private double longit;
  private HashMap<String, CityNode> connectingNodes;
  private HashMap<String, CityEdge> connectingEdges;

  /**
   * This is a constructor for a cityNode. A cityNode right now takes in
   * a name, a latitude and a longitude.
   * @param name  Name of city that will be stored in graph
   * @param lat  Latitude of city
   * @param lon  Longitude of city
   */
  public CityNode(String name, double lat, double lon){
    this.name = name;
    this.lat = lat;
    this.longit = lon;
  }
  /**
   * Returns the name of the cityNode.
   * @return The name of the City
   */
  public String getName(){
    return name;
  }
  /**
   * Returns the latitude of the city.
   * @return The lat of the city
   */
  public double getLat(){
    return lat;
  }
  /**
   * Returns the longitude of the city.
   * @return The longitude of the city
   */
  public double getLong(){
    return longit;
  }
  /**
   * This is a comparator method that takes in a GraphNode and tells us if the two cities are equal.
   * If the GraphNode is not a cityNode we return false due to comparison errors.
   * @return Boolean representing if nodes are the same node
   */
  public boolean equals(GraphNode node){
    if(node instanceof CityNode){
      return name.equals(((CityNode) node).getName()) && (lat == ((CityNode) node).getLat()) && (longit == ((CityNode) node).getLong());
    } else{
      //TODO: Make this throw an error?
      return false;
    }
  }
  //TODO: Define if we can reinsert edges into nodes
  public void insertEdge(GraphNode node){
  }



}
