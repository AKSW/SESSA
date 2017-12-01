package org.aksw.sessa.importing.dictionary.util;

import org.apache.lucene.search.similarities.DefaultSimilarity;

/**
 * This class overrides the tf-value (term-frequency) to accommodate for the fact that we do not
 * have a document store, but a simple dictionary of n-grams to URIs. Therefore the tf-value is
 * changed to a constant (1).
 *
 * This class is largely based on {@link https://lucene.apache.org/core/3_5_0/api/core/org/apache/lucene/search/package-summary.html#changingSimilarity
 * this entry}, where it is mentioned to make the above mentioned changed (see second use case).
 *
 * @author Simon Bordewisch
 */
public class DictionaryEntrySimilarity extends DefaultSimilarity {

  @Override
  public float tf(float freq) {
    return 1;
  }
}
