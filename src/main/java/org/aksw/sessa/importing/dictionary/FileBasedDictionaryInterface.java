package org.aksw.sessa.importing.dictionary;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;

/**
 * This abstract class is the base for all file-based dictionaries, i.e. all dictionaries, which
 * use file as their source of generating the dictionary.
 */
public interface FileBasedDictionaryInterface extends DictionaryInterface {


  /**
   * Adds the entries in the give handler to the dictionary.
   *
   * @param handler handler with file information
   */
  void putAll(FileHandlerInterface handler);
}
