package org.aksw.sessa.colorspreading;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.helper.graph.Graph;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 15.06.17.
 */
public class ColorSpreaderTest {

  private ColorSpreader colorspread;

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


  @Before
  public void init() {
    Graph graph = new Graph();
    bp = new Node<>("dbo:birthplace");
    pob = new Node<>("dbpedia:Place_of_birth");

    bg = new Node<>("dbpedia:Bill_Gates");

    spouse = new Node<>("dbo:spouse");
    wife = new Node<>("dbpedia:Wife");
    theWife = new Node<>("dbpedia:The_Wife");

    seattle = new Node<>("dbpedia:Seattle");
    dallas = new Node<>("dbpedia:Dallas");

    mg = new Node<>("dbpedia:Melinda_Gates");

    fact1 = new Node<>(1);
    fact1.setNodeType(true);
    fact2 = new Node<>(2);
    fact2.setNodeType(true);
    fact3 = new Node<>(3);
    fact3.setNodeType(true);

    graph.addNode(bp);
    graph.addNode(pob);
    graph.addNode(bg);
    graph.addNode(spouse);
    graph.addNode(wife);
    graph.addNode(theWife);
    graph.addNode(seattle);
    graph.addNode(dallas);
    graph.addNode(mg);

    graph.addNode(fact1);
    graph.addNode(fact2);
    graph.addNode(fact3);

    graph.addEdge(fact1, bp);
    graph.addEdge(fact1, bg);
    graph.addEdge(fact1, seattle);

    graph.addEdge(fact2, bg);
    graph.addEdge(fact2, spouse);
    graph.addEdge(fact2, mg);

    graph.addEdge(fact3, mg);
    graph.addEdge(fact1, dallas);
    graph.addEdge(fact1, bp);

    // init mapping ngram -> uri
    Map<NGramEntryPosition, Set<Node>> nodeMapping = new HashMap<>();
    NGramEntryPosition bpEntry = new NGramEntryPosition(0, 1);
    Set<Node> bpSet = new HashSet<>();
    bpSet.add(bp);
    bpSet.add(pob);
    nodeMapping.put(bpEntry, bpSet);

    NGramEntryPosition bgEntry = new NGramEntryPosition(0, 2);
    Set<Node> bgSet = new HashSet<>();
    bgSet.add(bg);
    nodeMapping.put(bgEntry, bgSet);

    NGramEntryPosition wifeEntry = new NGramEntryPosition(3, 1);
    Set<Node> wifeSet = new HashSet<>();
    wifeSet.add(spouse);
    wifeSet.add(wife);
    wifeSet.add(theWife);
    nodeMapping.put(wifeEntry, wifeSet);

    colorspread = new ColorSpreader(graph);
    colorspread.initialize(nodeMapping);
  }

  @Test
  public void testInitialize_TestInitialExplanationOfNodes() {
    Assert.assertEquals(1, bp.getExplanation());
    Assert.assertEquals(1, pob.getExplanation());
    Assert.assertEquals(2, bg.getExplanation());
    Assert.assertEquals(1, spouse.getExplanation());
    Assert.assertEquals(1, wife.getExplanation());
    Assert.assertEquals(1, theWife.getExplanation());
  }

  @Test
  public void testInitialize_TestInitialColorOfNodes() {
    Assert.assertEquals(bp.getColor(), pob.getColor());

    Assert.assertEquals(spouse.getColor(), wife.getColor());
    Assert.assertEquals(wife.getColor(), theWife.getColor());

    Assert.assertNotEquals(bp.getColor(), bg.getColor());
    Assert.assertNotEquals(bp.getColor(), spouse.getColor());
  }
}
