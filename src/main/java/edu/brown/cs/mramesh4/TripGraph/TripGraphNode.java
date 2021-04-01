package edu.brown.cs.mramesh4.TripGraph;

import java.util.List;

public interface TripGraphNode<N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>> {
  double distanceBetween(N node);
  void insertEdges(N node);
  boolean equals(N node);
  String getName();
  List<N> getNeighbors();
  List<E> getOutgoingEdges();
}
