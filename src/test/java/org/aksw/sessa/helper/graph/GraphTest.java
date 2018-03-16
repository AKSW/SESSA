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

  private List<Node> nodes;
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

  @Test
  public void testFindPathsToNodes_for8() {
    Set<Node> nodesToSearchFor = new HashSet<>();
    nodesToSearchFor.add(nodes.get(8));

    Set<Node> referenceNodes = new HashSet<>();
    referenceNodes.add(nodes.get(8));
    referenceNodes.add(nodes.get(7));
    referenceNodes.add(nodes.get(2));
    referenceNodes.add(nodes.get(3));
    referenceNodes.add(nodes.get(4));
    referenceNodes.add(nodes.get(0));
    referenceNodes.add(nodes.get(1));
    GraphInterface paths = graph.findPathsToNodes(nodesToSearchFor);
    Assert.assertThat(paths.getNodes(), equalTo(referenceNodes));
  }

  @Test
  public void testFindPathsToNodes_for6() {
    Set<Node> nodesToSearchFor = new HashSet<>();
    nodesToSearchFor.add(nodes.get(6));
    GraphInterface paths = graph.findPathsToNodes(nodesToSearchFor);

    GraphInterface referenceGraph = new Graph();
    referenceGraph.addNode(nodes.get(1));
    referenceGraph.addNode(nodes.get(2));
    referenceGraph.addNode(nodes.get(3));
    referenceGraph.addNode(nodes.get(5));
    referenceGraph.addNode(nodes.get(6));

    referenceGraph.addEdge(nodes.get(1), nodes.get(6));
    referenceGraph.addEdge(nodes.get(2), nodes.get(5));
    referenceGraph.addEdge(nodes.get(3), nodes.get(5));
    referenceGraph.addEdge(nodes.get(5), nodes.get(6));


    Assert.assertThat(paths, equalTo(referenceGraph));
  }


  @Test
  public void testFindPathsToNodes_WithNonExistentNode() {
    Set<Node> nodesToSearchFor = new HashSet<>();
    nodesToSearchFor.add(new Node<Integer>(20));
    GraphInterface paths = graph.findPathsToNodes(nodesToSearchFor);
    Assert.assertThat(paths.getNodes(), empty());
  }
}
