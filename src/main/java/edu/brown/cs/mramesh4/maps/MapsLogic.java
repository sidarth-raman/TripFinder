package edu.brown.cs.mramesh4.maps;

import edu.brown.cs.mramesh4.Graph.Graph;
import edu.brown.cs.mramesh4.KDTree.KDTree;
import edu.brown.cs.mramesh4.stars.ActionMethod;
import edu.brown.cs.mramesh4.stars.IllegalArgumentException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * This class represents all of the logic we need for Maps 1 + 2. It handles all finding which
 * command to call, getting a connection to the database, and calling commands and the necessary
 * helper methods to complete them. It also handles parsing the output to print in the terminal.
 */
public class MapsLogic implements ActionMethod<String> {

  private List<WayNodes> wayNodeList;
  private KDTree<WayNodes> wayNodesTree;
  private Connection conn = null;
  private Graph<WayNodes, Way> graph = new Graph<>();
  private WayNodeCache wnc;
  private HashMap<String, WayNodes> wayNodeCache = new HashMap<>();
  private String database;

  /**
   * In the constructor of MapsLogic, we initailize our list instance variable which holds
   * all the nodes in the database.
   */
  public MapsLogic() {
    wayNodeList = new ArrayList<>();
  }

  /**
   * Takes in a command from the command line and executes it.
   * @param coms String from the command line
   */
  @Override
  public void run(String[] coms) {
    //assuming the length > 0
    if (coms.length > 0) {
      //run regardless of uppercase and trimmed space
      switch (coms[0]) {
        case "map":
          this.map(coms);
          break;
        case "ways":
          this.ways(coms);
          break;
        case "nearest":
          this.nearest(coms);
          break;
        case "route":
          this.route(coms);
          break;
        default:
          System.err.println("ERROR: You entered" + coms[0] + "Please enter a valid command");
          break;
      }
    }
  }

  /**
   * Loads the map database and builds our KDTree.
   * @param coms whitesace separated list of arguments.
   */
  public void map(String[] coms) {
    //checking that command has correct number of args
    if (coms.length != 2) {
      System.err.println("ERROR: Incorrect number or args provided for map command");
      return;
    }
    File file = new File(coms[1]);
    //check to see if database file exists
    if (!file.exists()) {
      System.err.println("ERROR: File does not exist");
      //make sure file is valid
      return;
    }
    try {
      Class.forName("org.sqlite.JDBC");
      String pathToDB = "jdbc:sqlite:" + coms[1];
      //create a connection
      conn = DriverManager.getConnection(pathToDB);
      String[] tables = new String[]{"node", "way"};
      String[] expectedCols = new String[]{"id", "latitude", "longitude"};
      //makes sure our table we are reading from exists
      if (isValidTable(tables)) {
        PreparedStatement prep;
        //get all of the nodes table

        prep = conn.prepareStatement("WITH trav as (SELECT start AS node_id "
                + "from way WHERE type != \"\" and type != \"unclassified\""
          + "UNION SELECT end AS node from way WHERE type != \"\" and type != \"unclassified\")"
          + "SELECT id, latitude, longitude FROM trav JOIN node on trav.node_id == node.id");
        ResultSet rs = prep.executeQuery();
        wayNodeList.clear();
        //makes sure our table is correctly columned
        if (!isValidQuery(rs, expectedCols)) {
          System.err.println("ERROR: Table is ill-formated");
          return;
        } else {
          while (rs.next()) {
            //create a Node object for each entry in table
            String id = rs.getString(1);
            double latitude = rs.getDouble(2);
            double longitude = rs.getDouble(3);

            //we want to make sure this is valid and that the table is well formatted. If
            //it isn't we throw an error.
            WayNodes wayNode = new WayNodes(id, latitude, longitude, conn);
            wayNodeList.add(wayNode);
          }
          rs.close();
          //create KDTree with the wayNode List we creaed
          wayNodesTree = new KDTree<WayNodes>(wayNodeList, 2);
          graph = new Graph<WayNodes, Way>();
          wnc = new WayNodeCache(this.conn);
          wayNodeCache = new HashMap<>();
          System.out.println("map set to " + coms[1]);
        }
      } else {
        System.err.println("ERROR: Table is ill-formated");
      }
    } catch (SQLException e) {
      System.err.println("ERROR: Error connecting to database");
      return;
    } catch (ClassNotFoundException e) {
      System.err.println("ERROR: Invalid database class");
      return;
    } catch (IllegalArgumentException e) {
      System.err.println("ERROR: Ill-formatted database");
      return;
    }
  }
  /**
   * Accessor method for wayNodeList.
   * @return list of waynodes
   */
  public List<WayNodes> getWayNodeList() {
    return this.wayNodeList;
  }

  /**
   * Tells us if a given table name exists in our connection.
   * @param tablename name of table to search
   * @return a boolean if table exists
   */
  public boolean isValidTable(String[] tablename) {
    try {
      for (int i = 0; i < tablename.length; i++) {
        boolean exist = conn.getMetaData().getTables(null,
                null, tablename[i], null).next();
        if (!exist) {
          return false;
        }
      }
      return true;
    } catch (SQLException e) {
      System.out.println("Table SQL Exception");
      return false;
    }
  }

  /**
   * Tells us if the given query of our table has the rows we expected.
   * @param rs: ResultSet Query
   * @param expectedCols: Columns we are expecting
   * @return a boolean representing if query is valid
   */
  private boolean isValidQuery(ResultSet rs, String[] expectedCols) {
    try {
      //get the metadata of the result set
      ResultSetMetaData rsmd = rs.getMetaData();
      //make sure cols match up to what we want
      for (int i = 1; i <= expectedCols.length; i++) {
        if (expectedCols[i - 1].equals(rsmd.getColumnName(i))) {
          continue;
        } else {
          return false;
        }
      }
      return true;
    } catch (SQLException e) {
      System.out.println("SQL Exception");
      return false;
    }
  }


  /**
   * This is the method to run the ways command, prints all ways within a given
   * area.
   * @param coms input to command split by white space in an array
   * @return a list of ways
   */
  public List<Way> ways(String[] coms) {
    if (wayNodesTree == null || wayNodeList == null) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return null;
    }
    //make sure we were passed in right arguments
    if (wayNodesTree.isEmpty() || wayNodeList.isEmpty()) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return null;
    } else if (coms.length != 5) {
      System.err.println("ERROR: Incorrect number of args provided for ways command");
      return null;
    } else {
      try {
        //make sure we were passed in doubles
        double latMin = Double.parseDouble(coms[3]);
        double lonMin = Double.parseDouble(coms[2]);
        double latMax = Double.parseDouble(coms[1]);
        double lonMax = Double.parseDouble(coms[4]);
        if (latMax < latMin || lonMax < lonMin) {
          System.err.println("ERROR: Please make sure the values for "
                  + "<lat2><long2> are less than <lat1><long1>");
          return null;
        }
        PreparedStatement prep;
        //query the DB for all the ways within the boundaries
        prep = conn.prepareStatement("WITH s_nodes AS (SELECT id AS start_id, latitude "
                + "AS start_lat, longitude AS start_long FROM node), "
                + "e_nodes AS (SELECT id AS end_id, latitude AS "
                + "end_lat, longitude AS end_long FROM node), "
                + "rel_nodes AS (SELECT id as node_id FROM node WHERE CAST(latitude AS DOUBLE) "
                + "BETWEEN ? AND ?  AND CAST(longitude AS DOUBLE) BETWEEN ? AND ?), "
                + "filtered_ways AS (SELECT * FROM way WHERE start IN rel_nodes "
                + "OR end IN rel_nodes) "
                + "SELECT * FROM filtered_ways AS fw JOIN s_nodes AS sn ON fw.start == sn.start_id "
                + "JOIN e_nodes AS en ON fw.end == en.end_id");
        prep.setDouble(1, latMin);
        prep.setDouble(2, latMax);
        prep.setDouble(3, lonMin);
        prep.setDouble(4, lonMax);
        ResultSet rs = prep.executeQuery();

        // Processing query results
        LinkedHashSet<String> toPrint = new LinkedHashSet<>();
        HashMap<String, Way> wayMap = new HashMap<>();

        while (rs.next()) {
          // way id and type
          String wayId = rs.getString(1);
          String type = rs.getString(3);

          // from node info
          String startId = rs.getString(4);
          double startLat = rs.getDouble(7);
          double startLong = rs.getDouble(8);

          // to node info
          String endId = rs.getString(5);
          double endLat = rs.getDouble(10);
          double endLong = rs.getDouble(11);

          // for printing out ways
          toPrint.add(wayId);

          // node initialization
          WayNodes from = new WayNodes(startId, startLat, startLong, this.conn);
          WayNodes to = new WayNodes(endId, endLat, endLong, this.conn);

          // to do: initialize way between start and end initialization
          Way way = new Way(wayId, from, to, type);
          wayMap.put(wayId, way);
        }
        rs.close();

        // printing way id
        // returning what's in ResultSet
        List<Way> ret = new ArrayList<>();
        for (String id: toPrint) {
          System.out.println(id);
          ret.add(wayMap.get(id));
        }
        return ret;

      } catch (NumberFormatException e) {
        //if the number is not an integer, throw an exception
        System.err.println("ERROR: Please supply a valid double");
        return null;
      } catch (SQLException e) {
        System.err.println("ERROR: Error parsing your input");
        return null;
      }
    }
  }


  /**
   * This is the implementation of the nearest method.
   * @param coms an array of whitespace split commands
   * @return the nearest Way Node
   */
  public WayNodes nearest(String[] coms) {
    if (wayNodesTree == null || wayNodeList == null) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return null;
    }
    if (wayNodesTree.isEmpty() || wayNodeList.isEmpty()) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return null;
    } else if (coms.length != 3) {
      System.err.println("ERROR: Incorrect number or args provided for ways command");
      return null;
    } else {
      try {
        double lat = Double.parseDouble(coms[1]);
        double longit = Double.parseDouble(coms[2]);
        //check helper method- creates new WayNode
        WayNodes target = this.getWayNodeByLatLong(lat, longit);
        //use our KDTree search method to find nearest neighbor
        List<WayNodes> ret = wayNodesTree.nearestNeighbors(1, target, false);
        WayNodes nearest = ret.get(0);
        wayNodeCache.put(nearest.getId(), nearest);
        System.out.println(nearest.getInfo("id"));
        return nearest;
      } catch (NumberFormatException e) {
        System.err.println("ERROR: Please supply a valid double");
        return null;
      }
    }
  }

  /**
   * This is the implementation of the route method.
   * @param coms an array of whitespace split commands
   */
  private void route(String[] coms) {
    //check if we haven't loaded data
    if (wayNodesTree == null || wayNodeList == null) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return;
    }
    if (wayNodesTree.isEmpty() || wayNodeList.isEmpty()) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return;
    }
    if (coms.length != 5) {
      System.err.println("ERROR: Incorrect number of args provided for route command");
      return;
    }
    WayNodes start;
    WayNodes end;
    graph = new Graph<>();
    //street names inputted
    if (!this.isClassInt(coms)) {
      for (int i = 1; i < coms.length; i++) {
        //checking that each argument is a nonempty string
        if (!(coms[i] instanceof String && coms[i] != "")) {
          System.err.println("ERROR: Street names must be nonempty strings");
          return;
        }
      }
      //check helper methods
      start = getWayNodesAtIntersection(coms[1], coms[2]);
      end = getWayNodesAtIntersection(coms[3], coms[4]);
      //if intersection not found
      if (start == null && end == null) {
        System.err.println("ERROR: No intersection found: Both set of streets don't intersect");
        return;
        //if starting node and ending node are equal, can't find a path
      } else if (start == null) {
        System.err.println("ERROR: No intersection found: The first two streets don't intersect");
        return;
      } else if (end == null) {
        System.err.println("ERROR: No intersection found: The last two streets don't intersect");
        return;
      } else if (start.equals(end)) {
        System.out.println("No route found- start and end nodes are the same");
        return;
      } else {
        String startID = start.getId();
        String endID = end.getId();
        wayNodeCache.put(startID, start);
        wayNodeCache.put(endID, end);
        end = graph.aStar(start, end);
        //no path exists
        if (end == null) {
          System.out.println(startID + " -/- " + endID);
          return;
        } else {
          //check helper method, prints to terminal
          this.printRoute(start, end);
        }
      }
      //lat and long inputted
    } else {
      try {
        double lat1 = Double.parseDouble(coms[1]);
        double long1 = Double.parseDouble(coms[2]);
        WayNodes startTarget = this.getWayNodeByLatLong(lat1, long1);
        //find start node using nearestNeighbors
        List<WayNodes> startRet = wayNodesTree.nearestNeighbors(1, startTarget, false);
        start = startRet.get(0);
        double lat2 = Double.parseDouble(coms[3]);
        double long2 = Double.parseDouble(coms[4]);
        WayNodes endTarget = this.getWayNodeByLatLong(lat2, long2);
        //find ending node using nearestNeighbors
        List<WayNodes> endRet = wayNodesTree.nearestNeighbors(1, endTarget, false);
        end = endRet.get(0);
        //if start and end nodes are same, want to print nothing
        if (start.equals(end)) {
          System.out.println("No route found- start and end nodes are the same");
          return;
        }
        String startID = start.getId();
        String endID = end.getId();
        end = graph.aStar(start, end);
        //no path exists
        if (end == null) {
          System.out.println(startID + " -/- " + endID);
          return;
        } else {
          //check helper method, prints to terminal
          this.printRoute(start, end);
        }
      } catch (NumberFormatException e) {
        System.err.println("ERROR: Latitudes and longitudes must be ints or doubles");
        return;
      }

    }
  }

  /**
   * This is the implementation of the route method.
   * @param coms an array of whitespace split commands
   * @return a list of routes
   */
  public List<WayNodes> routeRet(String[] coms) {
    List<WayNodes> ret = new ArrayList<>();
    //check if we haven't loaded data
    if (wayNodesTree == null || wayNodeList == null) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return ret;
    }
    if (wayNodesTree.isEmpty() || wayNodeList.isEmpty()) {
      System.err.println("ERROR: Please load a maps db into the REPL");
      return ret;
    }
    if (coms.length != 5) {
      System.err.println("ERROR: Incorrect number of args provided for route command");
      return ret;
    }
    WayNodes start;
    WayNodes end;
    graph = new Graph<>();
    //street names inputted
    if (!this.isClassInt(coms)) {
      for (int i = 1; i < coms.length; i++) {
        //checking that each argument is a nonempty string
        if (!(coms[i] instanceof String && coms[i] != "")) {
          System.err.println("ERROR: Street names must be nonempty strings");
          return ret;
        }
      }
      //check helper methods
      start = getWayNodesAtIntersection(coms[1], coms[2]);
      end = getWayNodesAtIntersection(coms[3], coms[4]);
      //if intersection not found
      if (start == null && end == null) {
        System.err.println("ERROR: No intersection found: Both set of streets don't intersect");
        return ret;
        //if starting node and ending node are equal, can't find a path
      } else if (start == null) {
        System.err.println("ERROR: No intersection found: The first two streets don't intersect");
        return ret;
      } else if (end == null) {
        System.err.println("ERROR: No intersection found: The last two streets don't intersect");
        return ret;
      } else if (start.equals(end)) {
        System.out.println("No route found- start and end nodes are the same");
        return ret;
      } else {
        String startID = start.getId();
        String endID = end.getId();
        wayNodeCache.put(startID, start);
        wayNodeCache.put(endID, end);
        end = graph.aStar(start, end);
        //no path exists
        if (end == null) {
          System.out.println(startID + " -/- " + endID);
          return ret;
        } else {
          //check helper method, prints to terminal
          //this.printRoute(start, end);
          return this.getList(start, end);
        }
      }
      //lat and long inputted
    } else {
      try {
        double lat1 = Double.parseDouble(coms[1]);
        double long1 = Double.parseDouble(coms[2]);
        WayNodes startTarget = this.getWayNodeByLatLong(lat1, long1);
        //find start node using nearestNeighbors
        List<WayNodes> startRet = wayNodesTree.nearestNeighbors(1, startTarget, false);
        start = startRet.get(0);
        double lat2 = Double.parseDouble(coms[3]);
        double long2 = Double.parseDouble(coms[4]);
        WayNodes endTarget = this.getWayNodeByLatLong(lat2, long2);
        //find ending node using nearestNeighbors
        List<WayNodes> endRet = wayNodesTree.nearestNeighbors(1, endTarget, false);
        end = endRet.get(0);
        //if start and end nodes are same, want to print nothing
        if (start.equals(end)) {
          System.out.println("No route found- start and end nodes are the same");
          return ret;
        }
        String startID = start.getId();
        String endID = end.getId();
        end = graph.aStar(start, end);
        //no path exists
        if (end == null) {
          System.out.println(startID + " -/- " + endID);
          return ret;
        } else {
          //check helper method, prints to terminal
          //this.printRoute(start, end);
          return this.getList(start, end);
        }
      } catch (NumberFormatException e) {
        System.err.println("ERROR: Latitudes and longitudes must be ints or doubles");
        return ret;
      }

    }
  }


  /**.
   * Takes in the command line for naive_neighbors and radius
   * and sees if second arg is an int
   * @param scale string[] of commands
   * @return boolean representing if it takes an int (true if int)
   */
  public boolean isClassInt(String[] scale) {
    try {
      Double.parseDouble(scale[1]);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }


  /**
   * Helper method to print route path to terminal.
   * @param start representing starting WayNode
   * @param end representing ending WayNode
   */
  private void printRoute(WayNodes start, WayNodes end) {
    List<Way> route = new ArrayList<>();
    WayNodes currNode = end;
    //looping through the route, adding ways to list
    while (currNode.getId() != start.getId()) {
      Way currWay = currNode.getFrom();
      route.add(currWay);
      currNode = currWay.getFrom();
    }
    for (int i = route.size() - 1; i >= 0; i--) {
      //formatting string to print in terminal
      System.out.println(route.get(i).getFrom().getId() + " -> " + route.get(i).getTo().getId()
              + " : " + route.get(i).getId());
    }
  }

  /**
   * This is a function to get a List of Nodes.
   * @param start the start node we want to get to
   * @param end the end node we want to get to
   * @return list a list of Nodes
   */
  private List<WayNodes> getList(WayNodes start, WayNodes end) {
    List<WayNodes> route = new ArrayList<>();
    WayNodes currNode = end;
    route.add(currNode);
    //looping through the route, adding ways to list
    while (currNode.getId() != start.getId()) {
      Way currWay = currNode.getFrom();
      currNode = currWay.getFrom();
      route.add(currNode);
    }
    Collections.reverse(route);
    return route;
  }

  /**
   * Method to create a WayNode for nearest search, based on specific lat-long pair.
   * @param lat latitude of the point
   * @param longit longitude of the point
   * @return a WayNodes object with those points
   */
  public WayNodes getWayNodeByLatLong(double lat, double longit) {
    for (WayNodes node: wayNodeList) {
      if (Double.compare(node.getLat(), lat) == 0 && Double.compare(node.getLong(), longit) == 0) {
        return node;
      }
    }
    return new WayNodes("temp", lat, longit, this.conn);
  }

  /**
   * Gets a waynode by the string id name using the WayNodeCache.
   * @param s1 the id name
   * @return WayNode found by ID
   */
  public WayNodes getIDByString(String s1) {
    if (s1 == null || s1.equals("")) {
      return null;
    } else {
      try {
        if (wayNodeCache.containsKey(s1)) {
          return wayNodeCache.get(s1);
        } else {
          PreparedStatement getNode = conn.prepareStatement("SELECT * FROM node WHERE node.id = ?");
          getNode.setString(1, s1);
          ResultSet answer1 = getNode.executeQuery();
          WayNodes ws = null;
          //instantiate WayNode object
          while (answer1.next()) {
            ws = new WayNodes(answer1.getString(1),
              answer1.getDouble(2), answer1.getDouble(3), conn);
            wayNodeCache.put(ws.getId(), ws);
          }
          return ws;
        }
      } catch (SQLException e) {
        return null;
      }
    }
  }



  /**
   * Returns a wayNode at a given intersection street using SQL queries.
   * @param s1 street 1
   * @param s2 street 2
   * @return node at intersection of streets (or null if none exists)
   */
  public WayNodes getWayNodesAtIntersection(String s1, String s2) {
    try {

      PreparedStatement getWay1 = conn.prepareStatement("WITH street1 AS "
          + "(SELECT start, end FROM way WHERE name = ?), "
          + "cross1 as (SELECT start, end FROM way where name = ?) , "
          + "streetNodes AS (SELECT start AS nodeID FROM street1 UNION SELECT "
          + "end AS nodeID FROM street1)"
          + ", crossNodes AS (SELECT start AS nodeID FROM cross1 UNION SELECT "
          + "end as nodeID from cross1) "
          + "SELECT * FROM streetNodes INNER JOIN crossNodes ON "
          + "streetNodes.nodeID = crossNodes.nodeID");
      getWay1.setString(1, s1.substring(1, s1.length() - 1));
      getWay1.setString(2, s2.substring(1, s2.length() - 1));
      ResultSet answer1 = getWay1.executeQuery();
      while (answer1.next()) {
        String node = answer1.getString("nodeID");
        answer1.close();
        return this.getIDByString(node);
      }
      return null;
    } catch (SQLException e) {
      return null;
    }
  }
}


