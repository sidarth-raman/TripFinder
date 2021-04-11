
package edu.brown.cs.mramesh4.GraphTest;

import edu.brown.cs.mramesh4.TripGraph.CityNode;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

/**
 * This is a testing class for our CityNodes to ensure
 * they meet GraphNode capabilities as well as CityNode capabilities
 */
public class NodeTest{
  CityNode node;
  CityNode node2;
  private static final double DELTA = 0.01;
  @Before
  public void setUp() {
     node = new CityNode("New York", 40.7128, -74.0060, 10);
     node2 = new CityNode("New Jersey", 40.0583, -74.4057, 10);
  }
  @After
  public void tearDown(){
    node = null;
    node2 = null;
  }

  @Test
  public void testDistanceBetween(){
    setUp();
    //assert that the calculation is correct
    assertEquals( 0.766897, node.distanceBetween(node2),DELTA);
    //assert that they are the same reversed
    assertEquals(node2.distanceBetween(node), node.distanceBetween(node2), DELTA);
    tearDown();
  }

  @Test
  public void testEdges(){
    setUp();
    //insertEdge
    node.insertEdges(node2);
    //make sure they inserted an edge
    assertEquals(node.getConnectingEdges().size(), 1, DELTA);
    assertEquals(node2.getConnectingEdges().size(), 1, DELTA);
    //make sure it puts that opposite node
    assertTrue(node.getConnectingNodes().get("New Jersey").equals(node2));
    assertTrue(node2.getConnectingNodes().get("New York").equals(node));
    //make sure the edge weight is identical
    assertEquals(node.getConnectingEdges().get("New Jersey").getWeight(), 0.766897, DELTA);
    assertEquals(node2.getConnectingEdges().get("New York").getWeight(), 0.766897, DELTA);
    //make sure the two edges are equal
    assertTrue(node.getConnectingEdges().get("New Jersey").equals((node2.getConnectingEdges().get("New York"))));
    tearDown();
  }

  @Test
  public void testEquals(){
    setUp();
    //make sure this doesn't work
    assertFalse(node.equals(node2));
    CityNode node3 = new CityNode("New York", 40.7128, -74.0060, 10);
    CityNode node4 = new CityNode("New York", 40.7128, -74.0060, 10);
    //test this does work
    assertTrue(node.equals(node3));
    //test symmetry
    assertTrue(node3.equals(node));
    //test reflexivity
    assertTrue(node.equals(node));
    //test transitive
    assertTrue(node.equals(node4));
    assertTrue(node4.equals(node3));
    assertTrue(node.equals(node3));
    tearDown();
  }

  @Test
  public void testAccessors(){
    setUp();
    assertEquals(node.getName(), "New York");
    assertEquals(node2.getName(), "New Jersey");
    assertEquals(node.getLong(),-74.0060, DELTA);
    assertEquals(node.getLat(),40.7128, DELTA);
    tearDown();
  }

  @Test
  public void testOutgoing(){
    setUp();
    node.insertEdges(node2);
    //makes sure there is one edge
    assertEquals(node.getOutgoingEdges().size(), 1, DELTA);
    assertEquals(node2.getOutgoingEdges().size(), 1, DELTA);

    //make sure it recognizes the opposite neighbors
    assertTrue(node.getNeighbors().get(0).equals(node2));
    assertTrue(node2.getNeighbors().get(0).equals(node));

    //make sure the edge weight is identical
    assertEquals(node.getOutgoingEdges().get(0).getWeight(), 0.766897, DELTA);
    assertEquals(node2.getOutgoingEdges().get(0).getWeight(), 0.766897, DELTA);
    //make sure the two edges are equal
    assertTrue(node.getOutgoingEdges().get(0).equals(node2.getOutgoingEdges().get(0)));

    tearDown();
  }

  @Test
  public void testActivities(){
    //TODO: Insert activities tests
  }

}