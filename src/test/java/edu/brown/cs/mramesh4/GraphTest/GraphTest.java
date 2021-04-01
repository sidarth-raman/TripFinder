package edu.brown.cs.mramesh4.GraphTest;

import edu.brown.cs.mramesh4.Graph.Graph;
import edu.brown.cs.mramesh4.TripGraph.CityEdge;
import edu.brown.cs.mramesh4.TripGraph.CityNode;
import edu.brown.cs.mramesh4.TripGraph.TripGraph;
import org.junit.Before;

public class GraphTest {
  CityNode node;
  CityNode node2;
  private static final double DELTA = 0.01;
  CityEdge edge1;
  CityEdge edge2;
  TripGraph<CityNode, CityEdge> tripGraph = new TripGraph<>();
  @Before
  public void setUp() {
    node = new CityNode("New York", 40.7128, -74.0060);
    node2 = new CityNode("New Jersey", 40.0583, -74.4057);
  }
}
