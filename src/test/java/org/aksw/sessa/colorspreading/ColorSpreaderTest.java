package org.aksw.sessa.colorspreading;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 15.06.17.
 */
public class ColorSpreaderTest {

  private ColorSpreader colorSpread;
  private Map<NGramEntryPosition, Set<String>> nodeMapping;


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
    Set<String> bpSet = new HashSet<>();
    bpSet.add(bp);
    bpSet.add(pob);
    nodeMapping.put(bpEntry, bpSet);

    NGramEntryPosition bgEntry = new NGramEntryPosition(2, 1);
    Set<String> bgSet = new HashSet<>();
    bgSet.add(bg);
    nodeMapping.put(bgEntry, bgSet);

    NGramEntryPosition wifeEntry = new NGramEntryPosition(1, 3);
    Set<String> wifeSet = new HashSet<>();
    wifeSet.add(spouse);
    wifeSet.add(wife);
    wifeSet.add(theWife);
    nodeMapping.put(wifeEntry, wifeSet);

    colorSpread = new ColorSpreader(nodeMapping);
  }

  @Before
  public void before() {

  }

  @Test
  public void testSpreadColors_billGatesTestCase() {
    Set<Node> results = colorSpread.spreadColors();
    for (Node result : results) {
      Assert.assertTrue(((String) result.getContent()).contains("Dallas"));
    }
  }
}