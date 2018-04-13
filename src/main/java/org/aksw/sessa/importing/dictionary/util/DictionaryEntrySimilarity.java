package org.aksw.sessa.importing.dictionary.util;

import org.apache.lucene.search.similarities.DefaultSimilarity;

/**
 * This class overrides the tf- (term-frequency) and idf-value (inverse document frequency)  to
 * accommodate for the fact that we do not have a document store, but a simple dictionary of n-grams
 * to URIs in which we do not need scores based on those values. Therefore the tf-value is changed
 * to a constant (1). This implementation is largely based on the entry linked below, where it is
 * mentioned to make the above mentioned changed (see second use case).
 *
 * {@see https://lucene.apache.org/core/3_5_0/api/core/org/apache/lucene/search/package-summary.html#changingSimilarity}
 *
 * @author Simon Bordewisch
 */
public class DictionaryEntrySimilarity extends DefaultSimilarity {

  @Override
  public float tf(float freq) {
    return 1;
  }

  @Override
  public float idf(long docFreq, long numDocs) {
    return 1;
  }
}
