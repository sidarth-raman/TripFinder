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
    connectingNodes = new HashMap<>();
    connectingEdges = new HashMap<>();
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
   * Accessor method for the nodes map
   * @return map of nodes
   */
  public HashMap<String, CityNode> getConnectingNodes(){
    return connectingNodes;
  }

  /**
   * Accessor method for edges between nodes (undirected)
   * @return
   */
  public HashMap<String, CityEdge> getConnectingEdges(){
    return connectingEdges;
  }
  /**
   * This is a comparator method that takes in a GraphNode and tells us if the two cities are equal.
   * If the GraphNode is not a cityNode we return false due to comparison errors.
   * @return Boolean representing if nodes are the same node
   */
  @Override
  public boolean equals(CityNode node){
    return name.equals(node.getName()) && (lat == node.getLat()) && (longit ==  node.getLong());
  }

  /**
   * This is a method that insertsEdges between the node. Since
   * we have an undirected graph it will reinsert.
   * @param node node to insert edges between
   */
  @Override
  public void insertEdges(CityNode node){
    if(node != null) {
      String name = node.getName();
      System.out.println("name" + name);
      CityEdge edge = new CityEdge(this, node);
      CityEdge edge2 = new CityEdge(node, this);
      System.out.println("edge" + edge + "edge2" + edge2);
      connectingNodes.put(name, node);
      connectingEdges.put(name, edge);
      node.getConnectingNodes().put(this.name, this);
      node.getConnectingEdges().put(this.name, edge2);
    } else{
      System.out.println("node is null");
    }
  }
  //TODO: Change this to an A* or more significant path-finding version
  /**
   * Returns the euclidean distance between two city nodes
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
  public List<CityEdge> getOutgoingEdges(){
    Collection<CityEdge> c  = this.getConnectingEdges().values();
    List<CityEdge> ret = new ArrayList<>();
    for(CityEdge edge: c){
      ret.add(edge);
    }
    return ret;
  }

  /**
   * Returns a list of neighbors.
   * @return nodes of neighbors.
   */
  @Override
  public List<CityNode> getNeighbors(){
    Collection<CityNode> c  = this.getConnectingNodes().values();
    List<CityNode> ret = new ArrayList<>();
    for(CityNode node: c){
      ret.add(node);
    }
    return ret;
  }
  //TODO: Code a getActivities() method

}
