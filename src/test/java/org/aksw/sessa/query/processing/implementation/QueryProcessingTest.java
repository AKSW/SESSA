package org.aksw.sessa.query.processing.implementation;

import org.aksw.sessa.query.models.NGramHierarchy;
import org.aksw.sessa.query.processing.QueryProcessingInterface;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 02.06.17.
 */
public class QueryProcessingTest {

  private String ngram;
  private NGramHierarchy nGramHierarchy;

  @Test
  public void testQueryResultType() {
    QueryProcessingInterface qp = new SimpleQueryProcessing();
    ngram = "birthplace bill gates wife";
    nGramHierarchy = qp.processQuery(ngram);
    Assert.assertEquals(nGramHierarchy.getClass(), NGramHierarchy.class);
  }

  @Test
  public void testQueryResult() {
    QueryProcessingInterface qp = new SimpleQueryProcessing();
    ngram = "birthplace bill gates wife";
    nGramHierarchy = qp.processQuery(ngram);
    String[] expectedStringArray = {"birthplace bill gates wife", "birthplace bill gates",
        "bill gates wife", "birthplace bill", "bill gates", "gates wife", "birthplace", "bill",
        "gates", "wife"};
    Assert.assertArrayEquals(expectedStringArray, nGramHierarchy.toStringArray());
  }
}
