package edu.brown.cs.mramesh4.GraphTest;

import edu.brown.cs.mramesh4.TripGraph.CityEdge;
import edu.brown.cs.mramesh4.TripGraph.CityNode;
import edu.brown.cs.mramesh4.TripGraph.CompleteTripGraph;
import edu.brown.cs.mramesh4.TripGraph.TripGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CompleteGraphTest {
  CityNode node;
  CityNode node2;
  CityNode node3;
  CityNode node4;
  CityNode node5;
  CityNode node6;
  private static final double DELTA = 0.01;
  CompleteTripGraph<CityNode, CityEdge> tripGraph;

  @Before
  public void setUp() {
    node = new CityNode("New York", 40.4, -73.56);
    node2 = new CityNode("Jersey City", 40.34, -74.04);
    node3 = new CityNode("Trenton", 40.13, -74.46);
    node4 = new CityNode("Philadelphia", 39.57, -75.10);
    node5 = new CityNode("Pittsburgh", 40.27, -80);
    node6 = new CityNode("Harrison", 40.35, -79.6501);
    List<CityNode> graphList = new ArrayList<>();
    graphList.add(node);
    graphList.add(node2);
    graphList.add(node3);
    graphList.add(node4);
    graphList.add(node5);
    graphList.add(node6);
    tripGraph = new CompleteTripGraph<>(graphList);
  }

  @Before
  public void setUp2(){
    node = new CityNode("New York", 40.4, -73.56);
    node2 = new CityNode("Jersey City", 40.34, -74.04);
    node3 = new CityNode("Trenton", 40.13, -74.46);
    node4 = new CityNode("Philadelphia", 39.57, -75.10);
    node5 = new CityNode("Pittsburgh", 40.27, -80);
    node6 = new CityNode("Harrison", 40.35, -79.6501);
    node.insertEdges(node2);
    node.insertEdges(node3);
    node.insertEdges(node4);
    node.insertEdges(node6);
    node2.insertEdges(node3);
    node2.insertEdges(node5);
    node3.insertEdges(node4);
    node3.insertEdges(node5);
    node4.insertEdges(node5);
    node6.insertEdges(node5);
    List<CityNode> graphList = new ArrayList<>();
    graphList.add(node);
    graphList.add(node2);
    graphList.add(node3);
    graphList.add(node4);
    graphList.add(node5);
    graphList.add(node6);
    TripGraph<CityNode, CityEdge> tGraph = new TripGraph<>(graphList);
    tripGraph = new CompleteTripGraph<>(tGraph);
  }

  @After
  public void tearDown(){
    node = null;
    node2 = null;
    node3 = null;
    node4 = null;
    node5 = null;
    node6 = null;
    tripGraph = null;
  }

  @Test
  public void testComplete(){
    setUp();
    Collection<CityNode> nodes = tripGraph.getGraph().values();
    assertEquals(6, nodes.size(), DELTA);
    for(CityNode node: nodes){
      assertEquals(5, node.getOutgoingEdges().size(), DELTA);
    }
    tearDown();
  }

  @Test
  public void testAStarAlgo(){
    setUp();
    List<CityNode> c = tripGraph.aStar("New York", "Pittsburgh");
    assertNotNull(c);
    for(int i = 0; i < c.size(); i++){
      System.out.println("c name" + c.get(i).getName());
    }
    tearDown();
  }

  @Test
  public void testInsert(){
    setUp();
    node6 = new CityNode("Houston", 29.7604, -95.3689);
    tripGraph.insertNode(node6);
    Collection<CityNode> nodes = tripGraph.getGraph().values();
    assertEquals(nodes.size(), 7, DELTA);
    for(CityNode node: nodes){
      assertEquals(6, node.getOutgoingEdges().size(), DELTA);
    }
    tearDown();
  }

  @Test
  public void testDelete(){
    setUp();
    tripGraph.deleteNode(node5);
    Collection<CityNode> nodes = tripGraph.getGraph().values();
    assertEquals(nodes.size(), 5, DELTA);
    for(CityNode node: nodes){
      assertEquals(4, node.getOutgoingEdges().size(), DELTA);
    }
    tearDown();
  }

  @Test
  public void testConstructor(){
    setUp2();
    Collection<CityNode> nodes = tripGraph.getGraph().values();
    assertEquals(6, nodes.size(), DELTA);
    for(CityNode node: nodes){
      assertEquals(5, node.getOutgoingEdges().size(), DELTA);
    }
    tearDown();
  }

  @Test
  public void testKruskalSimple(){
    //we will generate a kruskal's test here with a simple rectangle
    CityNode a = new CityNode("A", 40, 72);
    CityNode b = new CityNode("B", 40, 81);
    CityNode c = new CityNode("C", 56, 72);
    CityNode d = new CityNode("D", 56, 81);
    List<CityNode> nodes = new ArrayList<>();
    nodes.add(a);
    nodes.add(b);
    nodes.add(c);
    nodes.add(d);
    CompleteTripGraph<CityNode, CityEdge> cg = new CompleteTripGraph<>(nodes);

    TripGraph<CityNode, CityEdge> ret = cg.Kruskals();
    assertNotNull(ret);
    for(CityNode node: ret.getGraph().values()){
      for(CityEdge e: node.getConnectingEdges().values()) {
        System.out.println("Outgoing edges are for mst" + e.getName());
      }
    }

    List<CityNode> dfsRet = cg.dfsTree(ret, a);
    //assertNotNull(dfsRet);
    for(CityNode c2: dfsRet){
      System.out.println("citynodename" + c2.getName());
    }
  }
}
