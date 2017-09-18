package org.aksw.sessa.importing.rdf;

import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 06.07.17.
 * @author abddatascienceadmin
 * @author ricardousbeck
 */
public class SparqlGraphFillerTest {

  @Test
  public void testFindMissingTripleElement_billgatesAndSeattle(){
    String bg = "http://dbpedia.org/resource/Bill_Gates";
    String seattle = "http://dbpedia.org/resource/Seattle";

    SparqlGraphFiller sgf = new SparqlGraphFiller();
    Set<String> resultSet = sgf.findMissingTripleElement(seattle, bg);

    boolean contains = resultSet.contains("http://dbpedia.org/ontology/birthPlace");
	Assert.assertTrue(contains);
  }
}
