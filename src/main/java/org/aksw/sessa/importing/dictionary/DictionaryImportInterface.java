package org.aksw.sessa.importing.dictionary;

import java.util.List;
import java.util.Map;
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
   * Given a file name, returns a dictionary of n-gram to set of URIs.
   * The file has to be a mapping of URIs to a list of n-grams.
   * @param fileName name (and location) of a file with a mapping of URI's to a list/set of n-grams
   * @return mapping of n-grams to set of URIs
   */
  Map<String, Set<String>> getDictionary(String fileName);
}
