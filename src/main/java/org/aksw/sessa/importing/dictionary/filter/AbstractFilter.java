package org.aksw.sessa.importing.dictionary.filter;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

public abstract class AbstractFilter {

  protected int numberOfResults;

  public AbstractFilter(int numberOfResults) {
    this.numberOfResults = numberOfResults;
  }

  public Set<Entry<String, String>> filter(String keyword,
      Set<Entry<String, String>> toBeFiltered) {

    PriorityQueue<Entry<Entry<String, String>, Float>> sortedResults =
        new PriorityQueue<>(1000,
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

  protected abstract float getRank(String keyword, Entry<String, String> entry);

  public int getNumberOfResults(){
    return numberOfResults;
  }

}
