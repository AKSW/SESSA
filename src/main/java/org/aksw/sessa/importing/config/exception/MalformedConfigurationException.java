package org.aksw.sessa.importing.config.exception;

import java.io.IOException;

/**
 * Signals that the given configuration file is malformed, i.e. a key is missing or a value has a
 * not expected value
 */
public class MalformedConfigurationException extends IOException {

  /**
   * Constructs an MalformedConfigurationException with null as its error detail message.
   */

  public MalformedConfigurationException() {
    this(null);
  }

  /**
   * Constructs an MalformedConfigurationException with the specified detail message.
   *
   * @param message detail message (which is saved for later retrieval by the Throwable.getMessage()
   * method)
   */
  public MalformedConfigurationException(String message) {
    super(message);
  }

}
