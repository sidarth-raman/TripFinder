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
  CityNode node7;
  CityNode node8;
  CityNode node9;
  CityNode node10;
  CityNode node11;
  CityNode node12;
  CityNode node13;
  CityNode node14;
  CityNode node15;
  List<CityNode> graphList;

  private static final double DELTA = 0.01;
  CompleteTripGraph<CityNode, CityEdge> tripGraph;

  @Before
  public void setUp() {
    node = new CityNode("New York", 40.4, -73.56, 100000);
    node2 = new CityNode("Jersey City", 40.34, -74.04, 100000);
    node3 = new CityNode("Trenton", 40.13, -74.46, 100000);
    node4 = new CityNode("Philadelphia", 39.57, -75.10, 100000);
    node5 = new CityNode("Pittsburgh", 40.27, -80, 100000);
    node6 = new CityNode("Harrison", 40.35, -79.6501, 100000);
    graphList = new ArrayList<>();
    graphList.add(node);
    graphList.add(node2);
    graphList.add(node3);
    graphList.add(node4);
    graphList.add(node5);
    graphList.add(node6);
    tripGraph = new CompleteTripGraph<>(graphList);
  }

  @Before
  public void setUp3() {
    node = new CityNode("New York", 40.4, -73.56, 100000);
    node2 = new CityNode("Jersey City", 40.34, -74.04, 100000);
    node3 = new CityNode("Trenton", 40.13, -74.46, 100000);
    node4 = new CityNode("Philadelphia", 39.57, -75.10, 100000);
    node5 = new CityNode("Pittsburgh", 40.27, -80, 100000);
     graphList = new ArrayList<>();
    graphList.add(node);
    graphList.add(node2);
    graphList.add(node3);
    graphList.add(node4);
    graphList.add(node5);
    tripGraph = new CompleteTripGraph<>(graphList);
  }

  @Before
  public void setUp2() {
    node = new CityNode("New York", 40.4, -73.56, 100000);
    node2 = new CityNode("Jersey City", 40.34, -74.04, 100000);
    node3 = new CityNode("Trenton", 40.13, -74.46, 100000);
    node4 = new CityNode("Philadelphia", 39.57, -75.10, 100000);
    node5 = new CityNode("Pittsburgh", 40.27, -80, 100000);
    node6 = new CityNode("Harrison", 40.35, -79.6501, 100000);
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
    graphList = new ArrayList<>();
    graphList.add(node);
    graphList.add(node2);
    graphList.add(node3);
    graphList.add(node4);
    graphList.add(node5);
    graphList.add(node6);
    TripGraph<CityNode, CityEdge> tGraph = new TripGraph<>(graphList);
    tripGraph = new CompleteTripGraph<>(tGraph);
  }

  @Test
  public void setUp4(){
    node = new CityNode("City A", -0.0000000400893815, 0.0000000358808126, 10);
    node2 = new CityNode("City B", -21.4983260706612533, -7.3194159498090388, 10);
    node3 = new CityNode("City C", -28.8732862244731230 ,  -0.0000008724121069, 10);
    node4 = new CityNode("City D", -43.0700258454450875 , -14.5548396888330487, 10);
    node5 = new CityNode("City E", -50.4808382862985496, -7.3744722432402208, 10);
    node6 = new CityNode("City F", -64.7472605264735108,  -21.8981713360336698, 10);
    node7 = new CityNode("City G", -72.0785319657452987,  -0.1815834632498404, 10);
    node8 = new CityNode("City H", -79.2915791686897506 ,  21.4033307581457670, 10);
    node9 = new CityNode("City I", -65.0865638413727368,   36.0624693073746769, 10);
    node10 = new CityNode("City J", -57.5687244704708050 , 43.2505562436354225, 10);
    node11 = new CityNode("City K", -50.5859026832315024, 21.5881966132975371, 10);
    node12 = new CityNode("City L", -36.0366489745023770, 21.6135482886620949, 10);
    node13 = new CityNode("City M", -29.0584693142401171 , 43.2167287683090606, 10);
    node14 = new CityNode("City N", -14.6577381710829471 ,  43.3895496964974043, 10);
    node15 = new CityNode("City O", -0.1358203773809326,  28.7292896751977480, 10);
    graphList = new ArrayList<>();
    graphList.add(node);
    graphList.add(node2);
    graphList.add(node3);
    graphList.add(node4);
    graphList.add(node5);
    graphList.add(node6);
    graphList.add(node7);
    graphList.add(node8);
    graphList.add(node9);
    graphList.add(node10);
    graphList.add(node11);
    graphList.add(node12);
    graphList.add(node13);
    graphList.add(node14);
    graphList.add(node15);
    tripGraph = new CompleteTripGraph<>(graphList);
  }

  @After
  public void tearDown() {
    node = null;
    node2 = null;
    node3 = null;
    node4 = null;
    node5 = null;
    node6 = null;
    tripGraph = null;
  }

  @Test
  public void testComplete() {
    setUp();
    Collection<CityNode> nodes = tripGraph.getGraph().values();
    assertEquals(6, nodes.size(), DELTA);
    for (CityNode node : nodes) {
      assertEquals(5, node.getOutgoingEdges().size(), DELTA);
    }
    tearDown();
  }

  @Test
  public void testAStarAlgo() {
    setUp();
    List<CityNode> c = tripGraph.aStar("New York", "Pittsburgh");
    assertNotNull(c);
    for (int i = 0; i < c.size(); i++) {
      System.out.println("c name" + c.get(i).getName());
    }
    tearDown();
  }

  @Test
  public void testInsert() {
    setUp();
    node6 = new CityNode("Houston", 29.7604, -95.3689, 10);
    tripGraph.insertNode(node6);
    Collection<CityNode> nodes = tripGraph.getGraph().values();
    assertEquals(nodes.size(), 7, DELTA);
    for (CityNode node : nodes) {
      assertEquals(6, node.getOutgoingEdges().size(), DELTA);
    }
    tearDown();
  }

  @Test
  public void testDelete() {
    setUp();
    tripGraph.deleteNode(node5);
    Collection<CityNode> nodes = tripGraph.getGraph().values();
    assertEquals(nodes.size(), 5, DELTA);
    for (CityNode node : nodes) {
      assertEquals(4, node.getOutgoingEdges().size(), DELTA);
    }
    tearDown();
  }

  @Test
  public void testConstructor() {
    setUp2();
    Collection<CityNode> nodes = tripGraph.getGraph().values();
    assertEquals(6, nodes.size(), DELTA);
    for (CityNode node : nodes) {
      assertEquals(5, node.getOutgoingEdges().size(), DELTA);
    }
    tearDown();
  }

  @Test
  public void testKruskalSimple() {
    //we will generate a kruskal's test here with a simple rectangle
    CityNode a = new CityNode("A", 40, 72, 10);
    CityNode b = new CityNode("B", 40, 81, 10);
    CityNode c = new CityNode("C", 56, 72, 10);
    CityNode d = new CityNode("D", 56, 81, 10);
    List<CityNode> nodes = new ArrayList<>();
    nodes.add(a);
    nodes.add(b);
    nodes.add(c);
    nodes.add(d);
    CompleteTripGraph<CityNode, CityEdge> cg = new CompleteTripGraph<>(nodes);
    TripGraph<CityNode, CityEdge> ret = cg.Kruskals();
    assertNotNull(ret);
    for (CityNode node : ret.getGraph().values()) {
      for (CityEdge e : node.getConnectingEdges().values()) {
        System.out.println("Outgoing edges are for mst" + e.getName());
      }
    }

    List<CityNode> dfsRet = cg.dfsTree(ret, a);
    //assertNotNull(dfsRet);
    for (CityNode c2 : dfsRet) {
      System.out.println("citynodename" + c2.getName());
    }
  }

  @Test
  public void testKruskalsSame() {
    setUp3();
    TripGraph<CityNode, CityEdge> ret = tripGraph.Kruskals();
    assertNotNull(ret);
    for (CityNode node : ret.getGraph().values()) {
      for (CityEdge e : node.getConnectingEdges().values()) {
        System.out.println("Outgoing edges are for mst" + e.getName());
      }
    }
  }

  @Test
  public void testDFS(){
    setUp4();
    TripGraph<CityNode, CityEdge> ret = tripGraph.Kruskals();
    assertNotNull(ret);

    List<CityNode> returned = tripGraph.TwoOptTSP(node);
    for(CityNode node: returned){
      System.out.println("node" + node.getName());
    }
  }

  @Test
  public void testChrist(){
    setUp4();
    List<CityNode> ret = tripGraph.christTSP(node);
    for(CityNode city: ret){
      System.out.println("testChrist: " + city.getName());
    }
  }

  public void testActivities(){
    setUp3();
    for(CityNode node: graphList){
      node.setActivities();
      for(String printLine: node.getActivities()){
        System.out.println(printLine);
      }
    }

  }
}