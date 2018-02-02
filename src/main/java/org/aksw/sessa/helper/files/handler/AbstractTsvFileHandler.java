package org.aksw.sessa.helper.files.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

/**
 * This class provides the ability to read tsv-files. How the information in the files is processed
 * has to be handled by child classes.
 *
 * @author Simon Bordewisch
 */
public abstract class AbstractTsvFileHandler implements FileHandlerInterface {

  protected String file;
  String firstEntry;
  Stack<String> otherEntries;
  private BufferedReader reader;

  /**
   * Initializes reader with given file and stack as empty stack.
   *
   * @param file file name that should be read
   * @throws IOException If an I/O error occurs
   */
  AbstractTsvFileHandler(String file) throws IOException {
    this.file = file;
    reader = new BufferedReader((new FileReader(file)));
    otherEntries = new Stack<>();
  }

  /**
   * Reads next line of file and therefore the next pair of key => set of values.
   *
   * @return next pair of key => set of values
   * @throws IOException If an I/O error occurs
   */
  boolean getNextPair() throws IOException {
    String line = reader.readLine();
    if (line != null) {
      String[] entryArray = line.split("\t");
      if (entryArray.length < 2) {
        throw new IOException("Malformed TSV-format in following line: " + line);
      }
      for (int i = 1; i < entryArray.length; i++) {
        otherEntries.add(entryArray[i]);
      }
      firstEntry = entryArray[0];
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns name of the file that is processed by the handler.
   *
   * @return name of the file that is processed by the handler
   */
  @Override
  public String getFileName() {
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
