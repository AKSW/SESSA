package org.aksw.sessa.helper.graph;

import org.aksw.sessa.query.models.NGramEntryPosition;

/**
 * Created by Simon Bordewisch on 07.06.17.
 */
public class Node<T extends Object> {
  private T nodeContent;


  private int explanation;
  private float energy;
  private NGramEntryPosition color;
  private boolean isFactNode;

  public Node(T nodeContent){
    this.nodeContent = nodeContent;
    this.explanation = 0;
    this.energy = 0;
    this.color = new NGramEntryPosition(0,0); // represents no color
    this.isFactNode = false;
  }

  public Node(T nodeContent, int explanation, float energy, NGramEntryPosition color, boolean isFactNode) {
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

  public NGramEntryPosition getColor() {
    return color;
  }

  public void setColor(NGramEntryPosition color) {
    this.color = color;
  }

  public void setNodeType(boolean isFactNode){
    this.isFactNode = isFactNode;
  }

  public boolean isFactNode(){
    return isFactNode;
  }

  public boolean equals(Object other){
    if (other instanceof Node<?>){
      if ( ((Node<?>)other).getContent().equals(this.nodeContent) ){
        return true;
      }
    }
    return false;
  }
}
