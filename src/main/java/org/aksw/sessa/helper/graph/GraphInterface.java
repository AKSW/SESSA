package org.aksw.sessa.helper.graph;

import java.util.Set;

/**
 * Created by Simon Bordewisch on 05.07.17.
 */
public interface GraphInterface {

  void addNode(Node node);
  Set<Node> getNodes();
  void addEdge(Node from, Node to);
  Set<Node> getNeighborsLeadingFrom(Node neighborsOf);
  Set<Node> getNeighborsLeadingTo(Node neighborsOf);
  Set<Node> getAllNeighbors(Node neighborsOf);

}
