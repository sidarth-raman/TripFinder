package edu.brown.cs.mramesh4.GraphTest;

import edu.brown.cs.mramesh4.TripGraph.CityEdge;
import edu.brown.cs.mramesh4.TripGraph.CityNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EdgeTest {
  CityNode node;
  CityNode node2;
  private static final double DELTA = 0.01;
  CityEdge edge1;
  CityEdge edge2;
  @Before
  public void setUp() {
    node = new CityNode("New York", 40.7128, -74.0060, 10);
    node2 = new CityNode("New Jersey", 40.0583, -74.4057, 10);
    edge1 = new CityEdge(node, node2, "NY-NJ");
    edge2 = new CityEdge(node2, node, "NY-NJ");
  }
  @After
  public void tearDown(){
    node = null;
    node2 = null;
  }

  @Test
  public void testEquals(){
    setUp();
    CityEdge edge3 = new CityEdge(node, node2, "NY-NJ", node.distanceBetween(node2));
    //tests that they are equal
    assertTrue(edge1.equals(edge2));
    assertTrue(edge2.equals(edge1));

    //test that the constructor doesn't affect
    assertTrue(edge1.equals(edge3));
    assertTrue(edge2.equals(edge3));

    //assert reflexive
    assertTrue(edge1.equals(edge1));
    assertTrue(edge2.equals(edge2));

    //makes sure that it fails with different nodes
    CityEdge edge4 = new CityEdge(node, node, "NEW NODE", 0);
    assertFalse(edge1.equals(edge4));
    CityNode node4 = new CityNode("New Thirty", 40.3, -74.4043, 10);
    CityEdge edge5 = new CityEdge(node4, node2, "NEXT NODE");
    assertFalse(edge1.equals(edge5));
    assertFalse(edge4.equals(edge5));
    assertTrue(edge5.equals(edge5));
    tearDown();
  }

  @Test
  public void testAccessors(){
    setUp();
    //test the accessors
    assertTrue(edge1.getEnd().equals(node2));
    assertTrue(edge1.getStart().equals(node));
    assertTrue(edge1.getNodes().get(0).equals(node));
    assertTrue(edge1.getNodes().get(1).equals(node2));
    assertEquals(0.766897, edge1.getWeight(), DELTA);
    edge1.setWeight(5);
    assertEquals(edge1.getWeight(), 5, DELTA);
    tearDown();
  }


}
