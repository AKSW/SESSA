package org.aksw.sessa.importing.dictionary.implementation;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;

import java.io.IOException;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LuceneDictionaryTest extends FileBasedDictionaryInterfaceTest {

  private final String TEST_INDEX_LOCATION = "src/test/resources/index";

  @Before
  public void init() throws IOException {
    FileHandlerInterface handler = new TsvFileHandler(TEST_FILE1);
    dictionary = new LuceneDictionary(null, TEST_INDEX_LOCATION);
    ((LuceneDictionary) dictionary).clearIndex();
    dictionary.putAll(handler);
  }

  @After
  public void end() throws IOException {
    ((LuceneDictionary) dictionary).clearIndex();
    ((LuceneDictionary) dictionary).close();
  }

  @Test
  public void clearIndex_ClearsEntries() throws IOException {
    String nGram = "birthplace";
    String uri = "http://dbpedia.org/ontology/birthPlace";
    Assert.assertThat(dictionary.get(nGram), hasItem(uri));
    ((LuceneDictionary) dictionary).clearIndex();
    Assert.assertThat(dictionary.get(nGram), not(hasItem(uri)));
  }

}
