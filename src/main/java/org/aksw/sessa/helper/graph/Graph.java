package org.aksw.sessa.helper.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.graph.exception.NodeNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a graph with nodes and edges.
 *
 * @author Simon Bordewisch
 */
public class Graph implements GraphInterface {

  private static final Logger log = LoggerFactory.getLogger(GraphInterface.class);

  /**
   * Node set which maps on itself to be easily searchable and gettable.
   */
  protected Map<Node, Node> nodes;
  protected Map<Node, Set<Node>> edgeMap;
  protected Map<Node, Set<Node>> reversedEdgeMap; // we need both ways (besides for fact-nodes)

  /**
   * Initialized empty graph.
   */
  public Graph() {
    nodes = new HashMap<>();
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
    this.nodes = new HashMap<>();
    this.addNodes(nodes);
    this.edgeMap = edgeMap;
    for (Entry<Node, Set<Node>> entry : edgeMap.entrySet()) {
      for (Node to : entry.getValue()) {
        addEdge(to, entry.getKey(), reversedEdgeMap);
      }
    }
  }

  @Override
  public void addNode(Node node) {
    nodes.put(node, node);
  }

  @Override
  public void addNodes(Set<Node> nodes) {
    for (Node node : nodes) {
      addNode(node);
    }
  }

  @Override
  public boolean containsNode(Node node) {
    return nodes.containsKey(node);
  }

  @Override
  public void addEdge(Node from, Node to) {
    try {
      if (!containsNode(from)) {
        throw new NodeNotFoundException(
            "Edge cannot be added, because the given node '" + from +  "' is not in the graph.", from, this);
      }
      if (!containsNode(to)) {
        throw new NodeNotFoundException(
            "Edge cannot be added, because the given node is not in the graph.", to, this);
      }
      addEdge(from, to, edgeMap);
      addEdge(to, from, reversedEdgeMap);
    } catch (NodeNotFoundException ex) {
      log.error(ex.getLocalizedMessage(), ex);
      log.error(ex.getGraph().toString());
    }
  }

  private void addEdge(Node from, Node to, Map<Node, Set<Node>> toMap) {
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
  public void addEdges(Map<Node, Set<Node>> edges) {
    for (Entry<Node, Set<Node>> edgesFromNode : edges.entrySet()) {
      addEdges(edgesFromNode);
    }
  }

  @Override
  public void addEdges(Entry<Node, Set<Node>> edgesFromNode) {
    for (Node toNode : edgesFromNode.getValue()) {
      addEdge(edgesFromNode.getKey(), toNode);
    }
  }

  @Override
  public Set<Node> getNodes() {
    return nodes.keySet();
  }

  public Map<Node, Set<Node>> getEdges() {
    return edgeMap;
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

  @Override
  public Graph findPathsToNodes(Set<Node> nodes) {
    Graph pathsGraph = new Graph();
    for (Node node : nodes) {
      try {
        if (!this.containsNode(node)) {
          throw new NodeNotFoundException(
              "Given node '" + node.getContent().toString() + "' is not in graph.");
        }
        pathsGraph.addNode(node);
        Set<Node> neighbors = this.getNeighborsLeadingTo(node);
        for (Node neighbor : neighbors) {
          pathsGraph.addNode(neighbor);
          pathsGraph.addEdge(neighbor, node);
        }
        Graph subGraph = findPathsToNodes(neighbors);
        pathsGraph.addSubGraph(subGraph);
      } catch (NodeNotFoundException ex) {
        log.error(ex.getLocalizedMessage(), ex);
        log.error("Skipping node.");
      }
    }
    return pathsGraph;
  }

  @Override
  public void addSubGraph(GraphInterface subGraph) {
    this.addNodes(subGraph.getNodes());
    this.addEdges(subGraph.getEdges());
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
    for (Node node : nodes.keySet()) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Graph)) {
      return false;
    }

    Graph graph = (Graph) o;

    if (this.getNodes() != null ? !this.getNodes().equals(graph.getNodes()) : graph.getNodes() != null) {
      return false;
    }
    return this.getEdges() != null ? this.getEdges().equals(graph.getEdges()) : graph.getEdges() == null;
  }

  @Override
  public int hashCode() {
    int result = this.getNodes() != null ? this.getNodes().hashCode() : 0;
    result = 31 * result + (this.getEdges() != null ? this.getEdges().hashCode() : 0);
    return result;
  }
}
