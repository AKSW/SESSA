package org.aksw.sessa.importing.dictionary.implementation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.importing.dictionary.filter.AbstractFilter;

/**
 * This class provides an easy implementation of dictionaries.
 * It essentially just wraps the  given Map<String, Set<String>>.
 */
public class SimpleMapDictionary implements DictionaryInterface {

  private Map<String, Set<String>> dictionary;
  protected List<AbstractFilter> filterList;

  public SimpleMapDictionary(Map<String, Set<String>> dictionary) {
    this.dictionary = dictionary;
    filterList = new LinkedList<>();
  }

  @Override
  public Set<String> get(String nGram) {
    return dictionary.get(nGram);
  }

  @Override
  public void addFilter(AbstractFilter filter) {
    filterList.add(filter);
  }
}
