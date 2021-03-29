
package edu.brown.cs.mramesh4.GraphTest;

import edu.brown.cs.mramesh4.TripGraph.CityNode;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

/**
 * This is a testing class for our CityNodes to ensure
 * they meet GraphNode capabilities as well as CityNode capabilities
 */
public class NodeTest{
  CityNode node;
  CityNode node2;
  @Before
  public void setUp() {
     node = new CityNode("New York", 40.7128, -74.0060);
     node2 = new CityNode("New Jersey", 40.0583, 74.4057);
  }
  @After
  public void tearDown(){
    node = null;
    node2 = null;
  }

  @Test
  public void testDistanceBetween(){

  }

  @Test
  public void testEdges(){

  }

  @Test
  public void testEquals(){

  }

  @Test
  public void testAccessors(){

  }

  @Test
  public void testActivities(){

  }
  //methods to test:
    //GraphNode distancebetween
    //getEdges
    //insertEdge
    //equals
    //getName()
    //getActivities

}