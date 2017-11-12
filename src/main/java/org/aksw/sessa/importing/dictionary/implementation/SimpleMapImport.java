package org.aksw.sessa.importing.dictionary.implementation;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;

public class SimpleMapImport implements DictionaryImportInterface {

  private Map<String, Set<String>> dictionary;

  public SimpleMapImport(Map<String, Set<String>> dictionary){
    this.dictionary = dictionary;
  }

  @Override
  public Set<String> get(String nGram) {
    return dictionary.get(nGram);
  }


}
