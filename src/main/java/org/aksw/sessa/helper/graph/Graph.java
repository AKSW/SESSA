package org.aksw.sessa.helper.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class represents a graph with nodes and edges.
 * @author Simon Bordewisch
 */
public class Graph implements GraphInterface{

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
    for(Entry<Node, Set<Node>> entry : edgeMap.entrySet()){
      for(Node to : entry.getValue()){
        addEdge(to, entry.getKey(), reversedEdgeMap);
      }
    }
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
    addEdge(to, from, reversedEdgeMap);
  }

  private void addEdge(Node from, Node to, Map<Node, Set<Node>> toMap){
    if (toMap.containsKey(from)) {
      Set<Node> neighbors = toMap.get(from);
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
   * which share an edge with the given node and the edge has to originate from
   * the given node.
   * @param neighborsOf node from which the neighbours should be found for
   * @return neighbors of given node.
   */
  public Set<Node> getNeighborsLeadingFrom(Node neighborsOf) {
    Set<Node> neighbors = edgeMap.get(neighborsOf);
    if(neighbors !=null){
      return neighbors;
    } else {
      return new HashSet<>();
    }
  }

  /**
   * Returns neighbors of a node, i.e. all nodes,
   * which share an edge with the given node and the edge has to lead to
   * the given node.
   * @param neighborsOf node from which the neighbours should be found for
   * @return neighbors of given node.
   */
  public Set<Node> getNeighborsLeadingTo(Node neighborsOf){
    Set<Node> neighbors = reversedEdgeMap.get(neighborsOf);
    if(neighbors !=null){
      return neighbors;
    } else {
      return new HashSet<>();
    }
  }

  /**
   * Returns neighbors of a given node, i.e. all nodes
   * for which an edge either leads to or originates from the given node.
   * Equivallent to the neighbors of a unoriented version of the graph.
   */
  public Set<Node> getAllNeighbors(Node neighborsOf){
    Set<Node> allNeighbors = getNeighborsLeadingFrom(neighborsOf);
    allNeighbors.addAll(getNeighborsLeadingTo(neighborsOf));
    return allNeighbors;
  }

}
