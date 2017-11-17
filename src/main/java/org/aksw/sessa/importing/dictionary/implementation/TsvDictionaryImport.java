package org.aksw.sessa.importing.dictionary.implementation;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.files.FileHandlerInterface;
import org.aksw.sessa.helper.files.TsvFileHandler;
import org.aksw.sessa.importing.dictionary.FileBasedDictionaryImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an implementation of the interface {@link org.aksw.sessa.importing.dictionary.DictionaryImportInterface}.
 * and is capable of importing tsv-files (tab separated values).
 *
 * @author Simon Bordewisch
 */
public class TsvDictionaryImport extends FileBasedDictionaryImport {

  private static final Logger log = LoggerFactory.getLogger(FileBasedDictionaryImport.class);

  public TsvDictionaryImport(String fileName) {
    dictionary = createDictionary(fileName);
  }


  /**
   * Given a file name, returns a dictionary of n-gram to set of URIs. The file has to be a mapping
   * of URIs to a list of n-grams. The file has to have the tsv-format. Therefore the file has to
   * have one entry per line and an entry has the following format: "URI\tList\tof\tn-grams".
   *
   * @param fileName name (and location) of a file with a mapping of URI's to a list/set of n-grams
   * @return mapping of n-grams to set of URIs
   */
  protected Map<String, Set<String>> createDictionary(String fileName) {
    Map<String, Set<String>> dictionary = new HashMap<>();
    try (FileHandlerInterface handler = new TsvFileHandler(fileName)) {
      for (Entry<String, String> entry; (entry = handler.nextEntry()) != null; ) {
        System.out.println(entry);
        String key = entry.getKey();
        Set<String> values = dictionary.get(key);
        if (values == null) {
          values = new HashSet<>();
        }
        values.add(entry.getValue());
        dictionary.put(key, values);
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
    return dictionary;
  }

  @Override
  public Set<String> get(String nGram) {
    return dictionary.get(nGram);
  }
}
