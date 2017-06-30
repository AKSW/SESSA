package org.aksw.sessa.helper.graph;

import java.util.HashSet;
import java.util.Set;
import org.aksw.sessa.query.models.NGramEntryPosition;

/**
 * This class represents a node in graph with given information of class T.
 * Furthermore it holds the scores and the colors, which are needed for the
 * colors-spreading algorithm.
 */
public class Node<T extends Object> {

  private T nodeContent;

  private int explanation;
  private float energy;
  private Set<NGramEntryPosition> colors;
  private boolean isFactNode;

  /**
   * Initializes node with the given information.
   * All scores are initialized with default parameters.
   * @param nodeContent content to be stored in the node
   */
  public Node(T nodeContent){
    this.nodeContent = nodeContent;
    this.explanation = 0;
    this.energy = 0;
    this.colors = new HashSet<>();
    this.isFactNode = false;
  }

  /**
   * Initializes node with given information and scores.
   * @param nodeContent content to be stored in the node.
   * @param explanation explanation score of the node
   * @param energy energy score of the node
   * @param color represents colors for the node
   * @param isFactNode is the given node a fact-node?
   */
  public Node(T nodeContent, int explanation, float energy, Set<NGramEntryPosition> color, boolean isFactNode) {
    this.nodeContent = nodeContent;
    this.explanation = explanation;
    this.energy = energy;
    this.colors = color;
    this.isFactNode = isFactNode;
  }

  public T getContent(){
    return nodeContent;
  }

  public int getExplanation(){
    return explanation;
  }

  public void setExplanation(int explanation) {
    this.explanation = explanation;
  }

  public float getEnergy(){
    return energy;
  }

  public void setEnergy(float newEnergy){
    energy = newEnergy;
  }

  public Set<NGramEntryPosition> getColors() {
    return colors;
  }

  public void addColor(NGramEntryPosition color) {
    this.colors.add(color);
  }

  public void addColors(Set<NGramEntryPosition> colors) {
    this.colors.addAll(colors);
  }


  /**
   * Sets the node type, i.e. if the node is a fact node (true) or not (false).
   * @param isFactNode is the given node a fact-node?
   */
  public void setNodeType(boolean isFactNode){
    this.isFactNode = isFactNode;
  }

  public boolean isFactNode(){
    return isFactNode;
  }

  @Override
  public boolean equals(Object other){
    if (other instanceof Node<?>){
      if ( ((Node<?>)other).getContent().equals(this.nodeContent) ){
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "Node{" +
        "nodeContent=" + nodeContent +
        ",\n explanation=" + explanation +
        ",\n energy=" + energy +
        ",\n colors=" + colors +
        ",\n isFactNode=" + isFactNode +
        '}';
  }
}
