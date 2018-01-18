package org.aksw.sessa.importing.dictionary;

import java.util.Set;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.aksw.sessa.query.models.Candidate;

/**
 * This interface provides the skeleton for classes which should import files, which contain entries
 * of the format "URI -> List/Set of n-grams". Those files will be used to make a reverse
 * dictionary, i. e."n-gram -> Set of URIs".
 *
 * @author Simon Bordewisch
 */
public interface DictionaryInterface {

  /**
   * Given a n-gram, returns a set of candidate URIs related to it or null if this map contains no
   * mapping for the key.
   *
   * @param nGram n-gram whose associated value is to be returned
   * @return mapping of n-grams to set of URIs
   */
  Set<Candidate> get(String nGram);

  /**
   * Adds filter to the results in the {@link #get(String) get}-method.
   *
   * @param filter filter which should be applied to the results
   */
  void addFilter(Filter filter);

  /**
   * Sets the energy function for the results.
   *
   * @param energyFunction energy function which should be applied to the results.
   */
  void setEnergyFunction(EnergyFunctionInterface energyFunction);
}

