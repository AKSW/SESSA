package org.aksw.sessa.importing.dictionary.implementation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;

/**
 * This class is an implementation of the interface
 * {@link org.aksw.sessa.importing.dictionary.DictionaryImportInterface}.
 * and is capable of importing tsv-file (tab seperated values).
 * Therefore the file has to have one entry per line and an entry has the following format:
 * "URI\t List\t of\t n-grams".
 * @author Simon Bordewisch
 */
public class TsvDictionaryImport implements DictionaryImportInterface{

  @Override
  public Map<String, List<String>> getDictionary(String fileName) {
    // TODO: Consider other Maps (e.g. PatriciaTrees)
    Map<String, List<String>> dictionary = new HashMap<>(10000000);
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));

      try {
        for(String line; (line = reader.readLine()) != null; ){
          // TODO: error handling for false tsv-entries
          String[] entryArray = line.split("\t");
          for(int i=1; i<entryArray.length;i++) {
            List<String> uriList = dictionary.get(entryArray[i]);
            if(uriList == null){
              uriList = new ArrayList<>();
            }
              uriList.add(entryArray[0]);
            dictionary.put(entryArray[i].toLowerCase(), uriList);
          }
        }
      }
      finally {
        reader.close();
      }
    }
    catch ( IOException e ) {
      System.err.println( "Error while handling " + fileName );
    }
    return dictionary;
  }
}
