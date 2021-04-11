package edu.brown.cs.mramesh4.GraphTest;

import edu.brown.cs.mramesh4.Graph.Graph;
import edu.brown.cs.mramesh4.TripGraph.CityEdge;
import edu.brown.cs.mramesh4.TripGraph.CityNode;
import edu.brown.cs.mramesh4.TripGraph.GraphBuilder;
import edu.brown.cs.mramesh4.TripGraph.TripGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GraphBuilderTest {
  
  private List<String> citiesToVisit;
  private Connection conn;


  @Before
  public void setUp() {
    citiesToVisit = new ArrayList<>();
    Connection c = null;
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.out.println("ERROR: connection failed");
    }
    String url = "jdbc:sqlite:data.sqlite";
    try {
      c = DriverManager.getConnection(url);
    } catch (SQLException e) {
      System.out.println("ERROR: connection failed");
    }
    conn = c;
  }

  @After
  public void tearDown(){
    citiesToVisit.clear();
    conn = null;
  }

  @Test
  public void testOriginSelector(){
    setUp();
    citiesToVisit.add("Los Angeles");
    citiesToVisit.add("New York");
    GraphBuilder g = new GraphBuilder(conn, "Chicago", 1000, 5, citiesToVisit);
    assertEquals(g.getOrigin(), "Chicago, IL");
    tearDown();
  }

  @Test
  public void testVisitingSelection(){
    setUp();
    citiesToVisit.add("Sacramento");
    citiesToVisit.add("Detroit");
    GraphBuilder g = new GraphBuilder(conn, "Denver", 1000, 3, citiesToVisit);
    assertEquals(g.getCitiesOfGraph().size(), 3);
    for(CityNode n : g.getCitiesOfGraph()){
      System.out.println(n.getName());
      assertTrue(n.getName().equals("Sacramento, CA") || n.getName().equals("Detroit, MI") || n.getName().equals("Denver, CO"));
    }
    tearDown();
  }

  @Test
  public void testNoSuggestions(){
    setUp();
    citiesToVisit.add("San Diego");
    citiesToVisit.add("Atlanta");
    GraphBuilder g = new GraphBuilder(conn, "New York", 1000, 3, citiesToVisit);
    assertEquals(g.getCitiesOfGraph().size(), 3);
    tearDown();
  }

  @Test
  public void tooManyToVisit(){
    setUp();
    citiesToVisit.add("Pittsburgh");
    citiesToVisit.add("Chicago");
    citiesToVisit.add("Des Moines");
    citiesToVisit.add("Houston");
    GraphBuilder g = new GraphBuilder(conn, "Boston", 1000, 3, citiesToVisit);
    assertEquals(g.getCitiesOfGraph().size(), 5);
    tearDown();
  }

  @Test
  public void maxDist(){
    setUp();
    citiesToVisit.add("Madison");
    citiesToVisit.add("Chicago");
    GraphBuilder g = new GraphBuilder(conn,"New York", 10, 3, citiesToVisit);
    for(CityNode n : g.getCitiesOfGraph()){
      System.out.println("node of graph: " + n.getName());
    }
    tearDown();
  }


}
