package org.aksw.sessa.helper.graph;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.graph.exception.NodeNotFoundException;

/**
 * Implementations of this interface should represent oriented graphs.
 */
public interface GraphInterface {

  /**
   * Adds a node to the graph if it is not already present.
   *
   * @param node node which should be added to the graph
   */
  void addNode(Node node);

  /**
   * Adds all given nodes to the graph if it is not already present.
   *
   * @param nodes set of nodes which should be added to the graph
   */
  void addNodes(Set<Node> nodes);

  /**
   * Returns all nodes of the graph as a set.
   *
   * @return set of all nodes in this graph
   */
  Set<Node> getNodes();

  /**
   * Checks if the given node is in the graph.
   *
   * @param node node which should be checked for existance in the graph
   * @return true if node is in the graph, false otherwise
   */
  boolean containsNode(Node node);

  /**
   * Add oriented edge between two nodes.
   *
   * @param from node from which the edge originates
   * @param to node to which the edge leads to
   */
  void addEdge(Node from, Node to);

  /**
   * Give a map of all edges, adds all edges to the graph.
   *
   * @param edges edges which should be added to the graph
   */
  void addEdges(Map<Node, Set<Node>> edges);

  /**
   * Give one entry of a edge map, i.e. a representation of one node and all its edges, where it is
   * the tail, it will add those edges.
   *
   * @param edgesFromNode edges leading from one node, which should be added to the graph
   */
  void addEdges(Entry<Node, Set<Node>> edgesFromNode);

  /**
   * Returns all edges of the graph as a map.
   *
   * @return all edges of the graph as a map
   */
  Map<Node, Set<Node>> getEdges();

  /**
   * Returns neighbors of a node, i.e. all nodes, which share an edge with the given node and the
   * edge has to originate from the given node.
   *
   * @param neighborsOf node from which the neighbours should be found for
   * @return neighbors of given node.
   */
  Set<Node> getNeighborsLeadingFrom(Node neighborsOf);

  /**
   * Returns neighbors of a node, i.e. all nodes, which share an edge with the given node and the
   * edge has to lead to the given node.
   *
   * @param neighborsOf node from which the neighbours should be found for
   * @return neighbors of given node.
   */
  Set<Node> getNeighborsLeadingTo(Node neighborsOf);

  /**
   * Returns neighbors of a given node, i.e. all nodes for which an edge either leads to or
   * originates from the given node. Equivallent to the neighbors of a unoriented version of the
   * graph.
   */
  Set<Node> getAllNeighbors(Node neighborsOf);

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
  String toString();

}
