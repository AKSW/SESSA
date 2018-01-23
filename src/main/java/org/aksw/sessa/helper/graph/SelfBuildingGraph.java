package org.aksw.sessa.helper.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.importing.rdf.SparqlGraphFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a graph, that builds itself using its node content to find new nodes. This
 * is currently realized using {@link org.aksw.sessa.importing.rdf.SparqlGraphFiller}. The class
 * only searches for new nodes if every node has an explanation score.
 *
 * @author Simon Bordewisch
 */
public class SelfBuildingGraph implements GraphInterface {

  /**
   * This variable is used to define how many expansions can be made before the graph should not be
   * further expanded.
   */
  public static final int MAX_EXPANSIONS = 3;
  private int currentExpansion;
  private Map<Node, Node> nodes;

  /**
   * We only want to update the graph with new information. Therefore we store the nodes that got
   * added after the last update
   */
  private Map<Node, Node> lastNewNodes;
  private Map<Node, Set<Node>> edgeMap;
  private Map<Node, Set<Node>> reversedEdgeMap; // we need both ways (except for fact-nodes)
  private static int factIterator = 0;

  // Stores already compared key pairs so they don't get compared again
  private Map<Node, Set<Node>> comparedNodes;

  private static final Logger log = LoggerFactory.getLogger(GraphInterface.class);


  /**
   * Constructs a graph with no nodes.
   */
  public SelfBuildingGraph() {
    this(new HashSet<>());
  }

  /**
   * Constructs a graph with given nodes.
   */
  public SelfBuildingGraph(Set<Node> nodes) {
    this.nodes = new HashMap<>();
    this.lastNewNodes = new HashMap<>();
    this.edgeMap = new HashMap<>();
    this.reversedEdgeMap = new HashMap<>();
    for (Node node : nodes) {
      this.nodes.put(node, node);
      this.lastNewNodes.put(node, node);
    }
    this.comparedNodes = new HashMap<>();
    this.currentExpansion = 1;
  }


  @Override
  public void addNode(Node node) {
    nodes.put(node, node);
    lastNewNodes.put(node, node);
  }

  @Override
  public Set<Node> getNodes() {
    return nodes.keySet();
  }

  @Override
  public void addEdge(Node from, Node to) {
    //TODO: Make sure the nodes are in the graph.
    addEdge(from, to, edgeMap);
    addEdge(to, from, reversedEdgeMap);
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
    //TODO: For now expanding graph in here should be enough, but maybe search for better solution
    if (everyNodeHasColor()) {
      expandGraph();
      //TODO this is called too often, see call hierachy, possibly the same nodes get added and added again and the graph is not unique, see unit test
    }
    Set<Node> allNeighbors = getNeighborsLeadingFrom(neighborsOf);
    allNeighbors.addAll(getNeighborsLeadingTo(neighborsOf));
    return allNeighbors;
  }

  /**
   * Searches the graph to find a node that that has no colors. If it finds one, it returns false,
   * else true.
   *
   * @return true if all nodes have colors.
   */
  private boolean everyNodeHasColor() {
    boolean everyNodeHasColor = true;
    for (Node node : nodes.keySet()) {
      if (node.getColors().isEmpty()) {
        everyNodeHasColor = false;
        break;
      }
    }
    return everyNodeHasColor;
  }

  /**
   * This method tries to expand the graph by finding new nodes. It tries to find a pair of nodes
   * whose content will be used in a SPARQL-query to find a complementing content, which will be
   * used to construct the new node.
   *
   * @see SparqlGraphFiller
   */
  protected void expandGraph() {
    if (currentExpansion <= MAX_EXPANSIONS) {
      SparqlGraphFiller filler = new SparqlGraphFiller();
      Map<Node, Node> newNodes = new HashMap<>();

      // Copies of the node-sets so we can add nodes to the original ones
      Map<Node, Node> nodes = new HashMap<>(this.nodes);
      Map<Node, Node> lastNewNodes = new HashMap<>(this.lastNewNodes);

      for (Node lastNewNode : lastNewNodes.keySet()) {
        for (Node node : nodes.keySet()) {
          if ((!comparedNodes.containsKey(lastNewNode) ||
              !comparedNodes.get(lastNewNode).contains(node)) &&
              !node.isFactNode()) {

            updateComparedNodes(lastNewNode, node);

            if (!node.getColors().isEmpty() &&
                !lastNewNode.getColors().isEmpty() &&
                !node.isOverlappingWith(lastNewNode)) {
              Set<String> newContent = filler.findMissingTripleElement(
                  node.getContent().toString(),
                  lastNewNode.getContent().toString());

              for (String content : newContent) {
                Node<String> foundNode = new Node<>(content);
                log.debug("SPARQL found new node {}", foundNode.getContent());
                if (newNodes.containsKey(foundNode) || nodes.containsKey(foundNode)) {
                  if (newNodes.containsKey(foundNode)) {
                    foundNode = newNodes.get(foundNode);
                    log.debug("Node was already found this round.");
                  }
                  if (nodes.containsKey(foundNode)) {
                    foundNode = nodes.get(foundNode);
                    log.debug("Its already in the node set.");
                  }
                  if (foundNode.colorsAreMergeable(lastNewNode.getColors()) &&
                      foundNode.colorsAreMergeable(node.getColors())) {
                    log.debug("Colors are mergeable.");
                  } else {
                    log.debug("Colors are not mergeable. Creating new node in graph");
                    foundNode = new Node<>(content);
                    foundNode.newId();
                  }
                }
                foundNode.addColors(lastNewNode.getColors());
                foundNode.addColors(node.getColors());
                newNodes.put(foundNode, foundNode);
                integrateNewNode(node, lastNewNode, foundNode);
              }
            }
          }
        }
      }
      this.lastNewNodes = newNodes;
      currentExpansion++;
    }
  }

  /**
   * Keeps track on which pair nodes where already used to find new nodes. These pairs shouldn't be
   * used again.
   *
   * @param newCompared1 first node used to find a new node
   * @param newCompared2 second node used to find a new node
   */
  private void updateComparedNodes(Node newCompared1, Node newCompared2) {
    if (!comparedNodes.containsKey(newCompared1)) {
      comparedNodes.put(newCompared1, new HashSet<>());
    }
    // Get old values and update them
    Set<Node> tmp = comparedNodes.get(newCompared1);
    tmp.add(newCompared2);
    comparedNodes.put(newCompared1, tmp);

    if (!comparedNodes.containsKey(newCompared2)) {
      comparedNodes.put(newCompared2, new HashSet<>());
    }
    tmp = comparedNodes.get(newCompared2);
    tmp.add(newCompared1);
    comparedNodes.put(newCompared2, tmp);
  }

  /**
   * This method integrates a new node based on the refied graph. For each new node and the two
   * corresponding nodes, which were used, to find the new node, a fact node is created and edges
   * between the fact node and the three other nodes are drawn.
   *
   * @param node1 first node used to find the new node
   * @param node2 second node used to find the new node
   * @param newNode new node found by using the other two nodes
   */
  private void integrateNewNode(Node node1, Node node2, Node newNode) {
    Node<Integer> factNode = new Node<>(factIterator);
    factIterator++;
    factNode.setNodeType(true);
    factNode.addColors(node1.getColors());
    factNode.addColors(node1.getColors());
    addNode(factNode);
    addNode(newNode);
    addEdge(factNode, node1);
    addEdge(factNode, node2);
    addEdge(factNode, newNode);
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

}