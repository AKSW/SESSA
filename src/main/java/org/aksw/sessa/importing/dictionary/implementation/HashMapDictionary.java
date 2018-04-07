package org.aksw.sessa.importing.dictionary.implementation;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.importing.dictionary.FileBasedDictionary;
import org.aksw.sessa.candidate.Candidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a HashMap-based dictionary given a file handler. This class is an implementation of the
 * interface {@link DictionaryInterface}.
 *
 * @author Simon Bordewisch
 */
public class HashMapDictionary extends FileBasedDictionary {

  private static final Logger log = LoggerFactory.getLogger(HashMapDictionary.class);
  private Map<String, Set<String>> dictionary;
  private int dictionarySize;

  /**
   * Initializes an empty dictionary
   */
  public HashMapDictionary() {
    this(null);
  }

  /**
   * Initializes the dictionary with given file handler. The file will be parsed into the
   * dictionary.
   *
   * @param handler handler to be used for filling the dictionary
   */
  public HashMapDictionary(FileHandlerInterface handler) {
    dictionarySize = 0;
    dictionary = new HashMap<>();
    if(handler != null){
      addToDictionary(handler);
    }
  }


  /**
   * Adds every entry that the file handler gives to the dictionary
   *
   * @param handler file handler that has file information
   */
  private void addToDictionary(FileHandlerInterface handler) {
    try {
      for (Entry<String, String> entry; (entry = handler.nextEntry()) != null; ) {
        String key = entry.getKey();
        Set<String> values = dictionary.get(key);
        if (values == null) {
          values = new HashSet<>();
        }
        values.add(entry.getValue());
        log.trace("Adding to dictionary: {} - {}", key, values);
        dictionary.put(key, values);
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage());
    }
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
    Set<String> foundUris = dictionary.get(nGram);
    Set<Candidate> candidateSet = new HashSet<>();
    if (foundUris != null) {
      for (String uri : foundUris) {
        Candidate candidate = new Candidate(uri, nGram);
        candidateSet.add(candidate);
        dictionarySize++;
      }
    }
    Set<Candidate> filteredCandidateSet = this.filter(nGram, candidateSet);
    if (energyFunction != null) {
      this.calculateEnergy(filteredCandidateSet, nGram);
    }
    return filteredCandidateSet;
  }

  /**
   * Adds the entries in the give file to the dictionary.
   *
   * @param handler handler that has file information
   */
  public void putAll(FileHandlerInterface handler) {
    addToDictionary(handler);
  }

  /**
   * Returns the size of the dictionary, i.e. how many pairs of keys and values.
   */
  @Override
  public int size() {
    return dictionarySize;
  }

  /**
   * Returns a set view of the mappings contained in this map.
   *
   * @return a set view of the mappings contained in this map
   */
  public Set<Entry<String, Set<String>>> entrySet() {
    return dictionary.entrySet();
  }

  private void calculateEnergy(Set<Candidate> candidateSet, String nGram) {
    for (Candidate candidate : candidateSet) {
      float energy = energyFunction
          .calculateEnergyScore(nGram, candidate.getUri(), candidate.getKey());
      candidate.setEnergy(energy);
    }
  }
}
