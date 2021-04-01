package edu.brown.cs.mramesh4.TripGraph;

import java.util.List;

public interface TripGraphEdge<N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>>{
  void setWeight(double weight);
  double getWeight();
  List<N> getNodes();
  boolean equals(E edge);
}
