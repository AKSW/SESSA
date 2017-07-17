package org.aksw.sessa.helper.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.importing.rdf.SparqlGraphFiller;

/**
 * Created by Simon Bordewisch on 04.07.17.
 */
public class SelfBuildingGraph implements GraphInterface{

  public static final int MAX_SPARQL_ITERATIONS = 3;
  private int currentIteration;
  private Set<Node> nodes;

  /**
   * We only want to update the graph with new information.
   * Therefore we store the nodes that got added after the last update
   */
  private Set<Node> lastNewNodes;
  private Map<Node, Set<Node>> edgeMap;
  private Map<Node, Set<Node>> reversedEdgeMap; // we need both ways (besides for fact-nodes)
  private static int factIteratator = 0;

  // Stores already compared key pairs so they don't get compared again
  private HashMap<Node,Set<Node>> comparedNodes;


  public SelfBuildingGraph() {
    this(new HashSet<>());
  }

  public SelfBuildingGraph(Set<Node> nodes){
    this.nodes = nodes;
    this.edgeMap = new HashMap<>();
    this.reversedEdgeMap = new HashMap<>();
    this.lastNewNodes = new HashSet<>(nodes);
    this.comparedNodes = new HashMap<>();
    this.currentIteration = 1;
  }


  public void addNode(Node node){
    nodes.add(node);
    lastNewNodes.add(node);
  }

  public Set<Node> getNodes() {
    return nodes;
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

  public void updateGraph(){
    if(currentIteration <= MAX_SPARQL_ITERATIONS) {
      SparqlGraphFiller sgf = new SparqlGraphFiller();
      Set<Node> newNodes = new HashSet<>();

      // Copies of the node-sets so we can add nodes to the original ones
      Set<Node> nodes = new HashSet<>(this.nodes);
      Set<Node> lastNewNodes = new HashSet<>(this.lastNewNodes);

      for (Node lastNewNode : lastNewNodes) {
        for (Node node : nodes) {
          if ((!comparedNodes.containsKey(lastNewNode) ||
              !comparedNodes.get(lastNewNode).contains(node)) &&
              !node.isFactNode()) {

            updateComparedNodes(lastNewNode, node);

            if (!node.getColors().isEmpty() &&
                !lastNewNode.getColors().isEmpty() &&
                !node.colorsOfNodeAreRelated(lastNewNode)) {
              Set<String> newContent = sgf.findMissingTripleElement(
                  node.getContent().toString(),
                  lastNewNode.getContent().toString());

              for (String content : newContent) {
                Node<String> foundNode = new Node<>(content);
                newNodes.add(foundNode);
                updateGraph(node, lastNewNode, foundNode);
              }
            }
          }
        }
      }
      this.lastNewNodes = newNodes;
    }
  }

  private void updateComparedNodes(Node newCompared1, Node newCompared2) {
    if(!comparedNodes.containsKey(newCompared1)) {
      comparedNodes.put(newCompared1, new HashSet<>());
    }
    // Get old values and update them
    Set<Node> tmp = comparedNodes.get(newCompared1);
    tmp.add(newCompared2);
    comparedNodes.put(newCompared1, tmp);

    if(!comparedNodes.containsKey(newCompared2)) {
      comparedNodes.put(newCompared2, new HashSet<>());
    }
    tmp = comparedNodes.get(newCompared2);
    tmp.add(newCompared1);
    comparedNodes.put(newCompared2, tmp);
  }

  private void updateGraph(Node node1, Node node2, Node newNode){
    if(!nodes.contains(newNode)) {
      Node<Integer> factNode = new Node<>(factIteratator);
      factIteratator++;
      factNode.setNodeType(true);
      addNode(factNode);
      addNode(newNode);
      addEdge(factNode, node1);
      addEdge(factNode, node2);
      addEdge(factNode, newNode);
    }
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Nodes:\n");
    for(Node node: nodes){
      sb.append(node.toString());
      sb.append("\n");
    }
    sb.append("Edges:\n");
    for(Entry<Node, Set<Node>> entry : edgeMap.entrySet()){
      for(Node node : entry.getValue()){
        sb.append(entry.getKey().getContent().toString());
        sb.append("->");
        sb.append(node.getContent().toString());
        sb.append("\n");
      }
    }
    return sb.toString();
  }

}
