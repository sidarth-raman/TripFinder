package edu.brown.cs.mramesh4.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.mramesh4.TripGraph.CityDatabaseReader;
import edu.brown.cs.mramesh4.TripGraph.CityEdge;
import edu.brown.cs.mramesh4.TripGraph.CityNode;
import edu.brown.cs.mramesh4.TripGraph.CompleteTripGraph;
import edu.brown.cs.mramesh4.TripGraph.GraphBuilder;
import edu.brown.cs.mramesh4.TripGraph.TripGraph;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.json.JSONObject;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;
import freemarker.template.Configuration;
import com.google.gson.Gson;


/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;


  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private static final Gson GSON = new Gson();
  private static CityDatabaseReader database;

  private static final double DELTA = 0.01;
  CompleteTripGraph<CityNode, CityEdge> tripGraph;
  private TripGraph<CityNode, CityEdge> tGraph1;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    database = new CityDatabaseReader("data.sqlite");
    database.readDB();
    List<String> sts = new ArrayList<>();
    sts.add("Los Angeles, CA");
//    sts.add("Miami, FL");
    GraphBuilder g = new GraphBuilder(database.connect(), "Providence, RI", 2000, 4, sts);
    for (CityNode n : g.getPath()) {
      System.out.println(n.getName());
    }
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {

    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");

    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });
    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));


    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();
    Spark.post("/route", new RouteHandler());
    Spark.post("/city", new AllCityHandler());
    Spark.post("/city", new CityActivityHandler());
  }


  private static class CityActivityHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String cityName;
      CityNode node = null;
      StringBuilder errorMessage = new StringBuilder();
      try{
        cityName = data.getString("cityName");
        String cityToQuery = cityName.split(",")[0];

        Connection conn = database.connect();
        PreparedStatement prep = null;

        try {
          prep = conn.prepareStatement("SELECT city, state_id, lat, lng, population, id FROM cities where city = ?;");
          prep.setString(1, cityToQuery);
          ResultSet rs = prep.executeQuery();
          while (rs.next()) {
            String name = rs.getString(1) + ", " + rs.getString(2);
            if(name.equals(cityName)) {
              double lat = rs.getDouble(3);
              double lon = rs.getDouble(4);
              int pop = rs.getInt(5);
              node = new CityNode(name, lat, lon, pop);
            }
          }
          rs.close();
          prep.close();
        } catch (SQLException throwables) {
          System.out.println("ERROR: sql query gone wrong");
          throwables.printStackTrace();
          errorMessage.append(" error with sql query");
        }

      } catch(Exception e){
        System.out.println("ERROR in parsing cityname for activities");
        errorMessage.append(" error in reading city name. ");
      }
      List<String> activitiesList = new ArrayList<>();
      if(node == null){
        errorMessage.append(" error: city not found. ");
      } else {
        node.setActivities();
        activitiesList = node.getActivities();
      }

      Map<String, Object> variables = ImmutableMap.of("activities", activitiesList, "error", errorMessage.toString());
      return GSON.toJson(variables);
    }
  }

  private static class RouteHandler implements Route {
    private final double earthRadius = 3958.8; //miles

    @Override
    public Object handle(Request request, Response response) throws Exception {

      JSONObject data = new JSONObject(request.body());
      boolean error = false;
      String origin = null;
      double maxDist = 0;
      int maxNumCities = 0;
      String cities = null;

      try {
        origin = data.getString("origin");
        maxDist = Double.parseDouble(data.getString("maxDist").split(" ")[0]);
        maxNumCities = data.getInt("numberOfCities");
        cities = data.getString("city");
      } catch (Exception e) {
        System.out.println("ERROR: parsing data sent from frontend");
        error = true;
      }

      if(origin.contains("Select")){
        error = true;
      }

      //TODO: Coordinate with Sid to handle passing in multiple cities to visit
//      String[] cities = data.getString("city").split(",");

      if (!error) {
        List<String> citiesToVisit = new ArrayList<>();
//      citiesToVisit = Arrays.asList(cities);
        citiesToVisit.add(cities);

        System.out.println("Origin received: " + origin);
        System.out.println("MaxDist received: " + maxDist);
        System.out.println("MaxNumCities received: " + maxNumCities);
        System.out.println("CitiesToVisit received: " + citiesToVisit.toString());
        List<CityNode> path = null;
        if (origin.length() > 1) {
          GraphBuilder
              graph =
              new GraphBuilder(database.connect(), origin, maxDist, maxNumCities, citiesToVisit);
          System.out.println("Graph builder ran");
          for (CityNode n : graph.getCitiesOfGraph()) {
            System.out.println("graphbuilder contains: " + n.getName());
          }
          path = graph.getPath();
          for (CityNode n : path) {
            System.out.println("city in path returned: " + n.getName());
          }
          System.out.println("path size: " + path.size());
        }
        List<String> cityNames = new ArrayList<>();
        double[][] latLong = new double[path.size()][2];

        for (int i = 0; i < path.size(); i++) {
          CityNode n = path.get(i);
          cityNames.add(n.getName());
          latLong[i][0] = n.getLat();
          latLong[i][1] = n.getLong();
        }
        double routeDist = Math.round(this.calcRouteDistance(path));
        int tripTime = (int) Math.round(routeDist / 60) + 1;
        String routeInfo =
            "You have visited " + maxNumCities + " cities in a trip that will take " + tripTime +
                " hours.";
        Map<String, Object> variables = ImmutableMap
            .of("output", cityNames, "latLong", latLong, "routeDist", routeDist, "error", "",
                "routeInfoMessage", routeInfo);

        return GSON.toJson(variables);
      } else {
        String[][] blankArray = new String[0][0];
        double[][] latLong = new double[0][0];
        return GSON.toJson(ImmutableMap
            .of("output", blankArray, "latLong", latLong, "routeDist", 0, "error",
                "Please select an origin city", "routeInfoMessage", ""));
      }
    }

    private double calcRouteDistance(List<CityNode> path) {
      double sum = 0;
      for (int i = 0; i < path.size() - 1; i++) {
        sum += this.haversineDist(path.get(i), path.get(i + 1));
      }
      return sum;
    }

    private double haversineDist(CityNode a, CityNode b) {
      // Find distance in radians between latitudes and longitudes

      double distLat = Math.toRadians(a.getLat() - b.getLat());
      double distLong = Math.toRadians(a.getLong() - b.getLong());

      // Convert latitudes to radians
      double startLat = Math.toRadians(b.getLat());
      double endLat = Math.toRadians(a.getLat());

      double calc = Math.pow(Math.sin(distLat / 2), 2)
          + Math.pow(Math.sin(distLong / 2), 2) * Math.cos(startLat) * Math.cos(endLat);
      double calc2 = 2 * Math.asin(Math.sqrt(calc));
      return earthRadius * calc2;

    }
  }

  private static class AllCityHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      List<String> alphabetizedCities = database.getCities();
      Collections.sort(alphabetizedCities);
      Map<String, Object> variables = ImmutableMap.of("cityList", alphabetizedCities.toArray());
      return GSON.toJson(variables);
    }
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}

