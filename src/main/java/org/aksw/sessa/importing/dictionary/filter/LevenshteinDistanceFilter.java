package org.aksw.sessa.importing.dictionary.filter;

import java.util.Map.Entry;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;

/**
 * Provides a filter which is based on levenshtein distance. This class is an implementation of the
 * abstract class {@link AbstractFilter}.
 *
 * @author Simon Bordewisch
 */
public class LevenshteinDistanceFilter extends AbstractFilter {

  /**
   * Constructs the filter which filters the given URIs down to the given number.
   *
   * @param numberOfResults specifies the number of returned results
   */
  public LevenshteinDistanceFilter(int numberOfResults) {
    super(numberOfResults);
  }

  /**
   * Calculates are score between 0 and 1 based on the levenshtein distance based on the initial
   * keyword and the n-gram in the entry. Returning a value of 1 means the specified strings are
   * identical and 0 means the string are maximally different.
   *
   * @param keyword keyword with which the entries where found
   * @param entry entry of one n-gram and uri
   * @return levenshtein distance
   * @see org.apache.lucene.search.spell.LuceneLevenshteinDistance
   */
  @Override
  protected float getRank(String keyword, Entry<String, String> entry) {
    LuceneLevenshteinDistance levenshtein = new LuceneLevenshteinDistance();
    float rank = levenshtein.getDistance(keyword, entry.getKey());
    return rank;
  }
}
