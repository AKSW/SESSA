package org.aksw.sessa.colorspreading;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.helper.graph.SelfBuildingGraph;
import org.aksw.sessa.query.models.Candidate;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class's main purpose is to color the given reified graph and calculate the explanation and
 * energy scores for each node.
 *
 * @author Simon Bordewisch
 */
public class ColorSpreader {


  private GraphInterface graph;
  private Set<Node> lastActivatedNodes;
  private Set<Node> activatedNodes;
  private Set<Node> resultNodes;
  private int bestExplanation;
  private static final Logger log = LoggerFactory.getLogger(ColorSpreader.class);

  /**
   * Constructs the initial graph in colorspreader with the given candidate mapping.
   *
   * @param nGramMapping provides the mapping (reverse dictionary) of n-grams to candidates
   */
  public ColorSpreader(Map<NGramEntryPosition, Set<Candidate>> nGramMapping) {
    lastActivatedNodes = new HashSet<>();
    activatedNodes = new HashSet<>(lastActivatedNodes);
    resultNodes = new HashSet<>();
    bestExplanation = 0;
    graph = new SelfBuildingGraph();
    initializeWithEmptyGraph(nGramMapping);
  }

  /**
   * First step in color-spreading process. Sets the initial explanation scores of the mapped nodes
   *
   * @param nGramMapping provides the mapping (reverse dictionary) of n-grams to candidates
   */
  private void initializeWithEmptyGraph(Map<NGramEntryPosition, Set<Candidate>> nGramMapping) {
    for (Entry<NGramEntryPosition, Set<Candidate>> entry : nGramMapping.entrySet()) {
      for (Candidate candidate : entry.getValue()) {
        Node<String> node = new Node<>(candidate.getUri());
        node.addColor(entry.getKey());
        node.setEnergy(candidate.getEnergy());
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
    Set<NGramEntryPosition> colors = new HashSet<>();
    for (Node neighbor : graph.getAllNeighbors(node)) {
      energy += neighbor.getEnergy();
      colors.addAll(neighbor.getColors()); // TODO: Get rid of the warning
    }
    node.setEnergy(energy);
    node.addColors(colors);
  }

  /**
   * Makes one step of the spreading activation algorithm. Mainly checks neighbors of nodes which
   * where activated in the last step for the activation criteria and updates their scores if they
   * fulfill those.
   *
   * @return true if at least one node was updated (i.e. it got a new color)
   */
  private boolean makeActiviationStep() {
    Set<Node> updatedLastActivatedNodes = new HashSet<>();
    for (Node node : lastActivatedNodes) {
      Set<Node> neighbors = graph.getAllNeighbors(node);
      for (Node neighbor : neighbors) {
        boolean fulfillsMinimumActivationCriterion = true;

        /* We are considering neighbors of already activated nodes,
         * therefore only fact nodes could potentially not fulfill the
         * minimum activation criterion.
         */
        if (neighbor.isFactNode()) {
          Set<Node> neighborsOfFactNode = graph.getNeighborsLeadingFrom(neighbor);
          int countActivated = 0;
          for (Node factNodeNeighbor : neighborsOfFactNode) {
            if (factNodeNeighbor.getExplanation() > 0 && factNodeNeighbor.getEnergy() > 0) {
              countActivated++;
            }
          }
          if (countActivated < 2) {
            fulfillsMinimumActivationCriterion = false;
          }
        }
        if (fulfillsMinimumActivationCriterion &&
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
  @SuppressWarnings("unchecked")
  private boolean colorsCanBeCombined(Node node) {
    Set<NGramEntryPosition> colors = new HashSet<>();
    for (Node neighbor : graph.getAllNeighbors(node)) {
      colors.addAll(neighbor.getColors());
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

  /**
   * Spreads colors until there are no changes, i.e. it repeats the activation step until no node
   * was updated.
   *
   * @return the nodes with the highest explanation score
   */
  public Set<Node> spreadColors() {
    boolean colorsHaveSpread = true;
    int activationSteps = 0;
    while (colorsHaveSpread) {
      activationSteps++;
      log.debug("Starting new activation step (#{}).", activationSteps);
      log.debug("\tNumber of nodes in graph: {}", graph.getNodes().size());
      colorsHaveSpread = makeActiviationStep();
    }
    log.debug("Spreading colors completed");
    log.debug("Final number of activation steps: {}", activationSteps);

    return getResult();
  }

  /**
   * Returns build graph with all updates.
   *
   * @return used graph
   */
  public GraphInterface getGraph() {
    return graph;
  }


}