package org.aksw.sessa.helper.files.handler;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * This class is an implementation of the abstract class {@link AbstractTsvFileHandler} and is
 * capable of handling tsv-files (tab separated values). This class expects a tsv-file which has a
 * mapping of URIs to a list of n-grams.
 *
 * @author Simon Bordewisch
 */
public class TsvFileHandler extends AbstractTsvFileHandler {

  /**
   * Initializes basic handler. File has to be loaded with {@link #loadFile(String)}.
   */
  public TsvFileHandler() {
    super();
  }

  /**
   * Initializes reader with given file and stack as empty stack.
   *
   * @param file file name that should be read
   * @throws IOException If an I/O error occurs
   */
  public TsvFileHandler(String file) throws IOException {
    super(file);
  }

  /**
   * Provides next entry, i.e. next key and value pair.
   *
   * @return next key and value pair
   * @throws IOException If an I/O error occurs
   */
  @Override
  public Entry<String, String> nextEntry() throws IOException {
    if (otherEntries.isEmpty()) {
      if (!super.getNextPair()) {
        return null;
      }
    }
    String key = otherEntries.pop();
    return new SimpleEntry<>(key.toLowerCase(), firstEntry);
  }
}