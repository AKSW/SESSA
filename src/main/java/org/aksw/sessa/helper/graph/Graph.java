package org.aksw.sessa.helper.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a graph with nodes and edges.
 * @author Simon Bordewisch
 */
public class Graph {

  private Set<Node> nodes;
  private Map<Node, Set<Node>> edgeMap;
  private Map<Node, Set<Node>> reversedEdgeMap; // we need both ways (besides for fact-nodes)

  /**
   * Initialized empty graph.
   */
  public Graph() {
    nodes = new HashSet<>();
    edgeMap = new HashMap<>();
    reversedEdgeMap = new HashMap<>();
  }

  /**
   * Initializes graph with already defined nodes and edges.
   * @param nodes nodes in the graph
   * @param edgeMap represents oriented edges between nodes
   */
  public Graph(HashSet<Node> nodes, HashMap<Node, Set<Node>> edgeMap) {
    this.nodes = nodes;
    this.edgeMap = edgeMap;
  }

  /**
   * Adds given node to the graph.
   * @param node node to be added to the graph
   */
  public void addNode(Node node) {
    nodes.add(node);
  }

  /**
   * Add oriented edge between two nodes.
   * @param from node from which the edge originates
   * @param to node to which the edge leads to
   */
  public void addEdge(Node from, Node to) {
    //TODO: Make sure the nodes are in the graph.
    addEdge(from, to, edgeMap);
    addEdge(from, to, reversedEdgeMap);
  }

  private void addEdge(Node from, Node to, Map<Node, Set<Node>> toMap){
    if (toMap.containsKey(from)) {
      Set<Node> neighbors = edgeMap.get(from);
      neighbors.add(to);
      toMap.put(from, neighbors);
    } else {
      Set<Node> neighbors = new HashSet<>();
      neighbors.add(to);
      toMap.put(from, neighbors);
    }
  }


  public Set<Node> getNodes() {
    return nodes;
  }

  /**
   * Returns neighbors of a node, i.e. all nodes,
   * which share a edge with given node (edge has to originate from given node).
   * @param neighborsOf node from which the neighbours should be found for
   * @return neighbors of given node.
   */
  public Set<Node> getNeighbors(Node neighborsOf) {
    return edgeMap.get(neighborsOf);
  }


}
