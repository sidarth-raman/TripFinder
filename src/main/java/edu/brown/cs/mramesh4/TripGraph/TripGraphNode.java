package edu.brown.cs.mramesh4.TripGraph;

import java.util.HashMap;
import java.util.List;

public interface TripGraphNode<N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>> {
  double distanceBetween(N node);
  void insertEdges(N node);
  boolean equals(N node);
  String getName();
  //this is needed for neighbors/edges
  List<N> getNeighbors();
  List<E> getOutgoingEdges();
  //this is needed for aStar**
  void setWeight(double weight);
  double getWeight();
  void setDistance(double dist);
  double getDistance();
  double toGoal(N node);
  //used to store undirected info
  HashMap<String, N> getConnectingNodes();
  HashMap<String, E> getConnectingEdges();
  void deleteEdge(N node);
  void clearGraphEdges();
}
