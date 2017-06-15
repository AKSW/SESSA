package org.aksw.sessa.helper.graph;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Created by Simon Bordewisch on 07.06.17.
 */
public class GraphTest {

  private Graph graph = new Graph();
  List<Node> nodes;


  @Before
  public void initialize(){
    nodes = new ArrayList<>();
    for (int i=0;i<4;i++){
      Node<Integer> node = new Node<>(i);
      nodes.add(node);
      graph.addNode(node);
    }
    // edges: 0->1, 0->2, 0->3, 1->2, 1->3, 2->3, 3->0
    graph.addEdge(nodes.get(0), nodes.get(1));
    graph.addEdge(nodes.get(0), nodes.get(2));
    graph.addEdge(nodes.get(0), nodes.get(3));

    graph.addEdge(nodes.get(1), nodes.get(2));
    graph.addEdge(nodes.get(1), nodes.get(3));

    graph.addEdge(nodes.get(2), nodes.get(3));

    graph.addEdge(nodes.get(3), nodes.get(0));
  }

  @Test
  public void testGetNeighbors() {
    Set<Node> neighborOf1 = new HashSet<>();
    neighborOf1.add(nodes.get(2));
    neighborOf1.add(nodes.get(3));
    Assert.assertEquals(neighborOf1,graph.getNeighbors(nodes.get(1)));

    Set<Node> neighborOf3 = new HashSet<>();
    neighborOf3.add(nodes.get(0));
    Assert.assertEquals(neighborOf3,graph.getNeighbors(nodes.get(3)));
  }

}
