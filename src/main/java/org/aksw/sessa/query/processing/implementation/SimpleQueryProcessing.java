package org.aksw.sessa.query.processing.implementation;

import org.aksw.sessa.query.models.NGramHierarchy;
import org.aksw.sessa.query.processing.QueryProcessingInterface;

/**
 * Created by Simon Bordewisch on 02.06.17.
 * Most simple query processing class.
 */
public class SimpleQueryProcessing implements QueryProcessingInterface {

  /**
   * Gives unprocessed query to NGramHierarchy and returns it.
   *
   * @param query human written query
   * @return the NGram-hierarchy of the query
   */
  @Override
  public NGramHierarchy processQuery(String query) {

    NGramHierarchy hierarchy = new NGramHierarchy(query);
    return hierarchy;
  }
}
