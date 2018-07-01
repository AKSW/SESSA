package org.aksw.sessa.colorspreading;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.helper.graph.Graph;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.candidate.Candidate;
import org.aksw.sessa.helper.graph.SelfBuildingGraph;
import org.aksw.sessa.helper.graph.SelfbuildingGraphTest;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Simon Bordewisch on 15.06.17.
 */
public class ColorSpreaderTest {

  private static final Logger log = LoggerFactory.getLogger(ColorSpreaderTest.class);

  private ColorSpreader colorSpread;
  private Map<NGramEntryPosition, Set<Candidate>> nodeMapping;


  @Before
  public void init() {
    String bp = "http://dbpedia.org/ontology/birthPlace";
    String pob = "http://dbpedia.org/resource/Place_of_birth";

    String spouse = "http://dbpedia.org/ontology/spouse";
    String wife = "http://dbpedia.org/resource/Wife";
    String theWife = "http://dbpedia.org/resource/The_Wife";

    String bg = "http://dbpedia.org/resource/Bill_Gates";

    // init mapping ngram -> uri
    nodeMapping = new HashMap<>();
    NGramEntryPosition bpEntry = new NGramEntryPosition(1, 0);
    Set<Candidate> bpSet = new HashSet<>();
    bpSet.add(new Candidate(bp));
    bpSet.add(new Candidate(pob));
    nodeMapping.put(bpEntry, bpSet);

    NGramEntryPosition bgEntry = new NGramEntryPosition(2, 1);
    Set<Candidate> bgSet = new HashSet<>();
    bgSet.add(new Candidate(bg));
    nodeMapping.put(bgEntry, bgSet);

    NGramEntryPosition wifeEntry = new NGramEntryPosition(1, 3);
    Set<Candidate> wifeSet = new HashSet<>();
    wifeSet.add(new Candidate(spouse));
    wifeSet.add(new Candidate(wife));
    wifeSet.add(new Candidate(theWife));
    nodeMapping.put(wifeEntry, wifeSet);
  }

  @Test
  public void testSpreadColors_billGatesTestCase() {
    colorSpread = new ColorSpreader(nodeMapping);
    Set<Node> results = colorSpread.spreadColors();
    log.debug("{}", colorSpread.getGraph().toString());
    for (Node result : results) {
      Assert.assertThat(((String) result.getContent()), containsString("Dallas"));
    }
  }

  /**
   * Test for #35
   */
  @Test
  public void testInitialGraph_withDoubleBillGates() {
    String bg = "http://dbpedia.org/resource/Bill_Gates";
    NGramEntryPosition bgEntry = new NGramEntryPosition(2, 4);
    Set<Candidate> bgSet = new HashSet<>();
    bgSet.add(new Candidate(bg));
    nodeMapping.put(bgEntry, bgSet);
    colorSpread = new ColorSpreader(nodeMapping);
    GraphInterface graph = colorSpread.getGraph();
    System.out.println(graph.toString());
    Assert.assertThat(graph.getNodes(), hasSize(7));
  }
}