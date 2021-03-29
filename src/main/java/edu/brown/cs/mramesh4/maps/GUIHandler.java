package edu.brown.cs.mramesh4.maps;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.mramesh4.SQLDatabase.UserSQLDatabase;
import edu.brown.cs.mramesh4.stars.Star;
import edu.brown.cs.mramesh4.stars.StarsLogic;
import org.json.JSONObject;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a class for a GUIHandler.
 */
public class GUIHandler {
  private static MapsLogic map;
  private static final Gson GSON = new Gson();
  private static UserSQLDatabase database;
  private static StarsLogic db;
  private static CheckinThread ct;

  /**
   * This is the constructor for the handler. It takes in all the pre-req info.
   * @param databases database class
   * @param maps mapsLogic class
   * @param star starsLogic class
   * @param c checkinThread instance
   */
  public GUIHandler(UserSQLDatabase databases, MapsLogic maps, StarsLogic star, CheckinThread c) {
    database = databases;
    map = maps;
    db = star;
    ct = c;
  }

  /**
   * This is a GuiHandler method used to run route methods.
   * @param call name of method to run
   * @return a handler
   */
  public Route method(String call) {
    Route c = null;
    switch (call) {
      case "UserHandler":
        c =  new UserHandler();
        break;
      case "WayHandler":
        c = new WayHandler();
        break;
      case "RouteCoorsHandler":
        c = new RouteCoorsHandler();
        break;
      case "NearestHandler":
        c = new NearestHandler();
        break;
      case "RouteStreetsHandler":
        c = new RouteStreetsHandler();
        break;
      case "CheckInHandler":
        c = new CheckInHandler();
        break;
      default:
        c = null;
        break;
    }
    return c;
  }

  /**
   * Handler to handle checkins.
   */
  private static class CheckInHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      Map<Double, UserCheckin> ret = ct.getLatestCheckins();
      Map<String, Object> variables = ImmutableMap.of("users", ret);
      return GSON.toJson(variables);
    }
  }

  /**
   * method to handle template view routes.
   * @param call a method to run
   * @return a handler for that method
   */
  public TemplateViewRoute methodTVR(String call) {
    TemplateViewRoute c = null;
    switch (call) {
      case "NeighborsHandler":
        c =  new NeighborsHandler();
        break;
      case "RadiusHandler":
        c = new RadiusHandler();
        break;
      case "FrontHandler":
        c = new FrontHandler();
        break;
      default:
        c = null;
        break;
    }
    return c;
  }

  /**
   * This is a handler to handle getting users from the database.
   */
  private static class UserHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String user = data.getString("userid");
      List<UserCheckin> ret = database.getUserInfo(user);
      Map<String, Object> variables = ImmutableMap.of("user", ret);
      return GSON.toJson(variables);

    }
  }

  /**
   * Handle requests to the front page of our Stars website.
   *
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String ret = "No results to display";
      if (db.getStarList().size() == 0) {
        ret = "Please load a file in the command line";
      }
      Map<String, Object> variables = ImmutableMap.of("title",
          "Stars: Query the database", "results", ret);
      return new ModelAndView(variables, "query.ftl");
    }
  }

  /**
   * This is a handler to get all the ways within a route.
   */
  private static class WayHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      double sLat = data.getDouble("lat1");
      double sLon = data.getDouble("long1");
      double dLat = data.getDouble("lat2");
      double dLon = data.getDouble("long2");
      String[] com = new String[]{"ways", Double.toString(sLat), Double.toString(sLon),
        Double.toString(dLat), Double.toString(dLon)};
      System.out.println(com[0] + com[1] + com[2] + com[3] + com[4]);
      List<Way> ret = map.ways(com);
      Map<String, Object> variables = ImmutableMap.of("ways", ret);
      return GSON.toJson(variables);
    }
  }


  /**
   * Handles requests made for a route.
   */
  private static class RouteCoorsHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      try {
        JSONObject data = new JSONObject(request.body());
        double sLat = data.getDouble("srclat");
        double sLon = data.getDouble("srclong");
        double dLat = data.getDouble("destlat");
        double dLon = data.getDouble("destlong");

        String[] com = new String[]{"route", Double.toString(sLat), Double.toString(sLon),
          Double.toString(dLat), Double.toString(dLon)};
        List<WayNodes> coordinates = map.routeRet(com);
        Map<String, Object> variables = ImmutableMap.of("routeCoors", coordinates);
        return GSON.toJson(variables);

      } catch (Exception e) {
        Map<String, Object> variables = ImmutableMap.of("routeCoors", new ArrayList<>());
        return GSON.toJson(variables);
      }

    }
  }


  /**
   * Handles requests made for a route.
   */

  private static class RouteStreetsHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String street1 = data.getString("street1");
      String cross1 = data.getString("cross1");
      String street2 = data.getString("street2");
      String cross2 = data.getString("cross2");
      String[] com = new String[]{"route", street1, cross1, street2, cross2};
      List<WayNodes> coordinates = map.routeRet(com);
      Map<String, Object> variables = ImmutableMap.of("routeStreets", coordinates);
      return GSON.toJson(variables);
    }
  }


  /**
   * Gets the nearest node.
   */
  private static class NearestHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      double sLat = data.getDouble("lat");
      double sLon = data.getDouble("long");
      String[] com = new String[]{"nearest", Double.toString(sLat), Double.toString(sLon)};
      WayNodes node = map.nearest(com);
      Map<String, Object> variables = ImmutableMap.of("nearest", node);
      return GSON.toJson(variables);
    }
  }


  /**
   * Handles the radius call on the gui.
   */
  private static class RadiusHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String cmd = qm.value("text2");
      String ret = "No results to display";
      if (db.getStarList().size() == 0) {
        ret = "Please load a file in the command line";
      } else if (cmd == null || cmd.equals("")) {
        ret = "Please input a valid answer";
      } else {
        String neighbs = qm.value("radius_switched");
        String start = "naive_radius";
        if (neighbs != null) {
          start = "radius";
        }
        String cmd2 = start.concat(" ").concat(cmd);
        String[] coms = cmd2.split(" ");
        List<Star> stars = db.radiusRet(cmd2, coms);

        if (db.getStarList().size() == 0) {
          ret = "Please load a file in the command line";
        }
        if (coms[0].toUpperCase().equals("NAIVE_NEIGHBORS")
            || coms[0].toUpperCase().equals("NEIGHBORS")) {
          ret = "Please use the neighbors box for this action";
        } else {
          if (db.getStarList().size() == 0) {
            ret = "Please load a file in the command line";
          }
          if (stars != null && !stars.isEmpty()) {
            ret = "The stars within the radius: " + coms[1] + " are";
            ret += "<br> <br>";
            ret += "<table id=\"table\">"
              + "<tr> <th> Match # </th> <th> Name </th>"
              + "<th> ID </th> <th> X </th> <th> Y </th> <th> Z </th> </tr>";
            for (int i = 0; i < stars.size(); i++) {
              Star curr = stars.get(i);
              ret += "<tr><td>" + Integer.toString(i + 1) + "</td>" + "<td>" + curr.getName()
                + "</td>" + "<td>" + curr.getStarID() + "</td>"
                + "<td>" + curr.getX() + "</td>"
                + "<td>" + curr.getY() + "</td>"
                + "<td>" + curr.getZ() + "</td>";
              ret += "</tr>";
            }
            ret += "</table>";
          } else {
            ret = "No stars found in the search";
          }
        }
      }
      Map<String, Object> variables = ImmutableMap.of("title",
          "Stars: Query the database", "results", ret);
      return new ModelAndView(variables, "query.ftl");
    }
  }

  /**
   * Handles the neighbors call on the GUI.
   */
  private static class NeighborsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String cmd = qm.value("text");
      String ret = "No results to display";
      if (db.getStarList().size() == 0) {
        ret = "Please load a file in the command line";
      } else if (cmd == null || cmd.equals("")) {
        ret = "Please input a valid answer";
      } else {
        String neighbs = qm.value("neighbors_switched");
        String start = "naive_neighbors";
        if (neighbs != null) {
          start = "neighbors";
        }
        String cmd2 = start.concat(" ").concat(cmd);
        String[] coms = cmd2.split(" ");
        List<Star> stars = db.neighborsRet(cmd2, coms);
        if (coms[0].toUpperCase().equals("NAIVE_RADIUS")
            || coms[0].toUpperCase().equals("RADIUS")) {
          ret = "Please use the radius box for this action";
        } else {
          if (stars != null && !stars.isEmpty()) {
            ret = "The " + Integer.toString(stars.size()) + " nearest neighbors are:";
            //
            ret += "<br> <br>";
            ret += "<table id=\"table\">"
              + "<tr> <th> Match # </th> <th> Name </th>"
              + "<th> ID </th> <th> X </th> <th> Y </th> <th> Z </th> </tr>";
            for (int i = 0; i < stars.size(); i++) {
              Star curr = stars.get(i);
              ret += "<tr><td>" + Integer.toString(i + 1) + "</td>"
                + "<td>" + curr.getName() + "</td>"
                + "<td>" + curr.getStarID() + "</td>"
                + "<td>" + curr.getX() + "</td>" + "<td>" + curr.getY()
                + "</td>" + "<td>" + curr.getZ() + "</td>";
              ret += "</tr>";
            }
            ret += "</table>";
          } else {
            ret = "No stars found in the search";
          }
        }
      }
      Map<String, Object> variables = ImmutableMap.of("title",
          "Stars: Query the database", "results", ret);
      return new ModelAndView(variables, "query.ftl");
    }
  }
}
