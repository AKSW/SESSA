package org.aksw.sessa.helper.files.saver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.RdfFileHandler;
import org.aksw.sessa.importing.dictionary.FileBasedDictionary;
import org.aksw.sessa.importing.dictionary.implementation.HashMapDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReversedTsvDictionarySaver {


  private static final Logger log = LoggerFactory.getLogger(ReversedTsvDictionarySaver.class);

  private static FileBasedDictionary dictionary;

  public static void saveDictionary(String target, FileHandlerInterface... sources) {

    for (FileHandlerInterface source : sources) {
      loadFileToHashMap(source);
    }
    saveDictionary(target);
  }


  private static void loadFileToHashMap(FileHandlerInterface fileHandler) {
    try (FileHandlerInterface handler = fileHandler) {
      if (dictionary == null) {
        dictionary = new HashMapDictionary(handler);
      } else {
        dictionary.putAll(handler);
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }


  private static void saveDictionary(String file) {
    try (PrintWriter writer = new PrintWriter(file)) {
      for (Entry<String, Set<String>> entry : dictionary.entrySet()) {
        writer.print(entry.getKey());
        for (String value : entry.getValue()) {
          writer.print("\t" + value);
        }
        writer.println();
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }
}
