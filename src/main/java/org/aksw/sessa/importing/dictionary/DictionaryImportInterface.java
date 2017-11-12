package org.aksw.sessa.importing.dictionary;

import java.util.Set;

/**
 * This interface provides the skeleton for classes which should
 * import files, which contain entries of the format "URI -> List/Set of n-grams".
 * Those files will be used to make a reverse dictionary, i. e."n-gram -> Set of URIs".
 *
 * @author Simon Bordewisch
 */
public interface DictionaryImportInterface {

  /**
   * Given a n-gram, returns a set of URIs related to it or null if this map contains
   * no mapping for the key.
   * @param nGram n-gram whose associated value is to be returned
   * @return mapping of n-grams to set of URIs
   */
  Set<String> get(String nGram);
}
