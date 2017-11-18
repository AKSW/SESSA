package org.aksw.sessa.importing.dictionary;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;

public abstract class FileBasedDictionary implements DictionaryInterface {

  protected Map<String, Set<String>> dictionary;

  /**
   * Adds the entries in the give handler to the dictionary.
   * @param handler handler with file information
   */
  public void putAll(FileHandlerInterface handler){
    dictionary.putAll(createDictionary(handler));
  }

  protected abstract Map<String, Set<String>> createDictionary(FileHandlerInterface handler);

  /**
   * Returns a set view of the mappings contained in this map.
   * @return a set view of the mappings contained in this map
   */
  public Set<Entry<String, Set<String>>> entrySet() {
    return dictionary.entrySet();
  }
}
