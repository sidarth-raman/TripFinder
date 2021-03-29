package edu.brown.cs.mramesh4.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import edu.brown.cs.mramesh4.REPLLoop.REPL;
import edu.brown.cs.mramesh4.MockPerson.MockPersonMethod;

import edu.brown.cs.mramesh4.SQLDatabase.UserSQLDatabase;
import edu.brown.cs.mramesh4.stars.ActionMethod;
import edu.brown.cs.mramesh4.stars.StarsLogic;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
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
  private static StarsLogic db;
  private static MapsLogic map;
  private static final Gson GSON = new Gson();
  //private static UserSQLDatabase database;
  //private static CheckinThread check;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    db = new StarsLogic();
    map = new MapsLogic();
    MockPersonMethod m = new MockPersonMethod();
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
    .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
    HashMap<String, ActionMethod<?>> methods = new HashMap<>();
    methods.put("stars", db);
    methods.put("naive_neighbors", db);
    methods.put("naive_radius", db);
    methods.put("mock", m);
    methods.put("neighbors", db);
    methods.put("radius", db);
    methods.put("map", map);
    methods.put("ways", map);
    methods.put("nearest", map);
    methods.put("route", map);
    REPL repl = new REPL(methods);
    repl.read();
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
    UserSQLDatabase database = new UserSQLDatabase("data/maps/smallMaps.sqlite3");
    FreeMarkerEngine freeMarker = createEngine();
    CheckinThread check = new CheckinThread(database);
    check.start();
    GUIHandler gui = new GUIHandler(database, map, db, check);
    Spark.get("/stars", gui.methodTVR("FrontHandler"), freeMarker);
    Spark.post("/radius", gui.methodTVR("RadiusHandler"), freeMarker);
    Spark.post("/neighbors", gui.methodTVR("NeighborsHandler"), freeMarker);
    Spark.post("/routeCoors", gui.method("RouteCoorsHandler"));
    Spark.post("/routeStreets", gui.method("RouteStreetsHandler"));
    Spark.post("/ways", gui.method("WayHandler"));
    Spark.post("/nearest", gui.method("NearestHandler"));
    Spark.post("/user", gui.method("UserHandler"));
    Spark.post("/checkin", gui.method("CheckInHandler"));


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
