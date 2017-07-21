package org.aksw.sessa.colorspreading;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Map;
import org.aksw.sessa.helper.graph.Graph;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.helper.graph.SelfBuildingGraph;
import org.aksw.sessa.query.models.NGramEntryPosition;

/**
 * This class's main purpose is to color the given reified graph and
 * calculate the explanation and energy scores for each node.
 *
 * @author Simon Bordewisch
 */
public class ColorSpreader {


  private GraphInterface graph;
  private Set<Node> lastActivatedNodes;
  private Set<Node> activatedNodes;
  private Set<Node> resultNodes;
  private int bestExplanation;

  /**
   * Constructs the initial graph in colorspreader with the given candidate mapping.
   * @param nodeMapping provides the mapping (reverse dictionary) of n-grams to candidates
   */
  public ColorSpreader(Map<NGramEntryPosition, Set<String>> nodeMapping){
    lastActivatedNodes = new HashSet<>();
    activatedNodes = new HashSet<>(lastActivatedNodes);
    resultNodes = new HashSet<>();
    bestExplanation = 0;
    graph = new SelfBuildingGraph();
    initialize(nodeMapping);
  }


  /**
   * First step in color-spreading process.
   * Sets the initial explanation scores of the mapped nodes
   *
   * @param nodeMapping provides the mapping (reverse dictionary) of n-grams to candidates
   */
  private void initialize(Map<NGramEntryPosition, Set<String>> nodeMapping) {
    for (Entry<NGramEntryPosition, Set<String>> entry : nodeMapping.entrySet()) {
      for (String value : entry.getValue()) {
        Node<String> node = new Node<>(value);
        node.addColor(entry.getKey());
        int explanationScore = entry.getKey().getLength();
        node.setExplanation(explanationScore);
        node.setEnergy(1); // TODO: set actual energy with some metric
        lastActivatedNodes.add(node);
        graph.addNode(node);
      }
    }
    activatedNodes.addAll(lastActivatedNodes);
    updateResult();
  }

  /**
   * Updates result set, which consists of all node in the graph with the highest explanation
   * score.
   */
  private void updateResult() {
    // TODO: Handle fact nodes?
    // TODO: Handle energy score?
    for (Node node : lastActivatedNodes) {
      if (!node.isFactNode()) {
        if (resultNodes.isEmpty()) {
          resultNodes.add(node);
          bestExplanation = node.getExplanation();
        } else {
          if (node.getExplanation() >= bestExplanation) {
            if (node.getExplanation() > bestExplanation) {
              resultNodes.clear();
              bestExplanation = node.getExplanation();
            }
            resultNodes.add(node);
          }
        }
      }
    }
  }

  /**
   * Returns result set, containing all nodes with the highest explanation score.
   */
  public Set<Node> getResult() {
    return resultNodes;
  }

  /**
   * Updates the scores and colors of a given node based on their neighbours
   *
   * @param node node which should be updated
   */
  private void updateNode(Node node) {
    int energy = 0;
    int explanation = 0;
    Set<NGramEntryPosition> colors = new HashSet<>();
    for (Node neighbor : graph.getAllNeighbors(node)) {
      explanation += neighbor.getExplanation();
      energy += neighbor.getEnergy();
      colors.addAll(neighbor.getColors()); // Why is there a warning?
    }
    node.setEnergy(energy);
    node.setExplanation(explanation);
    node.addColors(colors);
  }

  /**
   * Makes one step of the spreading activation algorithm.
   * Mainly checks neighbors of nodes which where activated in the last step for the
   * activation criteria and updates their scores if they fullfill those.
   *
   * @return true if at least one node was updated (i.e. it got a new color)
   */
  public boolean makeActiviationStep() {
    Set<Node> updatedLastActivatedNodes = new HashSet<>();
    for (Node node : lastActivatedNodes) {
      Set<Node> neighbors = graph.getAllNeighbors(node);
      for (Node neighbor : neighbors) {
        boolean fullfillsMinimumActivationCriterion = true;

        /* We are considering neighbors of already activated nodes,
         * therefore only fact nodes could potentially not fullfill the
         * minimum activation criterion.
         */
        if (neighbor.isFactNode()) {
          Set<Node> neighborsOfFactNode = graph.getNeighborsLeadingFrom(neighbor);
          int countActivated = 0;
          for (Node factNodeNeigbor : neighborsOfFactNode) {
            if (factNodeNeigbor.getExplanation() > 0 && factNodeNeigbor.getEnergy() > 0) {
              countActivated++;
            }
          }
          if (countActivated < 2) {
            fullfillsMinimumActivationCriterion = false;
          }
        }
        if (fullfillsMinimumActivationCriterion &&
            colorsCanBeCombined(neighbor) &&
            !activatedNodes.contains(neighbor)) {
          updateNode(neighbor);
          updatedLastActivatedNodes.add(neighbor);
        }
      }
    }
    lastActivatedNodes = updatedLastActivatedNodes;
    activatedNodes.addAll(lastActivatedNodes);
    updateResult();
    return !lastActivatedNodes.isEmpty();

  }

  /**
   * Checks if the given node can combine the colors of the neighbors.
   *
   * @param node Node to check the criterion for
   * @return true if the colors can be combined
   */
  private boolean colorsCanBeCombined(Node node) {
    Set<NGramEntryPosition> colors = new HashSet<>();
    for (Node neighbor : graph.getAllNeighbors(node)) {
      colors.addAll(neighbor.getColors()); // why is there a warning?
    }
    for (NGramEntryPosition color : colors) {
      Set<NGramEntryPosition> intersection = new HashSet<>(colors);
      intersection.retainAll(color.getAllDescendants());
      if (!intersection.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public Set<Node> spreadColors(){
    boolean colorsHaveSpread = true;

    while(colorsHaveSpread){
      colorsHaveSpread = makeActiviationStep();
    }
    return getResult();
  }

  public GraphInterface getGraph(){
    return graph;
  }


}
