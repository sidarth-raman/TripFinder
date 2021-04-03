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
    TripGraph<N, E> mst = this.Kruskals();
    List<N> mstDFS = this.dfsTree(mst, start);
    return this.removeDuplicates(mstDFS, start);
  }

  /**
   * Takes in the kruskal's tree and runs a DFS algorithm on it
   * in order to reduce the amount of nodes running.
   * @param mst a dfs-searched tree.
   * @return
   */
  public List<N> dfsTree(TripGraph<N,E> mst, N start){
    return null;
  }

  /**
   * Returns the list without duplicates
   * @param dfsTree: what we generated from the dfs search
   * @return the dfs searched.
   */
  public List<N> removeDuplicates(List<N> dfsTree, N start){
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
    HashMap<String, E> edgeList = new HashMap<>();
    //get all the edges within the graph and add them to the edgeList.
    //we make sure not to readd edges twice.
    for(N node: this.getGraph().values()){
      for(N node2: this.getGraph().values()){
        if(!node2.equals(node)){
          E edge = node.getConnectingEdges().get(node2.getName());
          String edgeName = edge.getName();
          //we only add unique edges so we don't add it to the graph multiple times
          if(!edgeList.containsKey(edgeName)){
            edgeList.put(edgeName,edge);
          }
        }
      }
    }

    //FIRST TEST: to see if all the edges are within the graph.
    System.out.println("This is the size of the edgeList" + edgeList.values().size());
    for(String s: edgeList.keySet()){
      System.out.println("Edge for" + s + "in set");
    }

    //take the priorityQueue
    PriorityQueue<E> pq = new PriorityQueue<E>(new TripGraphEdgeComparator<N, E>());
    //add the list of sorted edges to the priorityqueue
    for(E edge: edgeList.values()){
      //make sure to clear the graph nodes.
      for(N node: edge.getNodes()){
        node.clearGraphEdges();
      }
      pq.add(edge);
    }
    HashMap<String, E> addedEdges = new HashMap<>();
    //checks to see the elements in the pq.
    while(!pq.isEmpty()){
      E curr = pq.poll();
      //see the edge we are on.
      System.out.println("current edge is" + curr.getName());
      //if we have n-1 edges, we have a complete MST.
      if(mst.getNumEdges() == this.getGraph().values().size() - 1){
        return mst;
      }
      //add the edge to the graph otherwise
      mst.insertEdge(curr);

      //if there is a cycle because of this, we shouldn't add the node to the graph
      if(isCyclic(mst)){
        mst.deleteEdge(curr.getNodes().get(0), curr.getNodes().get(1));
      }
    }
      //if there was no minimum spanning tree: we return null, which indicates an issue.
    if(mst.getNumEdges() == this.getGraph().values().size() - 1){
      System.out.println("Broke out of the pq loop");
      return mst;
    } else{
        System.out.println("There was an issue");
      return null;
    }
  }

  /**
   * This is a helper method to test if a graph is cyclic using BFs
   * @param mst: the graph to search:
   * @param node the node we are at currently
   * @param visited the visited edge.
   * @return
   */
  public boolean isCyclicHelper(TripGraph<N,E> mst, N node, HashMap<String, Boolean> visited){
    //we have a deque for a max elements with 50: for larger graph algorithms, we may want
    //to use a different datastructure
    Deque<N> deque = new ArrayDeque<>(50);
    //the parent map
    HashMap<N, N> parent = new HashMap<>();
    for(N next: mst.getGraph().values()){
      parent.put(next, next);
    }

    //this is a list of visited nodes
    visited.put(node.getName(), true);
    System.out.println("Curr node added to visited" + node.getName());
    deque.offerLast(node);
    //we go through the deque
    while(!deque.isEmpty()){
      //find the neighbors
      N curr = deque.getLast();
      System.out.println("Curr node" + curr.getName());
      for(N neighbor: curr.getNeighbors()){
        if(!visited.containsKey(neighbor.getName())) {
          System.out.println("Neighbor node, queued" + neighbor.getName());
        } else if(visited.get(neighbor.getName()) == false) {
          System.out.println("Neighbor node, unvisited" + neighbor.getName());
          visited.put(neighbor.getName(), true);
          deque.offerLast(neighbor);
          parent.put(neighbor, curr);
        } else if(parent.containsKey(neighbor) && parent.get(neighbor).equals(curr) == false){
          System.out.println("Neighbor node, visited" + neighbor.getName() +
            "parent of neighbor" + parent.get(neighbor));
          return false;
        } else{
          System.out.println(" the parent should be null" + parent.get(neighbor));
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
      System.out.println("Nodes in the graph include" + node.getName());
      visited.put(node.getName(), false);
    }
    //if we detect a cycle in the portion of the graph, we know there is a cycle
    //in the graph
    for(N node: mst.getGraph().values()){
        System.out.println("Running helper method on" + node.getName());
        if(!visited.get(node.getName()) && isCyclicHelper(mst, node, visited)){
          return true;
        }
    }
    return false;
  }


}
