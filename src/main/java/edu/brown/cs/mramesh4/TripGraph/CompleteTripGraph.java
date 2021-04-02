package edu.brown.cs.mramesh4.TripGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class CompleteTripGraph<N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>>{
  private HashMap<String, N> graph;

  /**
   * Empty constructor for a completeGraph
   */
  public CompleteTripGraph(){
    graph = new HashMap<>();
  }

  /**
   * Constructor for a complete graph. If all the nodes don't have edges
   * the graph will make sure that the graph is complete.
   * @param nodes a list of nodes to fill the graph with
   */
  public CompleteTripGraph(List<N> nodes){
    graph = new HashMap<>();
    for(int i = 0; i < nodes.size(); i++){
      N node = nodes.get(i);
      String name = node.getName();
      //makes sure the graph is complete
      if(node.getOutgoingEdges().size() != nodes.size() - 1) {
        for (int j = i+1; j < nodes.size(); j++) {
          if(!nodes.get(j).equals(i)) {
            node.insertEdges(nodes.get(j));
          }
        }
      }
      if(!graph.containsKey(name)){
        graph.put(name, node);
      }
    }
  }
  //TODO: use OOP to get rid of this and make this class more sensible
  /**
   * This takes in a graph that may not be complete and
   * returns a complete version
   * @param g graph that may or may not be complete
   */
  public CompleteTripGraph(TripGraph<N, E> g){
    graph = new HashMap<>();
    for(N node: g.getGraph().values()){
      String name = node.getName();
      if(!graph.containsKey(name)){
        graph.put(name, node);
      }
    }
    for(N node: this.graph.values()){
      String name = node.getName();
      for(N node2: this.graph.values()){
        if(!node.equals(node2)){
          node.insertEdges(node2);
        }
      }
      graph.put(name, node);
    }
  }

  /**
   * This is a method to run aStar on a graph.
   * @param start the name of the start city
   * @param end the name of the end city.
   * @return a list of nodes to visit
   */
  public List<N> aStar(String start, String end){
    List<N> ret = new ArrayList<>();
    if(!graph.containsKey(start) || !graph.containsKey(end)){
      return null;
    } else if(start.equals(end)){
      ret.add(graph.get(start));
      return ret;
    } else{
      ret = aStarHelper(graph.get(start), graph.get(end));
      return ret;
    }
  }

  /**
   * This is a helper to run aStar within the graph that we are writing
   * @param start the start node to search from
   * @param end the end node to start from
   * @return a list of nodes
   */
  public List<N> aStarHelper(N start, N end){
    ArrayList<N> ret = new ArrayList<>();
    //create a comparator that sorts them by a weight aStar assigns them
    PriorityQueue<N> pq = new PriorityQueue<N>(new TripGraphNodeComparator<N, E>());
    //set the distance travelled to 0
    start.setDistance(0);
    start.setWeight(0);
    //keep track of a visited
    HashMap<String, N> visited = new HashMap<>();
    HashMap<N, List<E>> deletedEdges = new HashMap<>();
    //put them in the visited
    visited.put(start.getName(), start);
    List<N> nodes = new ArrayList<>();
    //nodes.add(start);
    pq.add(start);
    return aStarHelp(end, pq, nodes, visited);
  }

  /**
   * Helper method to conduct the AStar search recursively and use backtracking
   * to save the route, since this method could not be used previously.
   * @param end the end node to search for
   * @param pq priorityqueue to use
   * @param nodes nodes of curr path
   * @param visited map of visited
   * @return a list of Nodes.
   */
  public List<N> aStarHelp(N end, PriorityQueue<N> pq, List<N> nodes, HashMap<String, N> visited){
    if(!pq.isEmpty()){
      N curr = pq.poll();
      nodes.add(curr);
      if (curr.equals(end)) {
        //return a copy in order to prevent alteration
        return new ArrayList<>(nodes);
      }
      List<N> neighbors = curr.getNeighbors();
      for(N next: neighbors){
        if(next.equals(curr)) {
          continue;
        } else{
          String name = next.getName();
          Double dist = curr.getDistance() + curr.getConnectingEdges().get(name).getWeight();
          Double totalWeight = dist + next.toGoal(end);
          if (!visited.containsKey(next.getName())) {
            next.setDistance(dist);
            next.setWeight(totalWeight);
            visited.put(next.getName(), next);
            pq.add(next);
          } else {
            N prev = visited.get(next.getName());
            //checking if we want to update the weight and distance
            if (prev.getWeight() > totalWeight) {
              next.setDistance(dist);
              next.setWeight(totalWeight);
              visited.put(next.getName(), next);
              pq.add(next);
            }
          }
        }
      }
      //backtracking
      List<N> ret = aStarHelp(end, pq, nodes, visited);
      nodes.remove(curr);
      return ret;
    } else{
      if(visited.containsKey(end)){
        return nodes;
      } else{
        return null;
      }
    }
  }

  /**
   * This is used to insert a node into the graph.
   * @param node the node to insert into the graph
   */
  public void insertNode(N node){
    String nodeName = node.getName();
    if(!graph.containsKey(nodeName) || (graph.containsKey(nodeName) && !graph.get(nodeName).equals(node))) {
      for(N node2: this.graph.values()){
        node.insertEdges(node2);
      }
      graph.put(nodeName, node);
    }
  }

  /**
   * Deletes a node from the graph
   * @param node the node to delete
   */
  public void deleteNode(N node){
    //This will take a node, loop through the graph and delete all edges between it
    String nodeName = node.getName();
    if(graph.containsKey(nodeName)){
      graph.remove(nodeName);
      for(N node2: this.graph.values()){
        node2.deleteEdge(node);
      }
    }
  }

  /**
   * Accesor method for graph.
   * @return a graph.
   */
  public HashMap<String, N> getGraph(){
    return graph;
  }



  //TODO:Implement TSP algorithm
}
