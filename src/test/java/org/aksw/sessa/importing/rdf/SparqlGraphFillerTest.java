package org.aksw.sessa.importing.rdf;

import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 06.07.17.
 */
public class SparqlGraphFillerTest {

  @Test
  public void testFindMissingTripleElement_billgatesAndSeattle(){
    String bg = "http://dbpedia.org/resource/Bill_Gates";
    String seattle = "http://dbpedia.org/resource/Seattle";

    SparqlGraphFiller sgf = new SparqlGraphFiller();
    //System.out.println("SPARQL-Query is:");
    //System.out.println(sgf.buildQuery(seattle, bg).replace("UNION", "UNION\n").replace("{{", "{\n{"));
    Set<String> resultSet = sgf.findMissingTripleElement(seattle, bg);

    Assert.assertTrue(resultSet.contains("http://dbpedia.org/resource/Seattle"));
  }
}
