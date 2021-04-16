package edu.brown.cs.mramesh4.TripGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a serialized object that stores each of the cities individual information.
 */
public class CityInformationObject{
  List<CityData> results;
}

class CityData{
  List<POI> pois;
  List<POIDivision> poi_division;
  String more;
}

class POIDivision{
  String tag_name;
  String tag_label;
  List<String> poi_ids;
}

class POI{
  String snippet;
  String name;
  Double distance;
  String id;
  List<Double> coordinates;
}