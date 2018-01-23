package org.aksw.sessa.helper.graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.aksw.sessa.query.models.NGramEntryPosition;

/**
 * This class represents a node in graph with given information of class T. Furthermore it holds the
 * scores and the colors, which are needed for the colors-spreading algorithm.
 */
public class Node<T> {

  private T nodeContent;
  private long id;

  private int explanation;
  private float energy;
  private Set<NGramEntryPosition> colors;
  private boolean isFactNode;

  /**
   * Initializes node with the given information. For example, the information can contain URIs. All
   * scores are initialized with default parameters.
   *
   * @param nodeContent content to be stored in the node
   */
  public Node(T nodeContent) {
    this.nodeContent = nodeContent;
    this.explanation = 0;
    this.energy = 0;
    this.colors = new HashSet<>();
    this.isFactNode = false;
  }

  /**
   * Initializes node with given information and scores.
   *
   * @param nodeContent content to be stored in the node.
   * @param energy energy score of the node
   * @param colors represents colors for the node
   * @param isFactNode is the given node a fact-node?
   */
  public Node(T nodeContent, float energy, Set<NGramEntryPosition> colors, boolean isFactNode) {
    this.nodeContent = nodeContent;
    this.energy = energy;
    this.colors = colors;
    this.explanation = 0;
    updateExplanation(colors);
    this.isFactNode = isFactNode;
  }

  /**
   * Returns the content of this node.
   */
  public T getContent() {
    return nodeContent;
  }

  /**
   * Returns the explanation score of this node. For explanation of this score see {@link
   * #updateExplanation(NGramEntryPosition)}}.
   */
  public int getExplanation() {
    return explanation;
  }

  /**
   * Updates the explanation score for this node.
   *
   * @param colors newly added colors, with which the explanation will be updated
   * @see #updateExplanation(NGramEntryPosition)
   */
  private void updateExplanation(Set<NGramEntryPosition> colors) {
    for (NGramEntryPosition color : colors) {
      updateExplanation(color);
    }
  }

  /**
   * Updates the explanation score for this node. The explanation score provides information on how
   * many uni-grams this node is build on. E.g. this node might hold content about Bill Gates and is
   * explained by the uni-grams "bill" & "gates" and therefore has an explanation score of 2.
   *
   * @param color newly added color, with which the explanation will be updated
   */
  private void updateExplanation(NGramEntryPosition color) {
    // length of a colors equals number of represented n-grams
    this.explanation += color.getLength();
  }

  /**
   * Returns the energy score of this node.
   *
   * @return energy score of this node
   */
  public float getEnergy() {
    return energy;
  }

  /**
   * Sets the energy score for this node. The energy score is another score that tries to explain
   * how good trustworthy the n-gram mapping to the content is. E.g. this can be realized via
   * Levenshtein distance.
   */
  public void setEnergy(float newEnergy) {
    energy = newEnergy;
  }

  /**
   * Returns the colors of this node as set.
   *
   * @return set of colors of this node
   */
  public Set<NGramEntryPosition> getColors() {
    return colors;
  }

  /**
   * Adds a color to this node. Colors are represented by n-gram-positions in the n-gram hierarchy.
   * They show which n-grams were used to explain the content of this node.
   *
   * @param color position of the n-gram in the n-gram hierarchy
   * @see NGramEntryPosition
   */
  public void addColor(NGramEntryPosition color) {
    this.colors.add(color);
    updateExplanation(color);
  }

  /**
   * Adds multiple colors to this node.
   *
   * @param colors set of n-gram positions in the n-gram hierarchy
   * @see #addColor(NGramEntryPosition)
   */
  public void addColors(Set<NGramEntryPosition> colors) {
    this.colors.addAll(colors);
    updateExplanation(colors);
  }

  /**
   * Sets the node type, i.e. if the node is a fact node (true) or not (false). Fact nodes are nodes
   * which link normal nodes with each other, showing that they belong together.
   *
   * @param isFactNode set 'true' if this node is a fact node
   */
  public void setNodeType(boolean isFactNode) {
    this.isFactNode = isFactNode;
  }

  /**
   * Returns true if this node is a fact node.
   *
   * @return true if this node is a fact node
   */
  public boolean isFactNode() {
    return isFactNode;
  }

  long getId() {
    return id;
  }

  public void newId() {
    id = new Random().nextLong();
  }

  /**
   * Checks if the color of this node and the other are overlapping. I.e. if they share a color or
   * if they share a descendant of a color.
   *
   * @param other Node to be tested for related colors
   * @return true if they are related
   */
  public boolean isOverlappingWith(Node<?> other) {
    for (NGramEntryPosition thisColor : this.getColors()) {
      for (NGramEntryPosition otherColor : other.getColors()) {
        if (thisColor.isOverlappingWith(otherColor)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean colorsAreMergeable(Set<NGramEntryPosition> otherColors) {
    for (NGramEntryPosition color : this.colors) {
      if (!color.isMergeable(otherColors)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Compares the specified object with this node for equality. Returns true if the given object is
   * also a Node and has the same content and the same id.
   *
   * @param other object to be compared for equality with this node
   * @return true if the specified object is equal to this node
   */
  @Override
  public boolean equals(Object other) {
    if (other instanceof Node<?>) {
      if (((Node<?>) other).getContent().equals(this.nodeContent) &&
          ((Node<?>) other).getId() == this.getId()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the hash code of this node. The hash code of this node is the hashcode of its content.
   *
   * @return the hash code value for this node
   */
  @Override
  public int hashCode() {
    return nodeContent.hashCode();
  }

  /**
   * Returns a string representation of this node. It has the following scheme ('#var' represent the
   * variables): Node{nodeContent=#nodeContent, explanation=#explanation, energy=#energy,
   * colors=#colors, isFactNode=#isFactNode}
   *
   * @return string representation of this node
   */
  @Override
  public String toString() {
    return "Node{" + "nodeContent=" + nodeContent + ", explanation=" + explanation + ", energy="
        + energy + ", colors=" + colors + ", isFactNode=" + isFactNode + '}';
  }
}