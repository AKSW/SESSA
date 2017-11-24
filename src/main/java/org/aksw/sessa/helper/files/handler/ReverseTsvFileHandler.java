package org.aksw.sessa.helper.files.handler;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.aksw.sessa.importing.dictionary.FileBasedDictionary;

/**
 * This class is an implementation of the abstract class {@link FileBasedDictionary}. and is capable
 * of importing tsv-files (tab seperated values). Unlike {@link TsvFileHandler}, this class uses a
 * tsv-file which has a mapping of n-grams to a list of URIs. Therefore, this class is mainly used
 * for debugging, as it performs faster than the other file-import implementations.
 *
 * @author Simon Bordewisch
 */
public class ReverseTsvFileHandler extends AbstractTsvFileHandler {

  public ReverseTsvFileHandler(String file) throws IOException {
    super(file);
  }

  @Override
  public Entry<String, String> nextEntry() throws IOException {
    if (otherEntries.isEmpty()) {
      if (!super.getNextPair()) {
        return null;
      }
    }
    String value = otherEntries.pop();
    return new SimpleEntry<>(firstEntry.toLowerCase(), value);
  }

}