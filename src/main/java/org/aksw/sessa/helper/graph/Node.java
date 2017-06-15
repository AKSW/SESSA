package org.aksw.sessa.helper.graph;

import org.aksw.sessa.helper.graph.NodeInterface;

/**
 * Created by Simon Bordewisch on 07.06.17.
 */
public class Node<T> {
  private T nodeContent;


  private int explanation;
  private float energy;
  private int color;
  private boolean isFactNode;

  public Node(T nodeContent){
    this.nodeContent = nodeContent;
    this.explanation = 0;
    this.energy = 0;
    this.color = -1; // -1 is no color
    this.isFactNode = false;
  }

  public Node(T nodeContent, int explanation, float energy, int color, boolean isFactNode) {
    this.nodeContent = nodeContent;
    this.explanation = explanation;
    this.energy = energy;
    this.color = color;
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

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public void setNodeType(boolean isFactNode){
    this.isFactNode = isFactNode;
  }

  public boolean isFactNode(){
    return isFactNode;
  }
}
