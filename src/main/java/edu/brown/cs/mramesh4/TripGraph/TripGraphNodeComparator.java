package edu.brown.cs.mramesh4.TripGraph;
import java.util.Comparator;

/**
 * This is a comparator to compare two graph nodes in a priorityqueue.
 * @param <N> the graphnode object
 * @param <E> the graphedge object
 */
public class TripGraphNodeComparator<N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>>
    implements Comparator<N> {
  @Override
  public int compare(N a, N b) {
    //compare total weight (distance to node + distance to goal)
    return Double.compare(a.getWeight(), b.getWeight());
  }
}
