package org.aksw.sessa.importing.dictionary;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import org.aksw.sessa.candidate.Candidate;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.slf4j.LoggerFactory;

public abstract class FileBasedDictionary implements DictionaryInterface {

  protected PriorityQueue<Filter> filterQue;
  protected EnergyFunctionInterface energyFunction;

  protected org.slf4j.Logger log = LoggerFactory.getLogger(DictionaryInterface.class);

  /**
   * Constructs the dictionary with the given energy function.
   */
  public FileBasedDictionary() {
    filterQue = new PriorityQueue<>(10,
        Collections.reverseOrder(Comparator.comparing(Filter::getNumberOfResults)));
    energyFunction = (a, b, c) -> 1;
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
      log.debug("Used filter for keyword {} with {} with result limit of {}. Got list: {}",
          keyword,
          filter.getEnergyFunction().getClass().getSimpleName(),
          filter.getNumberOfResults(),
          filteredCandidateSet);
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

  /**
   * Calculates the energy of all candidates based on all information in the candidate and the query
   * string.
   *
   * @param candidateSet candidate set, in which all candidates should get their energy calculated
   * @param query query string, i.e. the string with which the candidate was found in the
   * dictionary
   * @return updated candidate set with energy function applied
   */
  protected Set<Candidate> calculateEnergy(Set<Candidate> candidateSet, String query) {
    for (Candidate candidate : candidateSet) {
      float energy = energyFunction
          .calculateEnergyScore(query, candidate.getUri(), candidate.getKey());
      candidate.setEnergy(energy);
    }
    return candidateSet;
  }
}
