package edu.brown.cs.mramesh4.TripGraph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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


  /**
   * This is a 2Opt-Algorithm (At worse the cost
   * of solving this is 2*optimal cost) for the Traveling Salesman
   * Problem. This algorithm approximates the Tsp
   * @param start The node to start from
   * @return a list that represents a hamlitonian cycle
   */
  public List<N> TwoOptTSP(N start){
    //start with a complete graph
    //get a MST from Kruskals
    TripGraph<N, E> mst = this.Kruskals();
    //TODO: apply DFS to Kruskal's
    //TODO: remove duplicates
    return null;
  }

  /**
   * This is Kruskal's algorithm, which we use to generate a MST
   * within the graph. Using the MST, we will do a DFS on the MST.
   * @return an MST of the graph
   */
  public TripGraph<N, E> Kruskals(){
    TripGraph<N, E> mst = new TripGraph<>();
    //get the edgeList.
    List<E> edgeList = new ArrayList<>();
    //get all the edges within the graph and add them to the edgeList.
    for(int i = 0; i < this.getGraph().values().toArray().length; i++){
        for(int j = i+1; j < this.getGraph().values().toArray().length; j++){
          N node = (N) this.getGraph().values().toArray()[i];
          N node2 = (N) this.getGraph().values().toArray()[j];
          edgeList.add(node.getConnectingEdges().get(node2.getName()));
        }
    }
    System.out.println("Edges added in this graph are" + edgeList.size());
    //take the priorityQueue
    PriorityQueue<E> pq = new PriorityQueue<E>(new TripGraphEdgeComparator<N, E>());
    //add the list of sorted edges to the priorityqueue
    for(E edge: edgeList){
      pq.add(edge);
    }
    //checks to see the elements in the pq.
    while(!pq.isEmpty()){
      E curr = pq.poll();
      if(mst.getNumEdges() == this.getGraph().values().size() - 1){
        return mst;
      }
      mst.insertEdge(curr);
      //if there is a cycle because of this, remove it
      if(isCyclic(mst)){
        mst.deleteEdge(curr.getNodes().get(0), curr.getNodes().get(1));
      }
    }
      //if there was no minimum spanning tree: we return null, which indicates an issue.
    if(mst.getNumEdges() == this.getGraph().values().size() - 1){
      return mst;
    } else{
        System.out.println("There was an issue");
      return null;
    }
  }

  /**
   * This conducts an algorithm to check whether the graph is a cycle.
   * @param mst
   * @return
   */
  //TODO: Implement isCycle

  /**
   * This is a helper method to test if a graph is cyclic using BFs
   * @param mst: the graph to search:
   * @param node the node we are at currently
   * @param visited the visited edge.
   * @return
   */
  public boolean isCyclicHelper(TripGraph<N,E> mst, N node, HashMap<String, Boolean> visited){
    Deque<N> deque = new ArrayDeque<>(50);
    HashMap<N, N> parent = new HashMap<>();
    visited.put(node.getName(), true);
    deque.offerLast(node);
    while(!deque.isEmpty()){
      N curr = deque.getLast();
      for(N neighbor: curr.getNeighbors()){
        if(!visited.get(neighbor.getName())){
          visited.put(neighbor.getName(), true);
          deque.offerLast(neighbor);
          parent.put(neighbor, curr);
        } else if(!parent.get(neighbor).equals(curr)){
          return false;
        }
      }
    }
    return true;
  }

  /**
   * This method uses bfs to tell us if there is a cycle in the graph.
   * @param mst: A minimum spanning tree graph
   * @return a boolean if the mst is cyclic or not.
   */
  public boolean isCyclic(TripGraph<N,E> mst){
    HashMap<String, Boolean> visited = new HashMap<>();
    //set every node visited to false: this means we haven't visited yet
    for(N node: mst.getGraph().values()){
      visited.put(node.getName(), false);
    }
    //if we detect a cycle in the portion of the graph, we know there is a cycle
    //in the graph 
    for(N node: mst.getGraph().values()){
        if(!visited.get(node.getName()) && isCyclicHelper(mst, node, visited)){
          return true;
        }
    }
    return false;
  }


}
