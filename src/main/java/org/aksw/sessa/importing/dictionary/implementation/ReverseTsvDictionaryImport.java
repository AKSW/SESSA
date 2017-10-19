package org.aksw.sessa.importing.dictionary.implementation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;

/**
 * This class is an implementation of the interface {@link org.aksw.sessa.importing.dictionary.DictionaryImportInterface}.
 * and is capable of importing tsv-files (tab seperated values). Unlike {@link
 * org.aksw.sessa.importing.dictionary.implementation.TsvDictionaryImport}, this class uses a
 * tsv-file which has a mapping of n-grams to a list of URIs. Therefore, this class is mainly used
 * for debugging, as it performs faster than the other file-import implementations.
 *
 * @author Simon Bordewisch
 */
public class ReverseTsvDictionaryImport implements DictionaryImportInterface {

  @Override
  public Map<String, Set<String>> getDictionary(String fileName) {
    Map<String, Set<String>> dictionary = new HashMap<>(14000000);
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      for (String line; (line = reader.readLine()) != null; ) {
        String[] record = line.split("\t");
        Set<String> values = new HashSet<>();
        for (int i = 1; i < record.length; i++) {
          if (!record[i].equals("")) {
            values.add(record[i]);
          }
        }
        dictionary.put(record[0], values);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dictionary;
  }
}
