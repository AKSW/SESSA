package org.aksw.sessa.importing.dictionary.implementation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.FileBasedDictionaryImport;

/**
 * This class is an implementation of the abstract class
 * {@link org.aksw.sessa.importing.dictionary.FileBasedDictionaryImport}.
 * and is capable of importing tsv-files (tab seperated values). Unlike {@link
 * org.aksw.sessa.importing.dictionary.implementation.TsvDictionaryImport}, this class uses a
 * tsv-file which has a mapping of n-grams to a list of URIs. Therefore, this class is mainly used
 * for debugging, as it performs faster than the other file-import implementations.
 *
 * @author Simon Bordewisch
 */
public class ReverseTsvDictionaryImport extends FileBasedDictionaryImport {

  public ReverseTsvDictionaryImport(String fileName){
    dictionary = createDictionary(fileName);
  }

  protected Map<String, Set<String>> createDictionary(String fileName) {
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
    } catch (FileNotFoundException fnfe) {
      System.err.println("The file " + fileName +
          " was not found. It was probably not generated yet.\n" +
          "Please use the ReversedTsvDictionarySaver once to generate it");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dictionary;
  }

  @Override
  public Set<String> get(String nGram){
    return dictionary.get(nGram);
  }
}
