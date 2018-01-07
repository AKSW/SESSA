package org.aksw.sessa.importing.dictionary;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.dictionary.filter.AbstractFilter;

public abstract class FileBasedDictionary implements DictionaryInterface {

  protected Map<String, Set<String>> dictionary;
  protected PriorityQueue<AbstractFilter> filterList;

  public FileBasedDictionary(){
    filterList = new PriorityQueue<>(10,
        Collections.reverseOrder(Comparator.comparing(AbstractFilter::getNumberOfResults)));
  }
  /**
   * Adds the entries in the give handler to the dictionary.
   *
   * @param handler handler with file information
   */
  public abstract void putAll(FileHandlerInterface handler);

  /**
   * Returns a set view of the mappings contained in this map.
   *
   * @return a set view of the mappings contained in this map
   */
  public Set<Entry<String, Set<String>>> entrySet() {
    return dictionary.entrySet();
  }

  @Override
  public void addFilter(AbstractFilter filter) {
    filterList.add(filter);
  }
}
