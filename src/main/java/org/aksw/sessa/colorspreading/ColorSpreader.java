package org.aksw.sessa.colorspreading;


import java.util.Map.Entry;
import java.util.Set;
import java.util.Map;
import org.aksw.sessa.helper.graph.Graph;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.query.models.NGramEntryPosition;

/**
 * This class's main purpose is to color the given reified graph and
 * calculate the explanation and energy scores for each node.
 * @author Simon Bordewisch
 */
public class ColorSpreader {


  private Graph graph;

  /**
   * Initializing with the reified graph.
   * @param graph Graph on which the color-spreading should be applied to.
   */
  public ColorSpreader(Graph graph) {
    this.graph = graph;
  }

  /**
   * Returns Graph with updated scores and colors.
   * @return calculated graph
   */
  public Graph getGraph() {
    return graph;
  }

  /**
   * First step in color-spreading process.
   * Sets the initial explanation scores of the mapped nodes
   * @param nodeMapping provides the mapping (reverse dictionary) of n-grams to candidates
   */
  public void initialize(Map<NGramEntryPosition, Set<Node>> nodeMapping) {
    setColor(nodeMapping);
    setExplanationScore(nodeMapping);
  }

  private void setColor(Map<NGramEntryPosition, Set<Node>> nodeMapping) {
    for(Entry<NGramEntryPosition, Set<Node>> entry : nodeMapping.entrySet()){
      for(Node node : entry.getValue()){
        node.setColor(entry.getKey());
      }
    }
  }

  private void setExplanationScore(Map<NGramEntryPosition, Set<Node>> nodeMapping) {
    for (Entry<NGramEntryPosition, Set<Node>> entry : nodeMapping.entrySet()) {
      for (Node node : entry.getValue()) {
        int explanationScore = entry.getKey().getLength();
        node.setExplanation(explanationScore);
      }
    }
  }


}
