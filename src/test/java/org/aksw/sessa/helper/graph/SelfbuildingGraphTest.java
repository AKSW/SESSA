package org.aksw.sessa.helper.graph;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 07.06.17.
 */
public class SelfbuildingGraphTest {

  private SelfBuildingGraph graph;


  @Before
  public void initialize() {
    graph = new SelfBuildingGraph();
    graph.addNode(new Node<String>("http://dbpedia.org/ontology/birthPlace"));
    graph.addNode(new Node<String>("http://dbpedia.org/ontology/spouse"));
    graph.addNode(new Node<String>("http://dbpedia.org/resource/Bill_Gates"));
  }

  @Test
  public void testUpdateGraph(){
    graph.updateGraph();
    graph.updateGraph();
    System.out.println(graph.toString());
  }
}
