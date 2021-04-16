package edu.brown.cs.mramesh4.TripGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a serialized object that stores each of the cities individual information.
 */
public class CityInformationObject {
  List<CityData> results;
}

/**
 * Serialized citydata.
 */
class CityData {
  List<POI> pois;
  List<POIDivision> poi_division;
  String more;
}

/**
 * Serialized POIDivision.
 */
class POIDivision {
  String tag_name;
  String tag_label;
  List<String> poi_ids;
}

/**
 * Serialized POI Data.
 */
class POI{
  String snippet;
  String name;
  Double distance;
  String id;
  List<Double> coordinates;
}