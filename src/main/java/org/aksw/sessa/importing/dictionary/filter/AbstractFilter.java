package org.aksw.sessa.importing.dictionary.filter;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * This abstract class is the foundation for making filters for the dictionary classes. Filters
 * apply after find the URIs and filter them down using a rank. How many results are left is based
 * on the given parameter.
 */
public abstract class AbstractFilter {

  /**
   * Number of returned results.
   */
  protected int numberOfResults;

  /**
   * Constructs the filter which filters the given URIs down to the given number.
   *
   * @param numberOfResults specifies the number of returned results
   */
  public AbstractFilter(int numberOfResults) {
    this.numberOfResults = numberOfResults;
  }

  /**
   * Main filter method. Returns a number of results with the highest rank (based on the rank
   * implementation).
   *
   * @param keyword keyword with which the entries where found
   * @param toBeFiltered entry set of n-grams and uris
   */
  public Set<Entry<String, String>> filter(
      String keyword, // e.g. "mark twains" (notice the 's')
      Set<Entry<String, String>> toBeFiltered //e.g. for entry: <"mark twain", dbr:Mark_Twain">
  ) {

    PriorityQueue<Entry<Entry<String, String>, Float>> sortedResults =
        new PriorityQueue<>(100,
            Collections.reverseOrder(Comparator.comparing(Entry::getValue)));
    for (Entry<String, String> entry : toBeFiltered) {
      float rank = getRank(keyword, entry);
      sortedResults.add(new SimpleEntry<>(entry, rank));
    }
    Set<Entry<String, String>> finalResultSet = new HashSet<>();
    for (int resultSize = 0;
        resultSize < numberOfResults && !sortedResults.isEmpty();
        resultSize++) {
      finalResultSet.add(sortedResults.poll().getKey());
    }
    return finalResultSet;
  }

  /**
   * Ranking method. Returns a rank/score for given entry and keyword. keyword is given to ensure
   * that the n-gram in the entry can be compared to the initial keyword.
   *
   * @param keyword keyword with which the entries where found
   * @param entry entry of one n-gram and uri
   */
  protected abstract float getRank(String keyword, Entry<String, String> entry);

  /**
   * Returns how many results should be returned
   *
   * @return number of returned results
   */
  public int getNumberOfResults() {
    return numberOfResults;
  }

}
