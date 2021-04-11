package edu.brown.cs.mramesh4.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;


  /**
   * The initial method called when execution begins.
   *
   * @param args
   *          An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private static final Gson GSON = new Gson();
  private static CityDatabaseReader database;
  CityNode node;
  CityNode node2;
  CityNode node3;
  CityNode node4;
  CityNode node5;
  CityNode node6;
  private static final double DELTA = 0.01;
  CompleteTripGraph<CityNode, CityEdge> tripGraph;
  private TripGraph<CityNode, CityEdge> tGraph1;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {


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
    List<CityNode> ret = tripGraph.christTSP(node);
    for(CityNode city: ret){
      System.out.println("ETTN: " + city.getName());
    }
    database = new CityDatabaseReader("data.sqlite");
    database.readDB();
    for(String s : database.getCities()){
      System.out.println(s);
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
//    UserSQLDatabase database = new UserSQLDatabase("data/maps/smallMaps.sqlite3");
    FreeMarkerEngine freeMarker = createEngine();
//    CheckinThread check = new CheckinThread(database);
//    check.start();
//    GUIHandler gui = new GUIHandler(database, map, db, check);
    Spark.post("/route", new RouteHandler());
    Spark.post("/city", new AllCityHandler());


  }

  private static class RouteHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {

      JSONObject data = new JSONObject(request.body());
      System.out.println("handler called");
      //Retrieving preferences
      String origin = data.getString("origin").split(",")[0];
      System.out.println("checkpt 1");
      double maxDist = Double.parseDouble(data.getString("maxDist").split(" ")[0]);
      System.out.println("checkpt 2");

      int maxNumCities =  data.getInt("numberOfCities");
      System.out.println("checkpt 3");

      String[] cities = data.getString("city").split(",");
      System.out.println("checkpt 4");

      List<String> citiesToVisit = new ArrayList<>();
      citiesToVisit.add(cities[0]);
      System.out.println("checkpt 5");

      System.out.println("O1: " + origin);
      System.out.println("O2: " + maxDist);
      System.out.println("O3: " + maxNumCities);
      System.out.println("O4: " + citiesToVisit.toString());
      List<CityNode> path = null;
      if(origin.length() > 1) {
        GraphBuilder
            graph =
            new GraphBuilder(database.connect(), origin, maxDist, maxNumCities, citiesToVisit);
        System.out.println("checkpt 6");
        for (CityNode n : graph.getCitiesOfGraph()) {
          System.out.println("graphbuilder contains: " + n.getName());
        }
        path = graph.getPath();
        System.out.println("checkpt 7");
        for (CityNode n : path) {
          System.out.println("city in path returned: " + n.getName());
        }
        System.out.println("path size: " + path.size());
      }
      List<String> res = new ArrayList<>();
      for(CityNode n : path){
        res.add(n.getName());
      }
      Map<String, Object> variables = ImmutableMap.of("output", res);

//      return null;
      return GSON.toJson(variables);
    }
  }

  private static class AllCityHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      Map<String, Object> variables = ImmutableMap.of("cityList", database.getCities().toArray());
      return GSON.toJson(variables);
    }
  }

  /**
   * Display an error page when an exception occurs in the server.
   *
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
