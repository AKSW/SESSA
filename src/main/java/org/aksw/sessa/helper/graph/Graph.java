package org.aksw.sessa.helper.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class represents a graph with nodes and edges.
 *
 * @author Simon Bordewisch
 */
public class Graph implements GraphInterface {

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
   *
   * @param nodes nodes in the graph
   * @param edgeMap represents oriented edges between nodes
   */
  public Graph(HashSet<Node> nodes, HashMap<Node, Set<Node>> edgeMap) {
    this.nodes = nodes;
    this.edgeMap = edgeMap;
    for (Entry<Node, Set<Node>> entry : edgeMap.entrySet()) {
      for (Node to : entry.getValue()) {
        addEdge(to, entry.getKey(), reversedEdgeMap);
      }
    }
  }

  @Override
  public void addNode(Node node) {
    nodes.add(node);
  }

  @Override
  public void addEdge(Node from, Node to) {
    //TODO: Make sure the nodes are in the graph.
    addEdge(from, to, edgeMap);
    addEdge(to, from, reversedEdgeMap);
  }

  protected void addEdge(Node from, Node to, Map<Node, Set<Node>> toMap) {
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

  @Override
  public Set<Node> getNodes() {
    return nodes;
  }

  @Override
  public Set<Node> getNeighborsLeadingFrom(Node neighborsOf) {
    Set<Node> neighbors = edgeMap.get(neighborsOf);
    if (neighbors != null) {
      return neighbors;
    } else {
      return new HashSet<>();
    }
  }

  @Override
  public Set<Node> getNeighborsLeadingTo(Node neighborsOf) {
    Set<Node> neighbors = reversedEdgeMap.get(neighborsOf);
    if (neighbors != null) {
      return neighbors;
    } else {
      return new HashSet<>();
    }
  }

  @Override
  public Set<Node> getAllNeighbors(Node neighborsOf) {
    Set<Node> allNeighbors = getNeighborsLeadingFrom(neighborsOf);
    allNeighbors.addAll(getNeighborsLeadingTo(neighborsOf));
    return allNeighbors;
  }

  /**
   * Returns a string representation of this class. The string representation consists of a list of
   * nodes and edges. Nodes are lead by the word 'Nodes:' followed by one node per line. The nodes
   * are represented by their string representation. The edges are introduced by 'Edges:' followed
   * by one edge per line. One edge consists of the content of the first node, followed by an arrow
   * '->' followed by the content of the second node.
   *
   * @return a string representation of this graph class
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Nodes:\n");
    for (Node node : nodes) {
      sb.append("\t");
      sb.append(node.toString());
      sb.append("\n");
    }
    sb.append("Edges:\n");
    for (Entry<Node, Set<Node>> entry : edgeMap.entrySet()) {
      for (Node node : entry.getValue()) {
        sb.append("\t");
        sb.append(entry.getKey().getContent().toString());
        sb.append("->");
        sb.append(node.getContent().toString());
        sb.append("\n");
      }
    }
    return sb.toString();
  }

}
