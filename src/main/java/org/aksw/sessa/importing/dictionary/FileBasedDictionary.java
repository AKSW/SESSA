package org.aksw.sessa.importing.dictionary;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.dictionary.filter.AbstractFilter;
import org.slf4j.LoggerFactory;

public abstract class FileBasedDictionary implements DictionaryInterface {

  protected Map<String, Set<String>> dictionary;
  protected PriorityQueue<AbstractFilter> filterQue;
  protected org.slf4j.Logger log = LoggerFactory.getLogger(DictionaryInterface.class);

  public FileBasedDictionary() {
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
   * Adds filter to the filter-queue. The filters added here are applied, order depending on their
   * given number of results (descending), after the dictionary found all candidates.
   *
   * @param filter filter to be added to the queue
   */
  @Override
  public void addFilter(AbstractFilter filter) {
    filterQue.add(filter);
  }

  /**
   * Allows the dictionary to filter based on the added filters.
   *
   * @param keyword the initial keyword for the search in the dictionary
   * @param foundEntrySet found set of URIs for the keyword
   */
  protected Set<String> filter(String keyword, Set<Entry<String, String>> foundEntrySet) {
    Set<String> uriSet = new HashSet<>();
    Set<Entry<String, String>> filteredEntrySet = new HashSet<>();
    filteredEntrySet.addAll(foundEntrySet);
    for (AbstractFilter filter : filterQue) {
      filteredEntrySet = filter.filter(keyword, filteredEntrySet);
      log.debug("Used filter {} with result limit of {}. Got list: {}",
          filter.getClass().getSimpleName(), filter.getNumberOfResults(), filteredEntrySet);
    }
    for (Entry<String, String> entry : filteredEntrySet) {
      uriSet.add(entry.getValue());
    }
    return uriSet;
  }
}
