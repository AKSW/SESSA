package org.aksw.sessa.helper.graph;

import java.util.HashSet;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 06.07.17.
 */
public class NodeTest {

  @Test
  public void testEquals_simpleComparison(){
    Node<String> node1 = new Node<>("test");
    Node<String> node2 = new Node<>("test");
    Assert.assertEquals(node1, node2);
  }

  @Test
  public void testEquals_changeScores(){
    Node<String> node1 = new Node<>("test");
    Node<String> node2 = new Node<>("test");

    node2.addColor(new NGramEntryPosition(1,2));
    node2.setExplanation(2);
    node2.setEnergy(2);
    Assert.assertEquals(node1, node2);
  }

  @Test
  public void test_onHashSet(){
    HashSet<Node> nodes = new HashSet<>();
    Node<String> node1 = new Node<>("test");
    Node<String> node2 = new Node<>("test");

    node2.addColor(new NGramEntryPosition(1,2));
    node2.setExplanation(2);
    node2.setEnergy(2);
    nodes.add(node1);
    Assert.assertFalse(nodes.add(node2));
  }

}
