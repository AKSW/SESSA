package org.aksw.sessa.helper.files.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

/**
 * This class provides the ability to read tsv-files.
 * How the information in the files is processed has to be handled by child classes.
 * @author Simon Bordewisch
 */
public abstract class AbstractTsvFileHandler implements FileHandlerInterface {

  BufferedReader reader;
  String firstEntry;
  Stack<String> otherEntries;
  protected String file;

  AbstractTsvFileHandler(String file) throws IOException {
    this.file = file;
    reader = new BufferedReader((new FileReader(file)));
    otherEntries = new Stack<>();
  }

  boolean getNextPair() throws IOException {
    String line = reader.readLine();
    if (line != null) {
      String[] entryArray = line.split("\t");
      for (int i = 1; i < entryArray.length; i++) {
        otherEntries.add(entryArray[i]);
      }
      firstEntry = entryArray[0];
      return true;
    } else {
      return false;
    }
  }



  @Override
  public String getFileName(){
    return file;
  }

  /**
   * Closes the used readers and releases any system resources associated with it.
   *
   * @throws IOException If an I/O error occurs
   */
  @Override
  public void close() throws IOException {
    reader.close();
  }
}
