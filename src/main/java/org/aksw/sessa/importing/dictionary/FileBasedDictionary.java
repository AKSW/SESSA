package org.aksw.sessa.importing.dictionary;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.aksw.sessa.query.models.Candidate;
import org.slf4j.LoggerFactory;

public abstract class FileBasedDictionary implements DictionaryInterface {

  protected Map<String, Set<String>> dictionary;
  protected PriorityQueue<Filter> filterQue;
  protected EnergyFunctionInterface energyFunction;

  protected org.slf4j.Logger log = LoggerFactory.getLogger(DictionaryInterface.class);

  /**
   * Constructs the dictionary with the given energy function.
   */
  public FileBasedDictionary() {
    filterQue = new PriorityQueue<>(10,
        Collections.reverseOrder(Comparator.comparing(Filter::getNumberOfResults)));
    energyFunction = null;
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
  public void addFilter(Filter filter) {
    filterQue.add(filter);
  }

  /**
   * Allows the dictionary to filter based on the added filters.
   *
   * @param keyword the initial keyword for the search in the dictionary
   * @param candidateSet found set of candidates for the keyword
   * @return filtered set of candidates
   */
  protected Set<Candidate> filter(String keyword, Set<Candidate> candidateSet) {
    Set<Candidate> filteredCandidateSet = new HashSet<>();
    filteredCandidateSet.addAll(candidateSet);
    for (Filter filter : filterQue) {
      filteredCandidateSet = filter.filter(keyword, filteredCandidateSet);
      log.debug("Used filter {} with result limit of {}. Got list: {}",
          filter.getClass().getSimpleName(), filter.getNumberOfResults(), filteredCandidateSet);
    }
    return filteredCandidateSet;
  }

  /**
   * Sets the energy function for the results.
   *
   * @param energyFunction energy function used to calculate the energy score for the nodes
   */
  @Override
  public void setEnergyFunction(EnergyFunctionInterface energyFunction) {
    this.energyFunction = energyFunction;
  }

  @Override
  public String toString() {
    return "FileBasedDictionary{" +
        "dictionary=" + dictionary +
        '}';
  }
}
