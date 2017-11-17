package org.aksw.sessa.helper.files;


import java.io.IOException;
import java.util.Map.Entry;

/**
 * Created by Simon Bordewisch on 17.11.17.
 */
public interface FileHandlerInterface extends AutoCloseable {

  Entry<String, String> nextEntry() throws IOException;

  void close() throws IOException;
}
