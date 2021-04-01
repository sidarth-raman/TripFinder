package edu.brown.cs.mramesh4.TripGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TripGraphNode<N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>> {
  double distanceBetween(N node);
  void insertEdges(N node);
  boolean equals(N node);
  String getName();
  List<N> getNeighbors();
  List<E> getOutgoingEdges();
  void setWeight(double weight);
  double getWeight();
  void setDistance(double dist);
  double getDistance();
  HashMap<String, N> getConnectingNodes();
  HashMap<String, E> getConnectingEdges();
  double toGoal(N node);
  void deleteEdge(N node);
}
