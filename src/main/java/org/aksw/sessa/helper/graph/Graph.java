package org.aksw.sessa.helper.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Simon Bordewisch on 07.06.17.
 */
public class Graph {

  private Set<Node> nodes;
  private Map<Node, Set<Node>> edgeMap;
  private Map<Node, Set<Node>> reversedEdgeMap; // we need both ways (beside for fact-nodes)

  public Graph() {
    nodes = new HashSet<>();
    edgeMap = new HashMap<>();
    reversedEdgeMap = new HashMap<>();
  }

  public Graph(HashSet<Node> nodes, HashMap<Node, Set<Node>> edgeMap) {
    this.nodes = nodes;
    this.edgeMap = edgeMap;
  }

  public void addNode(Node node) {
    nodes.add(node);
  }

  public void addEdge(Node from, Node to) {
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

  public Set<Node> getNeighbors(Node neighborsOf) {
    return edgeMap.get(neighborsOf);
  }


}
