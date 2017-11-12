package org.aksw.sessa.importing.dictionary;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class FileBasedDictionaryImport implements DictionaryImportInterface {

  protected Map<String, Set<String>> dictionary;

  public void putAll(String fileName){
    dictionary.putAll(createDictionary(fileName));
  }

  protected abstract Map<String, Set<String>> createDictionary(String fileName);

  public Set<Entry<String, Set<String>>> entrySet() {
    return dictionary.entrySet();
  }
}
