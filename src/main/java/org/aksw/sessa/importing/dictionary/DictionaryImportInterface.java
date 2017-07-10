package org.aksw.sessa.importing.dictionary;

import java.util.List;
import java.util.Map;

/**
 * This interface provides the skeleton for classes which should
 * import files, which contain entries of the format "URI -> List of n-grams".
 * Those files will be used to make a reverse dictionary, i. e."n-gram -> List of URIS".
 * @author Simon Bordewisch
 */
public interface DictionaryImportInterface {

  Map<String,List<String>> getDictionary(String fileName);
}
