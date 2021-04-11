package edu.brown.cs.mramesh4.GraphTest;

import edu.brown.cs.mramesh4.TripGraph.CityNode;
import edu.brown.cs.mramesh4.TripGraph.GraphBuilder;
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
    citiesToVisit.add("Los Angeles, CA");
    citiesToVisit.add("New York, NY");
    GraphBuilder g = new GraphBuilder(conn, "Chicago, IL", 1000, 5, citiesToVisit);
    assertEquals(g.getOrigin(), "Chicago, IL");
    tearDown();
  }

  @Test
  public void testVisitingSelection(){
    setUp();
    citiesToVisit.add("Sacramento, CA");
    citiesToVisit.add("Detroit, MI");
    GraphBuilder g = new GraphBuilder(conn, "Denver, CO", 1000, 3, citiesToVisit);
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
    citiesToVisit.add("San Diego, CA");
    citiesToVisit.add("Atlanta, GA");
    GraphBuilder g = new GraphBuilder(conn, "New York, NY", 1000, 3, citiesToVisit);
    assertEquals(g.getCitiesOfGraph().size(), 3);
    tearDown();
  }

  @Test
  public void tooManyToVisit(){
    setUp();
    citiesToVisit.add("Pittsburgh, PA");
    citiesToVisit.add("Chicago, IL");
    citiesToVisit.add("Des Moines, IA");
    citiesToVisit.add("Houston, TX");
    GraphBuilder g = new GraphBuilder(conn, "Boston, MA", 1000, 3, citiesToVisit);
    assertEquals(g.getCitiesOfGraph().size(), 5);
    tearDown();
  }

  @Test
  public void maxDist(){
    setUp();
    citiesToVisit.add("Madison, WI");
    citiesToVisit.add("Chicago, IL");
    GraphBuilder g = new GraphBuilder(conn,"New York, NY", 10, 3, citiesToVisit);
    for(CityNode n : g.getCitiesOfGraph()){
      System.out.println("node of graph: " + n.getName());
    }
    tearDown();
  }


}
