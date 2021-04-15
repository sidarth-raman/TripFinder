package edu.brown.cs.mramesh4.TripGraph;

import java.util.Comparator;

/**
 * This is a comparator to sort by even and odd degree. s
 * @param <N> the graphnode object
 * @param <E> the graphedge object
 */
public class EulerTripNodeComparator<N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>>
    implements Comparator<N> {
  @Override
  public int compare(N a, N b) {
    //compare whether the two are even or odd-degree
    return Integer.compare(a.getNeighbors().size() % 2, b.getNeighbors().size() % 2);
  }
}
