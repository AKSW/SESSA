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
  private Map<NGramEntryPosition, Set<String>> nodeMapping;

  private String bp;
  private String pob;

  private String bg;
  private String spouse;
  private String wife;
  private String theWife;


  @Before
  public void init() {
    bp = "http://dbpedia.org/ontology/birthPlace";
    pob = "http://dbpedia.org/resource/Place_of_birth";

    spouse = "http://dbpedia.org/ontology/spouse";
    wife = "http://dbpedia.org/resource/Wife";
    theWife = "http://dbpedia.org/resource/The_Wife";

    bg = "http://dbpedia.org/resource/Bill_Gates";

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

    colorspread = new ColorSpreader(nodeMapping);
  }

  @Test
  public void testSpreadColors_billGatesTestCase() {
    Set<Node> results = colorspread.spreadColors();
    for (Node result : results) {
      Assert.assertTrue(((String) result.getContent()).contains("Dallas"));
    }
    //System.out.println(colorspread.getGraph());
  }
}
