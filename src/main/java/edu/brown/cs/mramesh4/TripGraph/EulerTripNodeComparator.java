package edu.brown.cs.mramesh4.TripGraph;

import java.util.Comparator;

public class EulerTripNodeComparator <N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>>
  implements Comparator<N> {
  @Override
  public int compare(N a, N b) {
    //compare total weight (distance to node + distance to goal)
    return Integer.compare(a.getNeighbors().size() % 2, b.getNeighbors().size() % 2 );
  }
}