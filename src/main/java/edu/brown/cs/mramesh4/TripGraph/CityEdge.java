package edu.brown.cs.mramesh4.TripGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class for a CityEdge, which connects CityNodes. These
 * edges are intended to be undirected, but for conventional purpose,
 * we named the nodes start and end.
 */
public class CityEdge implements TripGraphEdge<CityNode, CityEdge> {
  private double weight;
  private CityNode start;
  private CityNode end;
  private String name;
  /**
   * This is a constructor to create a CityEdge, an edge between
   * two nodes in a graph.
   * @param start first node of edge
   * @param end second node of edge
   * @param name A name of the edge
   */
  public CityEdge(CityNode start, CityNode end, String name) {
    weight = start.distanceBetween(end);
    this.start = start;
    this.end = end;
    this.name = name;
  }

  /**
   * Constructor to create an edge with a specific weight.
   * @param start first node of edge
   * @param end second node of edge
   * @param name the name of the edge
   * @param weight weight of edge
   */
  public CityEdge(CityNode start, CityNode end, String name, double weight) {
    this.weight = weight;
    this.start = start;
    this.end = end;
    this.name = name;
  }

  /**
   * Method to get the nodes associated with the edge. The first
   * node will be "start" and the 2nd "end", but the edge should
   * be undirected
   * @return a list of nodes.
   */
  @Override
  public List<CityNode> getNodes() {
    List<CityNode> ret = new ArrayList<>();
    ret.add(start);
    ret.add(end);
    return ret;
  }

  /**
   * Get one end of the edge (the declared edge).
   * @return end
   */
  public CityNode getEnd() {
    return end;
  }

  /**
   * Get one end of the edge (the declared start).
   * @return start
   */
  public CityNode getStart() {
    return start;
  }

  /**
   * Weight setter method.
   * @param weight double to set wieght of edge
   */
  @Override
  public void setWeight(double weight) {
    this.weight = weight;
  }

  /**
   * Weight mutator function.
   * @return double representing weight
   */
  @Override
  public double getWeight() {
    return weight;
  }

  /**
   * Tells us whether two CityEdges are equal.
   * @param e edge to check
   * @return boolean if they are equal
   */
  public boolean equals(CityEdge e) {
    return (this.weight == e.getWeight()) && ((this.start.equals(e.start)
        && this.end.equals(e.end)) || this.end.equals(e.start) && this.start.equals(e.end));
  }

  /**
   * Getter method for name.
   * @return a name
   */
  public String getName() {
    return name;
  }
}
