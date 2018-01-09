package org.aksw.sessa.importing.dictionary;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.dictionary.filter.AbstractFilter;

public abstract class FileBasedDictionary implements DictionaryInterface {

  protected Map<String, Set<String>> dictionary;
  protected PriorityQueue<AbstractFilter> filterQue;

  public FileBasedDictionary(){
    filterQue = new PriorityQueue<>(10,
        Collections.reverseOrder(Comparator.comparing(AbstractFilter::getNumberOfResults)));
  }
  /**
   * Adds the entries in the give handler to the dictionary.
   *
   * @param handler handler with file information
   */
  public abstract void putAll(FileHandlerInterface handler);

  /**
   * Adds filter to the filter-queue.
   * @param filter filter to be added to the queue
   */
  @Override
  public void addFilter(AbstractFilter filter) {
    filterQue.add(filter);
  }
}
