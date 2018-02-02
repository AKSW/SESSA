package org.aksw.sessa.helper.graph;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.HashSet;
import java.util.Set;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 06.07.17.
 */
public class NodeTest {

  @Test
  public void testGetExplanation_returnsCorrectAfterAddingNewColor() {
    Node<String> node = new Node<>("test");
    node.addColor(new NGramEntryPosition(2, 2));
    node.addColor(new NGramEntryPosition(4, 8));
    Assert.assertThat(node.getExplanation(), is(2 + 4));
  }

  @Test
  public void testGetExplanation_returnsCorrectAfterAddingNewColors() {
    Node<String> node = new Node<>("test");
    Set<NGramEntryPosition> colors = new HashSet<>();
    colors.add(new NGramEntryPosition(2, 2));
    colors.add(new NGramEntryPosition(4, 8));
    node.addColors(colors);
    Assert.assertThat(node.getExplanation(), is(2 + 4));
  }

  @Test
  public void testEquals_simpleComparison() {
    Node<String> node1 = new Node<>("test");
    Node<String> node2 = new Node<>("test");
    Assert.assertEquals(node1, node2);
  }

  @Test
  public void testEquals_changeScores() {
    Node<String> node1 = new Node<>("test");
    Node<String> node2 = new Node<>("test");

    node2.addColor(new NGramEntryPosition(2, 2));
    node2.setEnergy(2);
    Assert.assertEquals(node1, node2);
  }

  @Test
  public void testEquals_onHashSet() {
    HashSet<Node> nodes = new HashSet<>();
    Node<String> node1 = new Node<>("test");
    Node<String> node2 = new Node<>("test");

    node2.addColor(new NGramEntryPosition(2, 2));
    node2.setEnergy(2);
    nodes.add(node1);
    Assert.assertFalse(nodes.add(node2));
  }

  @Test
  public void testColorsOfNodeAreRelated_simpleTest_NoRelation() {
    Node<Integer> node1 = new Node<>(1);
    node1.addColor(new NGramEntryPosition(1, 5));
    Node<Integer> node2 = new Node<>(2);
    node2.addColor(new NGramEntryPosition(1, 3));
    Assert.assertFalse(node1.isOverlappingWith(node2));
  }

  @Test
  public void testColorsOfNodeAreRelated_keepsColor() {
    Node<Integer> node1 = new Node<>(1);
    node1.addColor(new NGramEntryPosition(1, 5));
    Node<Integer> node2 = new Node<>(2);
    node2.addColor(new NGramEntryPosition(1, 3));
    Assert.assertFalse(node1.isOverlappingWith(node2));
    Assert.assertFalse(node1.getColors().isEmpty());
    Assert.assertFalse(node2.getColors().isEmpty());
  }

  @Test
  public void testColorsOfNodeAreRelated_simpleTest1_HasRelation() {
    // taken from QALD tests (question "How many seats, stadium of FC Porto")
    Node<String> node1 = new Node<>("http://dbpedia.org/resource/HoW");
    Node<String> node2 = new Node<>("http://dbpedia.org/resource/How_Many");

    node1.addColor(new NGramEntryPosition(1, 0));
    node2.addColor(new NGramEntryPosition(2, 0));

    Assert.assertTrue(node1.isOverlappingWith(node2));
  }

  @Test
  public void testColorsOfNodeAreRelated_AllPossibilities() {
    // taken from QALD tests (question "How many seats, stadium of FC Porto")
    Node<Integer> node1 = new Node<>(1);
    Node<Integer> node2 = new Node<>(2);

    node1.addColor(new NGramEntryPosition(1, 0));
    node2.addColor(new NGramEntryPosition(2, 0));

    Assert.assertTrue(node1.isOverlappingWith(node2));
  }

  @Test
  public void testColorsOfNodeAreRelated_simpleTest2_HasRelation() {
    Node<Integer> node1 = new Node<>(1);
    Node<Integer> node2 = new Node<>(2);

    node1.addColor(new NGramEntryPosition(4, 3));
    node2.addColor(new NGramEntryPosition(4, 2));

    Assert.assertTrue(node1.isOverlappingWith(node2));
  }

  @Test
  public void testGetEnergy_WithConstructor(){
    float energy = 2;
    Node<Integer> node1 = new Node<Integer>(1, energy, null, false);
    Assert.assertThat(node1.getEnergy(), equalTo(energy));
  }

  @Test
  public void testGetEnergy_WithSet(){
    float energy = 2;
    Node<Integer> node1 = new Node<Integer>(1, 5, null, false);
    node1.setEnergy(energy);
    Assert.assertThat(node1.getEnergy(), equalTo(energy));
  }


}