package org.aksw.sessa.importing.rdf;

import static org.hamcrest.core.Is.is;

import org.junit.Assert;
import org.junit.Test;

public class DbpediaSparqlQueryTest {


  @Test
  public void testAskQuery(){
    String sub = "http://dbpedia.org/resource/The_Lion_King_(musical)";
    String pred = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    String obj = "http://dbpedia.org/ontology/Musical";
    DbpediaSparqlQuery query = new DbpediaSparqlQuery();
    boolean answer = query.askQuery(sub, pred, obj);
    Assert.assertThat(answer, is(true));
  }

}
