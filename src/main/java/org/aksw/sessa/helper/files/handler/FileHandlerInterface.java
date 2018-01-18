package org.aksw.sessa.helper.files.handler;


import java.io.IOException;
import java.util.Map.Entry;

/**
 * Provides an interface for file handling. The handlers can handle their associate file type and
 * provide the method nextEntry() to easily stream the file.
 *
 * @author Simon Bordewisch
 */
public interface FileHandlerInterface extends AutoCloseable {

  /**
   * Provides next entry, i.e. next key (n-gram) and value (URI) pair.
   *
   * @return next key and value pair
   * @throws IOException If an I/O error occurs
   */
  Entry<String, String> nextEntry() throws IOException;

  /**
   * Returns name of the file that is processed by the handler.
   *
   * @return name of the file that is processed by the handler
   */
  String getFileName();

  /**
   * Closes the used readers and releases any system resources associated with it.
   *
   * @throws IOException If an I/O error occurs
   */
  @Override
  void close() throws IOException;
}
