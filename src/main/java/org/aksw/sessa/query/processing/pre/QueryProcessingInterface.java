package org.aksw.sessa.query.processing.pre;

import org.aksw.sessa.query.models.NGramHierarchy;

/**
 * Interface for processing queries and getting the n-gram hierarchy before they are given to
 * SESSA.
 *
 * @author Simon Bordewisch
 */
public interface QueryProcessingInterface {

  /**
   * Used to process the string in a meaningful way and creating a n-gram hiearchy.
   *
   * @param query unprocessed query or question for SESSA
   * @return n-gram hierarchy of the processed query
   */
  NGramHierarchy processQuery(String query);
}
