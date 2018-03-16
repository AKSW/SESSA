package org.aksw.sessa.helper.graph;


import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GraphTest {

  List<Node> nodes;
  private Graph graph;

  @Before
  public void initialize() {
    graph = new Graph();
    nodes = new ArrayList<>();
    for (int i = 0; i <= 9; i++) {
      Node<Integer> node = new Node<>(i);
      nodes.add(node);
      graph.addNode(node);
    }
    // edges: 0->1, 0->2, 0->3, 1->2, 1->3, 2->3, 3->0
    graph.addEdge(nodes.get(0), nodes.get(4));
    graph.addEdge(nodes.get(1), nodes.get(4));
    graph.addEdge(nodes.get(4), nodes.get(7));
    graph.addEdge(nodes.get(2), nodes.get(7));
    graph.addEdge(nodes.get(7), nodes.get(8));
    graph.addEdge(nodes.get(3), nodes.get(8));

    graph.addEdge(nodes.get(2), nodes.get(5));
    graph.addEdge(nodes.get(3), nodes.get(5));
    graph.addEdge(nodes.get(5), nodes.get(6));
    graph.addEdge(nodes.get(1), nodes.get(6));

    graph.addEdge(nodes.get(2), nodes.get(9));
    graph.addEdge(nodes.get(1), nodes.get(9));
  }

  @Test
  public void testGetNeighborsLeadingFrom_NotEmptyTests() {
    Set<Node> neighborOf1 = new HashSet<>();
    neighborOf1.add(nodes.get(4));
    neighborOf1.add(nodes.get(6));
    neighborOf1.add(nodes.get(9));
    Assert.assertThat(graph.getNeighborsLeadingFrom(nodes.get(1)), equalTo(neighborOf1));

    Set<Node> neighborOf3 = new HashSet<>();
    neighborOf3.add(nodes.get(8));
    neighborOf3.add(nodes.get(5));
    Assert.assertThat(graph.getNeighborsLeadingFrom(nodes.get(3)), equalTo(neighborOf3));
  }

  @Test
  public void testGetNeighborsLeadingFrom_EmptyNeighbours() {
    Assert.assertThat(graph.getNeighborsLeadingFrom(nodes.get(9)), empty());
  }

  @Test
  public void testGetNeighborsLeadingTo_NotEmptyTests() {
    Set<Node> neighbor = new HashSet<>();
    neighbor.add(nodes.get(2));
    neighbor.add(nodes.get(3));
    Assert.assertThat(graph.getNeighborsLeadingTo(nodes.get(5)), equalTo(neighbor));

    Set<Node> leadsTo8 = new HashSet<>();
    leadsTo8.add(nodes.get(3));
    leadsTo8.add(nodes.get(7));
    Assert.assertThat(graph.getNeighborsLeadingTo(nodes.get(8)), equalTo(leadsTo8));
  }

  @Test
  public void testGetNeighborsLeadingTo_EmptyNeighbours() {
    Assert.assertThat(graph.getNeighborsLeadingTo(nodes.get(0)),  empty());
  }

  @Test
  public void testGetAllNeighbors_NotEmptyTests() {
    Set<Node> allNeighborsOf1 = new HashSet<>();
    allNeighborsOf1.add(nodes.get(7));
    allNeighborsOf1.add(nodes.get(1));
    allNeighborsOf1.add(nodes.get(0));

    Assert.assertEquals(allNeighborsOf1, graph.getAllNeighbors(nodes.get(4)));
  }
}
