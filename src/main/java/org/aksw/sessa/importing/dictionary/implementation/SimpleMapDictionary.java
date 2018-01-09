package org.aksw.sessa.importing.dictionary.implementation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.importing.dictionary.filter.AbstractFilter;

/**
 * This class provides an easy implementation of dictionaries. It essentially just wraps the  given
 * Map<String, Set<String>>.
 */
public class SimpleMapDictionary implements DictionaryInterface {

  private Map<String, Set<String>> dictionary;
  protected List<AbstractFilter> filterList;

  /**
   * Initializes dictionary with given map
   *
   * @param dictionary map to be used for the dictionary
   */
  public SimpleMapDictionary(Map<String, Set<String>> dictionary) {
    this.dictionary = dictionary;
    filterList = new LinkedList<>();
  }

  /**
   * Given a n-gram, returns a set of URIs related to it or null if this map contains no mapping for
   * the key.
   *
   * @param nGram n-gram whose associated value is to be returned
   * @return mapping of n-grams to set of URIs
   */
  @Override
  public Set<String> get(String nGram) {
    return dictionary.get(nGram);
  }

  @Override
  public void addFilter(AbstractFilter filter) {
    filterList.add(filter);
  }
}
