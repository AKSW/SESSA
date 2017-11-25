package org.aksw.sessa.importing.dictionary.implementation;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;

import java.io.IOException;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.ReverseTsvFileHandler;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.aksw.sessa.importing.dictionary.FileBasedDictionary;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 21.06.17.
 */
public class HashMapDictionaryTest extends FileBasedDictionaryTest {

  @Before
  public void init() throws IOException{
    FileHandlerInterface handler = new TsvFileHandler(TEST_FILE1);
      dictionary = new HashMapDictionary(handler);
  }
}
