package edu.brown.cs.mramesh4.TripGraph;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class TripGraphNodeComparator<N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>>
  implements Comparator<N> {
  @Override
  public int compare(N a, N b) {
    //compare total weight (distance to node + distance to goal)
    return Double.compare(a.getWeight(), b.getWeight());
  }
}
