package org.aksw.sessa.helper.files.handler;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * Created by Simon Bordewisch on 17.11.17.
 */
public class TsvFileHandler extends AbstractTsvFileHandler {

  public TsvFileHandler(String file) throws IOException {
    super(file);
  }

  @Override
  public Entry<String, String> nextEntry() throws IOException {
    String key;
    if (otherEntries.isEmpty()) {
      if (!getNextPair()) {
        return null;
      }
    }
    key = otherEntries.pop();
    return new SimpleEntry<>(key.toLowerCase(), firstEntry);
  }
}