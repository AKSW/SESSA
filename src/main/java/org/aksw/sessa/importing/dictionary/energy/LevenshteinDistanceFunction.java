package org.aksw.sessa.importing.dictionary.energy;

import org.apache.lucene.search.spell.LuceneLevenshteinDistance;

/**
 * Provides function to calculate the energy score based on Levenshtein distance. This class is an
 * implementation of the interface {@link EnergyFunctionInterface}.
 */
public class LevenshteinDistanceFunction implements EnergyFunctionInterface {

  /**
   * Returns the energy score of an URI with the given data. More precisely it calculates a score
   * between 0 and 1 based on the levenshtein distance based on the initial keyword and the n-gram
   * in the entry. Returning a value of 1 means the specified strings are identical and 0 means the
   * string are maximally different.
   *
   * @param nGram original n-gram with which the uri was found
   * @param foundURI found URI for which the energy score should be calculated
   * @param foundKey key of the dictionary for which the URI is the value
   * @return the energy score of an URI with the given data
   */
  @Override
  public float calculateEnergyScore(String nGram, String foundURI, String foundKey) {
    LuceneLevenshteinDistance levenshtein = new LuceneLevenshteinDistance();
    return levenshtein.getDistance(nGram.toLowerCase(), foundKey.toLowerCase());
  }
}
