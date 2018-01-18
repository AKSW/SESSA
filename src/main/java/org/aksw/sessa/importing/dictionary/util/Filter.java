package org.aksw.sessa.importing.dictionary.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.query.models.Candidate;

/**
 * This abstract class is the foundation for making filters for the dictionary classes. Filters
 * apply after find the URIs and filter them down using the calculated energy. How many results are
 * left is based on the given parameter.
 */
public class Filter {

  /**
   * Number of returned results.
   */
  private int numberOfResults;
  private EnergyFunctionInterface energyFunction;
  private boolean descendingOrder;

  /**
   * Constructs the filter which filters the given URIs down to the given number based on the used
   * energy score. The order is set to default (descending), so lowest scores will be filtered out.
   *
   * @param energyFunction specifies the used energy function
   * @param numberOfResults specifies the number of returned results
   */
  public Filter(EnergyFunctionInterface energyFunction, int numberOfResults) {
    this(energyFunction, true, numberOfResults);
  }

  /**
   * Constructs the filter which filters the given URIs down to the given number based on the used
   * energy score.
   *
   * @param energyFunction specifies the used energy function
   * @param descendingOrder set to true to filter out the lowest scores
   * @param numberOfResults specifies the number of returned results
   */
  public Filter(EnergyFunctionInterface energyFunction, boolean descendingOrder,
      int numberOfResults) {
    this.numberOfResults = numberOfResults;
    this.energyFunction = energyFunction;
    this.descendingOrder = descendingOrder;
  }

  /**
   * Main filter method. Returns a number of results with the highest rank (based on the rank
   * implementation).
   *
   * @param keyword keyword with which the entries where found
   * @param candidateSet entry set of n-grams and uris (candidates)
   * @return filtered set of candidates
   */
  public Set<Candidate> filter(String keyword, Set<Candidate> candidateSet) {

    PriorityQueue<Entry<Candidate, Float>> sortedResults;
    if (descendingOrder) {
      sortedResults = new PriorityQueue<>(100,
          Collections.reverseOrder(Comparator.comparing(Entry::getValue)));
    } else {
      sortedResults = new PriorityQueue<>(100, Comparator.comparing(Entry::getValue));
    }

    for (Candidate candidate : candidateSet) {
      float rank = getRank(keyword, candidate.getUri(), candidate.getKey());
      sortedResults.add(new SimpleEntry<>(candidate, rank));
    }
    Set<Candidate> finalResultSet = new HashSet<>();
    for (int resultSize = 0;
        resultSize < numberOfResults && !sortedResults.isEmpty();
        resultSize++) {
      finalResultSet.add(sortedResults.poll().getKey());
    }
    return finalResultSet;
  }

  /**
   * Ranking method. Returns a rank/score for given entry and keyword. Keyword is given to ensure
   * that the n-gram in the entry can be compared to the initial keyword.
   *
   * @param keyword original n-gram with which the uri was found
   * @param foundURI found URI for which the energy score should be calculated
   * @param foundKey key of the dictionary for which the URI is the value
   *
   * @return rank for given entry and keyword
   */
  protected float getRank(String keyword, String foundURI, String foundKey) {
    return energyFunction.calculateEnergyScore(keyword, foundURI, foundKey);
  }

  /**
   * Returns how many results should be returned
   *
   * @return number of returned results
   */
  public int getNumberOfResults() {
    return numberOfResults;
  }

}
