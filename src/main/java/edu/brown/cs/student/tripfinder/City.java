package edu.brown.cs.student.tripfinder;

public class City {

  private final double id;
  private final String name;
  private final String state;
  private final double lat;
  private final double lon;
  private final double population;

  public City(double id, String name, String state, double lat, double lon, double pop) {
    this.id = id;
    this.name = name;
    this.state = state;
    this.lat = lat;
    this.lon = lon;
    this.population = pop;
  }

  public double getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getState() {
    return state;
  }

  public double getLat() {
    return lat;
  }

  public double getLon() {
    return lon;
  }

  public double getPopulation() {
    return population;
  }
}
