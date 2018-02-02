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

  List<Node> nodes;
  private Graph graph;

  @Before
  public void initialize() {
    graph = new Graph();
    nodes = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
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
  public void testGetNeighborsLeadingFrom_NotEmptyTests() {
    Set<Node> neighborOf1 = new HashSet<>();
    neighborOf1.add(nodes.get(2));
    neighborOf1.add(nodes.get(3));
    Assert.assertEquals(neighborOf1, graph.getNeighborsLeadingFrom(nodes.get(1)));

    Set<Node> neighborOf3 = new HashSet<>();
    neighborOf3.add(nodes.get(0));
    Assert.assertEquals(neighborOf3, graph.getNeighborsLeadingFrom(nodes.get(3)));
  }

  @Test
  public void testGetNeighborsLeadingFrom_EmptyNeighbours() {
    Assert.assertEquals(new HashSet<Node>(), graph.getNeighborsLeadingFrom(nodes.get(4)));
  }

  @Test
  public void testGetNeighborsLeadingTo_NotEmptyTests() {
    Set<Node> neighbor = new HashSet<>();
    neighbor.add(nodes.get(0));
    neighbor.add(nodes.get(1));
    Assert.assertEquals(neighbor, graph.getNeighborsLeadingTo(nodes.get(2)));

    Set<Node> leadsTo0 = new HashSet<>();
    leadsTo0.add(nodes.get(3));
    Assert.assertEquals(leadsTo0, graph.getNeighborsLeadingTo(nodes.get(0)));
  }

  @Test
  public void testGetNeighborsLeadingTo_EmptyNeighbours() {
    Assert.assertEquals(new HashSet<Node>(), graph.getNeighborsLeadingTo(nodes.get(4)));
  }

  @Test
  public void testGetAllNeighbors_NotEmptyTests() {
    Set<Node> allNeighborsOf1 = new HashSet<>();
    allNeighborsOf1.add(nodes.get(0));
    allNeighborsOf1.add(nodes.get(2));
    allNeighborsOf1.add(nodes.get(3));

    Assert.assertEquals(allNeighborsOf1, graph.getAllNeighbors(nodes.get(1)));
  }

  @Test
  public void testGetAllNeighbors_EmptyNeighbours() {
    Assert.assertEquals(new HashSet<Node>(), graph.getNeighborsLeadingTo(nodes.get(4)));
  }
}
