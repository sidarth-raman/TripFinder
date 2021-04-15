package edu.brown.cs.mramesh4.GraphTest;

import edu.brown.cs.mramesh4.TripGraph.CityDatabaseReader;
import edu.brown.cs.mramesh4.TripGraph.CityNode;
import edu.brown.cs.mramesh4.TripGraph.CompleteTripGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GraphAlgoModelTesting {
  List<CityNode> cities;
  @Before
  public void setUp(){
    CityDatabaseReader database = new CityDatabaseReader("data.sqlite");
    database.loadCityDB();
    cities = database.getNodes();
  }
  @After
  public void tearDown(){
    cities = null;
  }
  @Test
  public void testEffiencyofGraph(){
    setUp();
    double mstWins = 0;
    double christWins = 0;
    for(int i = 0; i < 40; i++){
      List<CityNode> currGraph = new ArrayList<>();
      int random_size = (int)Math.floor(Math.random()*(20-3)+2);
      for(int j = 0; j < random_size; j++){
        int random_elem = (int)Math.floor(Math.random()*(cities.size()));
        currGraph.add(cities.get(random_elem));
      }
      int random_start = (int)Math.floor(Math.random()*(random_size));
      CityNode start = currGraph.get(random_start);

      CompleteTripGraph cg = new CompleteTripGraph(currGraph);
      List<CityNode> mstdfs = cg.TwoOptTSP(start);
      List<CityNode> christ = cg.christTSP(start);

      double mstCost = 0;
      for(int m = 0; m < mstdfs.size() - 1; m++){
        mstCost += mstdfs.get(m).distanceBetween(mstdfs.get(m+1));
      }

      double christCost = 0;
      for(int l = 0; l < christ.size() - 1; l++){
        christCost += mstdfs.get(l).distanceBetween(mstdfs.get(l+1));
      }

      //System.out.println("mstCost" + mstCost + "christCost" + christCost);

      if(mstCost > christCost){
        christWins++;
        System.out.println("Christofedes won on graph of size" + random_size);
      } else if(mstCost < christCost){
        mstWins++;
        System.out.println("MST won on graph of size" + random_size);
      } else{
        mstWins++;
        christWins++;
        System.out.println("Algos tied on graph of size" + random_size + "mstCost" + mstCost + "ChristCost" + christCost);
      }
    }

    System.out.println("mstWins" + mstWins + "christWins" + christWins);
    tearDown();
  }

  @Test
  public void testGraphAlgosonDifferentSizes(){

    setUp();
    //run trials on 20;
    for(int k = 2; k < 300; k++){
      //run 100 trials on the given size
      int mstWins = 0;
      int christWins = 0;
      for(int i = 0; i < 100; i++){
        List<CityNode> currGraph = new ArrayList<>();
        for(int j = 0; j < k; j++){
          int random_elem = (int)Math.floor(Math.random()*(cities.size()));
          currGraph.add(cities.get(random_elem));
        }
        int random_start = (int)Math.floor(Math.random()*(k));
        CityNode start = currGraph.get(random_start);

        CompleteTripGraph cg = new CompleteTripGraph(currGraph);
        List<CityNode> mstdfs = cg.TwoOptTSP(start);
        List<CityNode> christ = cg.christTSP(start);

        double mstCost = 0;
        for(int m = 0; m < mstdfs.size() - 1; m++){
          mstCost += mstdfs.get(m).distanceBetween(mstdfs.get(m+1));
        }

        double christCost = 0;
        for(int l = 0; l < christ.size() - 1; l++){
          christCost += mstdfs.get(l).distanceBetween(mstdfs.get(l+1));
        }

        if(mstCost > christCost){
          christWins++;
        } else if(mstCost < christCost){
          mstWins++;
        } else{
          mstWins++;
          christWins++;
        }
      }
      System.out.println("On a graph of size" + k + "mst had success rate of" + mstWins + "/100" + "and christofedes had a success rate of" + christWins + "/100");
    }
  }
}
