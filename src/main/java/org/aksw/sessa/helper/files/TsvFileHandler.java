package org.aksw.sessa.helper.files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Stack;

/**
 * Created by Simon Bordewisch on 17.11.17.
 */
public class TsvFileHandler implements FileHandlerInterface {

  private BufferedReader reader;
  private String value;
  private Stack<String> keys;
  private String file;

  public TsvFileHandler(String file) throws IOException {
    this.file = file;
    reader = new BufferedReader((new FileReader(file)));
    keys = new Stack<>();
  }


  @Override
  public Entry<String, String> nextEntry() throws IOException {
    String key;
    if (keys.isEmpty()) {
      if (!getNextPair()) {
        return null;
      }
    }
    key = keys.pop();
    return new SimpleEntry<>(key.toLowerCase(), value);
  }

  private boolean getNextPair() throws IOException {
    String line = reader.readLine();
    if (line != null) {
      String[] entryArray = line.split("\t");
      for (int i = 1; i < entryArray.length; i++) {
        keys.add(entryArray[i]);
      }
      value = entryArray[0];
      return true;
    } else {
      return false;
    }
  }


  @Override
  public void close() throws IOException {
    reader.close();
  }

  @Override
  public String getFileName(){
    return file;
  }
}
