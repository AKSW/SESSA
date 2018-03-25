package org.aksw.sessa.importing.dictionary.implementation;

import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.aksw.sessa.query.models.Candidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an easy implementation of dictionaries. It essentially just wraps the  given
 * Map<String, Set<String>>.
 */
public class SimpleMapDictionary implements DictionaryInterface {

  private static final Logger log = LoggerFactory.getLogger(SimpleMapDictionary.class);

  private Map<String, Set<String>> dictionary;
  private PriorityQueue<Filter> filterQue;
  private EnergyFunctionInterface energyFunction;

  /**
   * Initializes dictionary with given map
   *
   * @param dictionary map to be used for the dictionary
   */
  public SimpleMapDictionary(Map<String, Set<String>> dictionary) {
    this.dictionary = dictionary;
    filterQue = new PriorityQueue<>();
    energyFunction = null;
  }

  /**
   * Given a n-gram, returns a set of URIs related to it or null if this map contains no mapping for
   * the key.
   *
   * @param nGram n-gram whose associated value is to be returned
   * @return mapping of n-grams to set of URIs
   */
  @Override
  public Set<Candidate> get(String nGram) {
    Set<String> uriSet = dictionary.get(nGram);
    Set<Candidate> candidateSet = new HashSet<>();
    if (uriSet != null) {
      for (String uri : uriSet) {
        Candidate candidate = new Candidate(uri, nGram);
        if (energyFunction != null) {
          float energy = energyFunction.calculateEnergyScore(nGram, uri, nGram);
          candidate.setEnergy(energy);
        }
        candidateSet.add(candidate);
      }
    }
    return filter(nGram, candidateSet);
  }

  /**
   * Allows the dictionary to filter based on the added filters.
   *
   * @param keyword the initial keyword for the search in the dictionary
   * @param candidateSet found set of candidates for the keyword
   * @return filtered set of candidates
   */
  private Set<Candidate> filter(String keyword, Set<Candidate> candidateSet) {
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

  @Override
  public void addFilter(Filter filter) {
    filterQue.add(filter);
  }

  /**
   * Sets the energy function for the results.
   *
   * @param function energy function which should be applied to the results.
   */
  @Override
  public void setEnergyFunction(EnergyFunctionInterface function) {
    energyFunction = function;
  }
}
