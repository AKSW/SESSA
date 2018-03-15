package org.aksw.sessa.helper.graph;

import java.util.Set;

/**
 * Implementations of this interface should represent oriented graphs.
 */
public interface GraphInterface {

  /**
   * Adds a node to the tree if it is not already present.
   *
   * @param node node which should be added to the graph
   */
  void addNode(Node node);

  /**
   * Returns all nodes of the graph as a set.
   *
   * @return set of all nodes in this graph
   */
  Set<Node> getNodes();

  /**
   * Add oriented edge between two nodes.
   *
   * @param from node from which the edge originates
   * @param to node to which the edge leads to
   */
  void addEdge(Node from, Node to);

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
