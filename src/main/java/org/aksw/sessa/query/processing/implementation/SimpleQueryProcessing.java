package org.aksw.sessa.query.processing.implementation;

import org.aksw.sessa.query.models.NGramHierarchy;
import org.aksw.sessa.query.processing.QueryProcessingInterface;

/**
 * Simplest query processing class. Does not process the query before it is transformed to a n-gram
 * hiearchy. Using this class is recommended with keyword-search only, i.e. 'birthplace bill gates
 * wife" instead of 'Where was Bill Gates wife born?'.
 *
 * @author Simon Bordewisch
 */
public class SimpleQueryProcessing implements QueryProcessingInterface {

  /**
   * Gives unprocessed query to NGramHierarchy and returns it.
   *
   * @param query human written query
   * @return the n-gram hierarchy of the unprocessed query
   */
  @Override
  public NGramHierarchy processQuery(String query) {

    NGramHierarchy hierarchy = new NGramHierarchy(query.toLowerCase());
    return hierarchy;
  }
}
