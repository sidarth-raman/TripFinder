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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
    return 3959 * calc2;
  }

  private void run() {
    database = new CityDatabaseReader("data.sqlite");
    database.readDB();


//    this.testThisDistanceThang(500);

//    this.whyDoesntNoVisitWork();

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

  private void whyDoesntNoVisitWork(){
    List<String> sts = new ArrayList<>();
    String originCity = "Milwaukee, WI";
//    sts.add("Los Angeles, CA");
    sts.add("Chicago, IL");
    GraphBuilder g = new GraphBuilder(database.connect(), originCity, 5000, 5, sts);
    List<CityNode> path = g.getPath();
    for (CityNode n : g.getCitiesOfGraph()) {
      System.out.println("graphbuilder contains: " + n.getName());
    }
    for (CityNode n : path) {
      System.out.println(n.getName());
    }
  }

  private void testThisDistanceThang(int dist1){
    double average = 0;
    double trials = 200;
    int totalAvg = 0;
    List<Double> averages = new ArrayList<>();
    for (int j = 3; j < 7; j++) {
      HashMap<String, Integer> map = new HashMap<>();
      for (int i = 0; i < trials; i++) {
        List<String> sts = new ArrayList<>();
        String originCity = "Milwaukee, WI";
//    sts.add("Los Angeles, CA");

        sts.add("Chicago, IL");
        GraphBuilder g = new GraphBuilder(database.connect(), originCity, dist1, j, sts);
        List<CityNode> path = g.getPath();
//    for (CityNode n : path) {
//      System.out.println(n.getName());
//    }
        for (CityNode n : path) {
          if(!sts.contains(n.getName()) && !originCity.equals(n.getName())) {
            if (!map.containsKey(n.getName())) {
              map.put(n.getName(), 1);
            } else {
              int num = map.get(n.getName());
              map.replace(n.getName(), num + 1);
            }
          }
        }
        double dist = Math.round(this.calcRouteDistance(path));
        average += dist;
//      System.out.println("Distance: " + dist + " miles");
      }
      totalAvg += (average/4);
      Map<String, Integer> hm1 = sortByValue(map);
      StringBuilder stt = new StringBuilder("Most frequent cities: ");
      List<String> cc = new ArrayList<>();
      for (Map.Entry<String, Integer> en : hm1.entrySet()) {
        cc.add(en.getKey().split(",")[0] + ": " + en.getValue().toString() + " ");
      }
      System.out.println("For " + j + " cities");

      for(int z = 0; z < Math.min(5, cc.size()); z++){
        stt.append(cc.get(z));
      }
      System.out.println(stt.toString());
      System.out.println("average trip length: " + Math.round(average / (4*trials)));
      averages.add(average / (4*trials));
    }

    totalAvg = (int) Math.round(totalAvg/(4*trials));
    System.out.println("total average trip length: " + totalAvg + " off by: " +  Math.abs(totalAvg - dist1));
    System.out.println("deviation: " + Math.round(Collections.max(averages) - Collections.min(averages)));
  }

  public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
  {
    // Create a list from elements of HashMap
    List<Map.Entry<String, Integer> > list =
        new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

    // Sort the list
    Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
      public int compare(Map.Entry<String, Integer> o1,
                         Map.Entry<String, Integer> o2)
      {
        return (o2.getValue()).compareTo(o1.getValue());
      }
    });

    // put data from sorted list to hashmap
    HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
    for (Map.Entry<String, Integer> aa : list) {
      temp.put(aa.getKey(), aa.getValue());
    }
    return temp;
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
    Spark.post("/activity", new CityActivityHandler());
  }


  private static class CityActivityHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String cityName;
      CityNode node = null;
      StringBuilder errorMessage = new StringBuilder();
      try {
        cityName = data.getString("cityName");
        String cityToQuery = cityName.split(",")[0];

        Connection conn = database.connect();
        PreparedStatement prep = null;

        try {
          prep = conn.prepareStatement(
              "SELECT city, state_id, lat, lng, population, id FROM cities where city = ?;");
          prep.setString(1, cityToQuery);
          ResultSet rs = prep.executeQuery();
          while (rs.next()) {
            String name = rs.getString(1) + ", " + rs.getString(2);
            if (name.equals(cityName)) {
              double lat = rs.getDouble(3);
              double lon = rs.getDouble(4);
              int pop = rs.getInt(5);
              node = new CityNode(name, lat, lon, pop);
              System.out.println("city node for " + name);
            }
          }
          rs.close();
          prep.close();
        } catch (SQLException throwables) {
          throwables.printStackTrace();
          errorMessage.append(" error with sql query");
        }

      } catch (Exception e) {
        errorMessage.append(" error in reading city name. ");
      }
      List<String> activitiesList = new ArrayList<>();
      if (node == null) {
        errorMessage.append(" error: city not found. ");
      } else {
        node.setActivities();
        activitiesList = node.getActivities();
      }
      System.out.println(errorMessage.toString());
      Map<String, Object> variables =
          ImmutableMap.of("activities", activitiesList, "error", errorMessage.toString());
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
      String errorMessage = "";
      boolean numCityError = false;
      boolean distError = false;
      boolean originCityError = false;
      boolean sameCityError = false;

      try {
        origin = data.getString("origin");
        maxNumCities = data.getInt("numberOfCities");
        cities = data.getString("city");
      } catch (Exception e) {
        System.out.println("ERROR: parsing data sent from frontend");
        error = true;
        numCityError = true;
      }

      try {
        maxDist = Double.parseDouble(data.getString("maxDist").split(" ")[0]);
      } catch (Exception e) {
        System.out.println("ERROR: parsing data sent from frontend");
        error = true;
        distError = true;
      }


      if (origin.contains("Select")) {
        error = true;
        originCityError = true;
      } else if (origin.equals(cities)) {
        error = true;
        sameCityError = true;
      }

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

          if (path.size() == 2 && maxNumCities == 2) {
            if (path.get(0).equals(path.get(1))) {
              for (CityNode n : graph.getCitiesOfGraph()) {
                if (!n.getName().equals(path.get(0).getName())) {
                  path.add(1, n);
                }
              }
            }
          }
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
        if (originCityError && numCityError && distError) {
          errorMessage =
              "Please be sure to select an origin city, number of cities, and a distance!";
        } else if (originCityError && numCityError) {
          errorMessage = "Please be select an origin city and a number of cities!";
        } else if (originCityError && distError) {
          errorMessage = "Please select an origin city and a distance!";
        } else if (numCityError && distError) {
          errorMessage = "Please select a number of cities and a distance!";
        } else if (numCityError) {
          errorMessage = "Please select a number of cities!";
        } else if (distError) {
          errorMessage = "Please select a distance!";
        }

        if (sameCityError) {
          errorMessage +=
              " Origin city and city to visit need to be different! It's a circular tour :)";
        }
        return GSON.toJson(ImmutableMap
            .of("output", blankArray, "latLong", latLong, "routeDist", 0, "error",
                errorMessage, "routeInfoMessage", ""));
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