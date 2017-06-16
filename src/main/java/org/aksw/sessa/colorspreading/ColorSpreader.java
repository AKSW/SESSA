package org.aksw.sessa.colorspreading;


import java.util.Map.Entry;
import java.util.Set;
import java.util.Map;
import org.aksw.sessa.helper.graph.Graph;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.query.models.NGramEntryPosition;

/**
 * Created by Simon Bordewisch on 13.06.17.
 */
public class ColorSpreader {

  Graph graph;

  public ColorSpreader(Graph graph) {
    this.graph = graph;
  }

  public Graph getGraph() {
    return graph;
  }

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
