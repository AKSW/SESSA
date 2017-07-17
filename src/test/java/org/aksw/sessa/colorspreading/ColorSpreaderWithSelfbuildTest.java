package org.aksw.sessa.colorspreading;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.helper.graph.SelfBuildingGraph;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 15.06.17.
 */
public class ColorSpreaderWithSelfbuildTest {

  private ColorSpreader colorspread;
  private Map<NGramEntryPosition, Set<Node>> nodeMapping;

  private Node<String> bp;
  private Node<String> pob;

  private Node<String> bg;

  private Node<String> spouse;
  private Node<String> wife;
  private Node<String> theWife;

  private Node<String> seattle;
  private Node<String> dallas;

  private Node<String> mg;
  private Node<Integer> fact1;
  private Node<Integer> fact2;
  private Node<Integer> fact3;
  SelfBuildingGraph graph;


  @Before
  public void init() {
    graph = new SelfBuildingGraph();
    bp = new Node<String>("http://dbpedia.org/ontology/birthPlace");
    pob = new Node<>("http://dbpedia.org/resource/Place_of_birth");

    spouse = new Node<String>("http://dbpedia.org/ontology/spouse");
    wife = new Node<>("http://dbpedia.org/resource/Wife");
    theWife = new Node<>("http://dbpedia.org/resource/The_Wife");

    bg = new Node<String>("http://dbpedia.org/resource/Bill_Gates");

    graph.addNode(bp);
    graph.addNode(pob);

    graph.addNode(bg);

    graph.addNode(spouse);
    graph.addNode(wife);
    graph.addNode(theWife);

    // init mapping ngram -> uri
    nodeMapping = new HashMap<>();
    NGramEntryPosition bpEntry = new NGramEntryPosition(1, 0);
    Set<Node> bpSet = new HashSet<>();
    bpSet.add(bp);
    bpSet.add(pob);
    nodeMapping.put(bpEntry, bpSet);

    NGramEntryPosition bgEntry = new NGramEntryPosition(2, 1);
    Set<Node> bgSet = new HashSet<>();
    bgSet.add(bg);
    nodeMapping.put(bgEntry, bgSet);

    NGramEntryPosition wifeEntry = new NGramEntryPosition(1, 3);
    Set<Node> wifeSet = new HashSet<>();
    wifeSet.add(spouse);
    wifeSet.add(wife);
    wifeSet.add(theWife);
    nodeMapping.put(wifeEntry, wifeSet);

    colorspread = new ColorSpreader(graph);
  }

  @Test
  public void testInitialize_TestColors() {
    colorspread.initialize(nodeMapping);
    Assert.assertTrue(!bg.getColors().isEmpty());
    Assert.assertTrue(!bp.getColors().isEmpty());
    Assert.assertTrue(!pob.getColors().isEmpty());
    Assert.assertTrue(!wife.getColors().isEmpty());
    Assert.assertTrue(!theWife.getColors().isEmpty());
    Assert.assertTrue(!spouse.getColors().isEmpty());
    System.out.println(graph.toString());
    System.out.println(graph.toString());
  }

  @Test
  public void testMakeActiviationStep_Make4StepsAndGetResults() {
    colorspread.initialize(nodeMapping);
    graph.updateGraph();
    colorspread.makeActiviationStep();
    colorspread.makeActiviationStep();
    graph.updateGraph();
    colorspread.makeActiviationStep();
    colorspread.makeActiviationStep();
    Set<Node> results = colorspread.getResult();
    for (Node result : results){
      Assert.assertTrue(((String)result.getContent()).contains("Dallas"));
    }
  }
}
