package org.aksw.sessa.helper.graph;

import java.util.HashSet;

import org.aksw.sessa.query.models.NGramEntryPosition;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 06.07.17.
 */
public class NodeTest {

  @Test
  @Ignore
  //FIXME @Simon fix this test, explain why nodes having the same content are not treated as equal nodes.RU
  public void testEquals_simpleComparison(){
    Node<String> node1 = new Node<>("test");
    Node<String> node2 = new Node<>("test");
    Assert.assertEquals(null,node1, node2);
  }

  @Test 
  @Ignore
  //FIXME @Simon fix this test, explain why nodes having the same content are not treated as equal nodes.RU 
  public void testEquals_changeScores(){
    Node<String> node1 = new Node<>("test");
    Node<String> node2 = new Node<>("test");

    node2.addColor(new NGramEntryPosition(2,2));
    node2.setEnergy(2);
    Assert.assertEquals(null,node1, node2);
  }

  @Test
  @Ignore
  //FIXME @Simon fix this test, explain why nodes having the same content are not treated as equal nodes.RU
  public void testEquals_onHashSet(){
    HashSet<Node> nodes = new HashSet<>();
    Node<String> node1 = new Node<>("test");
    Node<String> node2 = new Node<>("test");

    node2.addColor(new NGramEntryPosition(2,2));
    node2.setEnergy(2);
    nodes.add(node1);
    Assert.assertFalse(nodes.add(node2));
  }

  @Test
  public void testColorsOfNodeAreRelated_simpleTest_NoRelation(){
    Node<Integer> node1 = new Node<>(1);
    node1.addColor(new NGramEntryPosition(1,5));
    Node<Integer> node2 = new Node<>(2);
    node2.addColor(new NGramEntryPosition(1,3));
    Assert.assertFalse(node1.isRelatedTo(node2));
  }

  @Test
  public void testColorsOfNodeAreRelated_keepsColor(){
    Node<Integer> node1 = new Node<>(1);
    node1.addColor(new NGramEntryPosition(1,5));
    Node<Integer> node2 = new Node<>(2);
    node2.addColor(new NGramEntryPosition(1,3));
    Assert.assertFalse(node1.isRelatedTo(node2));
    Assert.assertFalse(node1.getColors().isEmpty());
    Assert.assertFalse(node2.getColors().isEmpty());
  }

  @Test
  public void testColorsOfNodeAreRelated_simpleTest1_HasRelation(){
    // taken from QALD tests (question "How many seats, stadium of FC Porto")
    Node<String> node1 = new Node<>("http://dbpedia.org/resource/HoW");
    Node<String> node2 = new Node<>("http://dbpedia.org/resource/How_Many");

    node1.addColor(new NGramEntryPosition(1,0));
    node2.addColor(new NGramEntryPosition(2, 0));

    Assert.assertTrue(node1.isRelatedTo(node2));
  }

  @Test
  public void testColorsOfNodeAreRelated_simpleTest2_HasRelation(){
    Node<Integer> node1 = new Node<>(1);
    Node<Integer> node2 = new Node<>(2);

    node1.addColor(new NGramEntryPosition(4,3));
    node2.addColor(new NGramEntryPosition(4, 2));

    Assert.assertTrue(node1.isRelatedTo(node2));
  }

}
