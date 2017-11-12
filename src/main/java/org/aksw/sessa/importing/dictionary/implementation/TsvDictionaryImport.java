package org.aksw.sessa.importing.dictionary.implementation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.FileBasedDictionaryImport;

/**
 * This class is an implementation of the interface
 * {@link org.aksw.sessa.importing.dictionary.DictionaryImportInterface}.
 * and is capable of importing tsv-files (tab seperated values).
 * @author Simon Bordewisch
 */
public class TsvDictionaryImport extends FileBasedDictionaryImport {

  public TsvDictionaryImport(String fileName){
    dictionary = createDictionary(fileName);
  }


  /**
   * Given a file name, returns a dictionary of n-gram to set of URIs.
   * The file has to be a mapping of URIs to a list of n-grams.
   * The file has to have the tsv-format. Therefore the file has to have one
   * entry per line and an entry has the following format:
   * "URI\tList\tof\tn-grams".
   * @param fileName name (and location) of a file with a mapping of URI's to a list/set of n-grams
   * @return mapping of n-grams to set of URIs
   */
  protected Map<String, Set<String>> createDictionary(String fileName) {
    // TODO: Consider other Maps (e.g. PatriciaTrees)
    Map<String, Set<String>> dictionary = new HashMap<>(10000000);
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));

      try {
        for (String line; (line = reader.readLine()) != null; ) {
          // TODO: error handling for false tsv-entries
          String[] entryArray = line.split("\t");
          for (int i = 1; i < entryArray.length; i++) {
            Set<String> surfaceFormSet = dictionary.get(entryArray[i]);
            if (surfaceFormSet == null) {
              surfaceFormSet = new HashSet<>();
            }
            surfaceFormSet.add(entryArray[0]);
            String uri = entryArray[i].toLowerCase();
			dictionary.put(uri, surfaceFormSet);
          }
        }
      } finally {
        reader.close();
      }
    } catch (IOException e) {
   //.error("Error while handling " + fileName);
    	//FIXME use logger of logback
    	System.out.println(e.getLocalizedMessage());
    }
    return dictionary;
  }

  @Override
  public Set<String> get(String nGram) {
    return dictionary.get(nGram);
  }
}
