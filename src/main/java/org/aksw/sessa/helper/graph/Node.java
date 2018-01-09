package org.aksw.sessa.helper.graph;

import java.util.HashSet;
import java.util.Set;
import org.aksw.sessa.query.models.NGramEntryPosition;

/**
 * This class represents a node in graph with given information of class T. Furthermore it holds the
 * scores and the colors, which are needed for the colors-spreading algorithm.
 */
public class Node<T extends Object> {

  private T nodeContent;

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
  public void updateExplanation(Set<NGramEntryPosition> colors) {
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
  public void updateExplanation(NGramEntryPosition color) {
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

  /**
   * Checks if the color of this node and the other are related. I.e. if they share a color or if
   * they share a decendant of a color.
   *
   * @param other Node to be tested for related colors
   * @return true if they are related
   *
   * BUG https://stackoverflow.com/questions/16500240/exception-in- thread-main-java-util-concurrentmodificationexception
   * FIXME @Simon if more than a level one descendant should be checked, rewrite this to recursion
   * For now this method only checks level one descendants
   *
   * Set<NGramEntryPosition> otherColors = new HashSet<>(other.getColors()); for (NGramEntryPosition
   * color : otherColors) { otherColors.addAll(color.getAllDescendants()); } Set<NGramEntryPosition>
   * colors = new HashSet<>(this.getColors()); for (NGramEntryPosition color : colors) {
   * colors.addAll(color.getAllDescendants()); }
   */
  public boolean isRelatedTo(Node other) {
    if (this == other) {
      return true;
    }

    // Get all descendants of colors of the other node
    Set<NGramEntryPosition> otherColors = new HashSet<>();
    for (NGramEntryPosition color : new HashSet<NGramEntryPosition>(other.getColors())) {
      otherColors.addAll(color.getAllDescendants());
    }
    otherColors.addAll(other.getColors());

    // Get all decendants of colors of this node
    Set<NGramEntryPosition> colors = new HashSet<>();
    for (NGramEntryPosition color : new HashSet<>(this.getColors())) {
      colors.addAll(color.getAllDescendants());
    }
    colors.addAll(this.getColors());

    // Get intersection, if empty, they are not related
    otherColors.retainAll(colors);
    return !otherColors.isEmpty();
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Node<?>) {
      if (((Node<?>) other).getContent().equals(this.nodeContent)) {
        return true;
      }
    }
    return false;
  }


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