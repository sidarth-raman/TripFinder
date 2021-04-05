package edu.brown.cs.mramesh4.TripGraph;

import java.util.Comparator;

/**
 * This is a comparator to compare cityEdge.
 * @param <N> node to use
 * @param <E> type of edge to use
 */
public class TripGraphEdgeComparator<N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>>
    implements Comparator<E> {

  @Override
  public int compare(E a, E b) {
    //compare total weight (distance to node + distance to goal)
    return Double.compare(a.getWeight(), b.getWeight());
  }
}
