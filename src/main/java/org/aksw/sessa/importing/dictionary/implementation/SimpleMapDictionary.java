package org.aksw.sessa.importing.dictionary.implementation;

import java.util.Map;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;

/**
 * This class provides an easy implementation of dictionaries.
 * It essentially just wraps the  given Map<String, Set<String>>.
 */
public class SimpleMapDictionary implements DictionaryInterface {

  private Map<String, Set<String>> dictionary;

  public SimpleMapDictionary(Map<String, Set<String>> dictionary) {
    this.dictionary = dictionary;
  }

  @Override
  public Set<String> get(String nGram) {
    return dictionary.get(nGram);
  }


}
