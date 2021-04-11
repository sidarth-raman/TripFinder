package edu.brown.cs.mramesh4.TripGraph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

public class CompleteTripGraph<N extends TripGraphNode<N, E>, E extends TripGraphEdge<N, E>> {
  private HashMap<String, N> graph;

  /**
   * Empty constructor for a completeGraph
   */
  public CompleteTripGraph() {
    graph = new HashMap<>();
  }

  /**
   * Constructor for a complete graph. If all the nodes don't have edges
   * the graph will make sure that the graph is complete.
   *
   * @param nodes a list of nodes to fill the graph with
   */
  public CompleteTripGraph(List<N> nodes) {
    graph = new HashMap<>();
    for (int i = 0; i < nodes.size(); i++) {
      N node = nodes.get(i);
      String name = node.getName();
      if (!graph.containsKey(name)) {
        graph.put(name, node);
      }
    }

    //to add all the edges to each other
    for (int j = 0; j < nodes.size(); j++) {
      N node2 = nodes.get(j);
      for (N node : graph.values()) {
        String name = node.getName();
        if (!node2.equals(node)) {
          node2.insertEdges(node);
        }
        graph.put(name, node);
      }
      graph.put(node2.getName(), node2);
    }
  }

  /**
   * This takes in a graph that may not be complete and
   * returns a complete version
   *
   * @param g graph that may or may not be complete
   */
  public CompleteTripGraph(TripGraph<N, E> g) {
    graph = new HashMap<>();
    for (N node : g.getGraph().values()) {
      String name = node.getName();
      if (!graph.containsKey(name)) {
        graph.put(name, node);
      }
    }
    for (N node : this.graph.values()) {
      String name = node.getName();
      for (N node2 : this.graph.values()) {
        if (!node.equals(node2)) {
          node.insertEdges(node2);
        }
      }
      graph.put(name, node);
    }
  }

  /**
   * This is a method to run aStar on a graph.
   *
   * @param start the name of the start city
   * @param end   the name of the end city.
   * @return a list of nodes to visit
   */
  public List<N> aStar(String start, String end) {
    List<N> ret = new ArrayList<>();
    if (!graph.containsKey(start) || !graph.containsKey(end)) {
      return null;
    } else if (start.equals(end)) {
      ret.add(graph.get(start));
      return ret;
    } else {
      ret = aStarHelper(graph.get(start), graph.get(end));
      return ret;
    }
  }

  /**
   * This is a helper to run aStar within the graph that we are writing
   *
   * @param start the start node to search from
   * @param end   the end node to start from
   * @return a list of nodes
   */
  public List<N> aStarHelper(N start, N end) {
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
   *
   * @param end     the end node to search for
   * @param pq      priorityqueue to use
   * @param nodes   nodes of curr path
   * @param visited map of visited
   * @return a list of Nodes.
   */
  public List<N> aStarHelp(N end, PriorityQueue<N> pq, List<N> nodes, HashMap<String, N> visited) {
    if (!pq.isEmpty()) {
      N curr = pq.poll();
      nodes.add(curr);
      if (curr.equals(end)) {
        //return a copy in order to prevent alteration
        return new ArrayList<>(nodes);
      }
      List<N> neighbors = curr.getNeighbors();
      for (N next : neighbors) {
        if (next.equals(curr)) {
          continue;
        } else {
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
    } else {
      if (visited.containsKey(end)) {
        return nodes;
      } else {
        return null;
      }
    }
  }

  /**
   * This is used to insert a node into the graph.
   *
   * @param node the node to insert into the graph
   */
  public void insertNode(N node) {
    String nodeName = node.getName();
    if (!graph.containsKey(nodeName) ||
        (graph.containsKey(nodeName) && !graph.get(nodeName).equals(node))) {
      for (N node2 : this.graph.values()) {
        node.insertEdges(node2);
      }
      graph.put(nodeName, node);
    }
  }

  /**
   * Deletes a node from the graph
   *
   * @param node the node to delete
   */
  public void deleteNode(N node) {
    //This will take a node, loop through the graph and delete all edges between it
    String nodeName = node.getName();
    if (graph.containsKey(nodeName)) {
      graph.remove(nodeName);
      for (N node2 : this.graph.values()) {
        node2.deleteEdge(node);
      }
    }
  }

  /**
   * Accesor method for graph.
   *
   * @return a graph.
   */
  public HashMap<String, N> getGraph() {
    return graph;
  }


  /**
   * This is a 2Opt-Algorithm (At worse the cost
   * of solving this is 2*optimal cost) for the Traveling Salesman
   * Problem. This algorithm approximates the Tsp
   *
   * @param start The node to start from
   * @return a list that represents a hamlitonian cycle or null with bad inputs
   */
  public List<N> TwoOptTSP(N start) {
    if (start == null || !graph.containsKey(start.getName())) {
      return null;
    }
    TripGraph<N, E> mst = this.Kruskals();
    List<N> mstDFS = this.dfsTree(mst, start);
    return this.deleteRepeats(mstDFS);
  }

  /**
   * This is a method for the christofedes algorithm
   * @param start the start node to return to
   * @return the Hamiltonian cycle from the start node.
   */
  public List<N> christTSP(N start) {
    if (start == null || !graph.containsKey(start.getName())) {
      return null;
    }
    //generate a min cost tree
    //Step 2
    TripGraph<N, E> mst = this.Kruskals();
    //find the edges of the min-cost perfect match and add it to the mst
    mst = this.minCostMatch(mst);

    //do a eulerian tour and then find the best path using shortcuts
    List<N> ret = this.eulerTourPath(mst, start);
    return ret;
  }


  /**
   * Takes in the kruskal's tree and runs a DFS algorithm on it
   * in order to reduce the amount of nodes running.
   *
   * @param mst a dfs-searched tree.
   * @return a list of nodes
   */
  public List<N> dfsTree(TripGraph<N, E> mst, N start) {
    List<N> ret = new ArrayList<>();
    HashMap<String, Integer> visited = new HashMap<>();
    Stack<N> stack = new Stack<>();
    String name = start.getName();
    N node = mst.getGraph().get(name);
    ret.add(node);
    stack.push(node);
    visited.put(name, 1);
    while (!stack.isEmpty()) {
      N pop = stack.pop();
      ret.add(pop);
      for (E edge : pop.getOutgoingEdges()) {
        N next = edge.getNodes().get(1);
        String nextName = next.getName();
        if (!visited.containsKey(nextName)) {
          stack.push(edge.getNodes().get(1));
          visited.put(nextName, 1);
        }
      }
    }
    ret.add(node);
    return ret;
  }


  /**
   * This is Kruskal's algorithm, which we use to generate a MST
   * within the graph. Using the MST, we will do a DFS on the MST.
   *
   * @return an MST of the graph
   */
  public TripGraph<N, E> Kruskals() {
    TripGraph<N, E> mst = new TripGraph<>();
    //get the edgeList.
    HashMap<String, E> edgeList = new HashMap<>();
    //get all the edges within the graph and add them to the edgeList.
    //we make sure not to readd edges twice.
    for (N node : this.getGraph().values()) {
      for (N node2 : this.getGraph().values()) {
        if (!node2.equals(node)) {
          E edge = node.getConnectingEdges().get(node2.getName());
          if (edge != null) {
            String edgeName = edge.getName();
            String[] edgeSplit = edgeName.split("->");
            String reverseEdge = edgeSplit[1] + "->" + edgeSplit[0];
            //we only add unique edges so we don't add it to the graph multiple times
            if (!edgeList.containsKey(edgeName) && !edgeList.containsKey(reverseEdge)) {
              edgeList.put(edgeName, edge);
              edgeList.put(reverseEdge, edge);
            }
          }
        }
      }
    }

    //take the priorityQueue
    PriorityQueue<E> pq = new PriorityQueue<E>(new TripGraphEdgeComparator<N, E>());
    //add the list of sorted edges to the priorityqueue
    for (E edge : edgeList.values()) {
      //make sure to clear the graph nodes.
      for (N node : edge.getNodes()) {
        node.clearGraphEdges();
      }
      pq.add(edge);
    }
    HashMap<String, E> addedEdges = new HashMap<>();
    //checks to see the elements in the pq.
    while (!pq.isEmpty()) {
      E curr = pq.poll();


      //if we have n-1 edges, we have a complete MST.
      if (mst.getNumEdges() == this.getGraph().values().size() - 1) {
        return mst;
      }
      //add the edge to the graph otherwise
      mst.insertEdge(curr);

      //if there is a cycle because of this, we shouldn't add the node to the graph

      if (UnionFind(mst) == 1) {
        mst.deleteEdge(curr.getNodes().get(0), curr.getNodes().get(1));
      }
    }
    //if there was no minimum spanning tree: we return null, which indicates an issue.
    if (mst.getNumEdges() == this.getGraph().values().size() - 1) {
      return mst;
    } else {
      return null;
    }
  }

  /**
   * This is the union-find algorithm, which is used to detect if there is a cycle within the graph.
   * @param mst a minimum spanning tree represented as a graph
   * @return an integer representing 1 = true, 0 = false.
   */
  public int UnionFind(TripGraph<N, E> mst) {
    Collection<N> nodes = mst.getGraph().values();
    int[] parent = new int[nodes.size()];
    HashMap<N, Integer> nodesMap = new HashMap<>();
    int i = 0;
    //create a parent array for each node
    for (N node : nodes) {
      parent[i] = -1;
      nodesMap.put(node, i);
      i++;
    }
    HashSet<String> visited = new HashSet<>();

    //go through each edge
    for (N node : nodes) {
      for (E edge : node.getOutgoingEdges()) {
        String edgeName = edge.getName();
        String[] edgeSplit = edgeName.split("->");
        String reverseEdge = edgeSplit[1] + "->" + edgeSplit[0];
        if (!visited.contains(edgeName) && !visited.contains(reverseEdge)) {
          visited.add(reverseEdge);
          visited.add(edge.getName());
          //find the parent of each node and see if they matchup
          int x = this.find(parent, nodesMap.get(node));
          int y = this.find(parent, nodesMap.get(edge.getNodes().get(1)));
          if (x == y) {
            return 1;
          }
          this.Union(parent, x, y);
        }

      }
    }
    return 0;
  }

  /**
   * Used to find the parent of a given node
   * @param parent the parent array
   * @param i the index of the node
   * @return the index of the parent
   */
  public int find(int[] parent, int i) {
    if (parent[i] == -1) {
      return i;
    }
    return find(parent, parent[i]);
  }

  /**
   * Used to merge the parent array with a new parent
   * @param parent the parent array
   * @param x original index
   * @param y merge index
   */
  public void Union(int parent[], int x, int y) {
    parent[x] = y;
  }


  /**
   * This is the setup for the eulertour portion of the christofedes algorithm, in which
   * the algo takes a mincost matching and addes it to the mst
   * @param mst the mst
   * @param add a min cost match matrix
   * @param nodes the edges to add based on min cost matrix
   * @return an updated mst
   */
  public TripGraph<N, E> eulerTour(TripGraph<N, E> mst, int[][] add,
                                   HashMap<String, List<N>> nodes) {
    //goes through the nodes to add
    for (int i = 0; i < add.length; i++) {
      for (int k = 0; k < add[i].length; k++) {
        if (add[i][k] != 0) {
          String get = Integer.toString(i).concat(Integer.toString(k));
          String get2 = Integer.toString(k).concat(Integer.toString(i));
          //inserts the edge from min cost match to the graph
          if (nodes.containsKey(get)) {
            List<N> edgeAdd = nodes.get(get);
            mst.insertEdge(edgeAdd.get(0), edgeAdd.get(1));
          } else if (nodes.containsKey(get2)) {
            List<N> edgeAdd = nodes.get(get2);
            mst.insertEdge(edgeAdd.get(0), edgeAdd.get(1));
          } else {
            continue;
          }
        }
      }
    }
    //returns the mst
    return mst;
  }

  /**
   * This is the method to generate a minCostMatch
   * @param mst the minimum spanning tree
   * @return a mincostMatch of the TripGrpah
   */
  public TripGraph<N, E> minCostMatch(TripGraph<N, E> mst) {
    List<N> nodes = new ArrayList<>();
    //gets all the odd-degree nodes
    for (N node : mst.getGraph().values()) {
      if (node.getNeighbors().size() % 2 != 0) {
        nodes.add(node);
      }
    }

    HashMap<String, List<N>> costMatricesPos = new HashMap<>();
    double[][] costMatrix = new double[nodes.size()][nodes.size()];
    int start = 0;

    //create a cost matrix that maps the cost of each node pairing
    for (N node : nodes) {
      String str = Integer.toString(start);
      int pos = 0;
      for (N connector : nodes) {
        String strCopy = str.concat(Integer.toString(pos));
        List<N> matrix = new ArrayList<>();
        matrix.add(node);
        matrix.add(connector);
        if (!(connector.equals(node))) {
          costMatricesPos.put(strCopy, matrix);
          costMatrix[start][pos] = node.distanceBetween(connector);
        } else {
          costMatrix[start][pos] = Double.MAX_VALUE;
        }
        pos++;
      }
      start++;
    }

    MinMatchCostMatrix match = new MinMatchCostMatrix(costMatrix);
    //creates the optimal assignment for mincostmatches based
    int[][] assignment = match.runHungerAlgo();
    return eulerTour(mst, assignment, costMatricesPos);
  }

  /**
   * Finds a eulerian tour within a graph in faster time.
   *
   * @param mst   the minimum spanning tree to use
   * @param start a start node
   * @return a List of Nodes that comprise a Eulerian Tour
   */
  public List<N> eulerTourPath(TripGraph<N, E> mst, N start) {
    Stack<N> s = new Stack<>();
    List<N> c = new ArrayList<>();

    Set<E> edges = new HashSet<>();
    Set<E> unvisited = new HashSet<>();

    //setup visited and unvisited edges
    for (N node : mst.getGraph().values()) {
      for (E edge : node.getOutgoingEdges()) {
        edges.add(edge);
        unvisited.add(edge);
      }
    }
    //get the current start node
    N cur = start;
    s.push(cur);
    while (!s.empty()) {
      if (!incidentOnUnusedEdges(edges, unvisited, cur)) {
        c.add(0, cur);
        cur = s.pop();
      } else {
        s.push(cur);
        cur.getOutgoingEdges();
        N destination = null;
        for (E edge : cur.getOutgoingEdges()) {
          if (unvisited.contains(edge)) {
            for (N node : edge.getNodes()) {
              if (!node.equals(cur)) {
                destination = node;
              }
            }
          }
        }
        if (destination != null) {
          E edge1 = cur.getConnectingEdges().get(destination.getName());
          E edge2 = destination.getConnectingEdges().get(cur.getName());
          unvisited.remove(edge1);
          unvisited.remove(edge2);
          cur = destination;
        } else {
          System.out.println("ERROR: finding destination broken");
        }
      }
    }
    return this.deleteRepeats(c);
  }

  /**
   * This is a method to remove repeats in a tour
   * @param tour a tour to edit
   * @return a deleted duplicate tour.
   */
  private List<N> deleteRepeats(List<N> tour){
    List<N> cities = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    for(int i = 0; i < tour.size() - 1; i++){ //Minus 1 bc we don't want to remove origin
      String cityName = tour.get(i).getName();
      //System.out.println("CityName" + cityName);
      if(!visited.contains(cityName)){
        visited.add(cityName);
        cities.add(tour.get(i));
      } else{
        continue;
      }
    }
    cities.add(tour.get(0));

    return cities;
  }

  /**
   * Helper method to see if an edge is incident on a vertex
   * @param edges set of edges
   * @param unvisited set of unvisited edges
   * @param cur currentn ode
   * @return a boolean
   */
  private boolean incidentOnUnusedEdges(Set<E> edges, Set<E> unvisited, N cur) {

    for (E edge : edges) {
      if (edge.getNodes().contains(cur) && unvisited.contains(edge)) {
        return true;
      }
    }

    return false;
  }


}