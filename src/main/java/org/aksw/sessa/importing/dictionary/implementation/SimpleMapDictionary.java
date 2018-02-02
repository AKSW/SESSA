package org.aksw.sessa.importing.dictionary.implementation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.aksw.sessa.query.models.Candidate;

/**
 * This class provides an easy implementation of dictionaries. It essentially just wraps the  given
 * Map<String, Set<String>>.
 */
public class SimpleMapDictionary implements DictionaryInterface {

  private Map<String, Set<String>> dictionary;
  private List<Filter> filterList;
  private EnergyFunctionInterface energyFunction;

  /**
   * Initializes dictionary with given map
   *
   * @param dictionary map to be used for the dictionary
   */
  public SimpleMapDictionary(Map<String, Set<String>> dictionary) {
    this.dictionary = dictionary;
    filterList = new LinkedList<>();
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
    return candidateSet;
  }

  @Override
  public void addFilter(Filter filter) {
    filterList.add(filter);
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
