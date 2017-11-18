package org.aksw.sessa.importing.dictionary.implementation;

import java.io.IOException;
import java.util.Set;
import org.aksw.sessa.helper.files.FileHandlerInterface;
import org.aksw.sessa.helper.files.TsvFileHandler;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LuceneDictionaryTest {

  private final String TEST_FILE = "src/test/resources/en_surface_forms_small";
  private FileHandlerInterface handler;
  private DictionaryImportInterface dictionary;

  @Before
  public void init() throws IOException {
    handler = new TsvFileHandler(TEST_FILE);
    dictionary = new LuceneDictionary(handler);
  }

  //TODO: find a way to avoid code duplication (see HashMapDictionaryTest)
  @Test
  public void get_TestTwoWords() {
    String uri = "http://dbpedia.org/resource/Bill_Gates";
    Set<String> set = dictionary.get("bill gates");
    Assert.assertTrue(set.contains(uri));
  }

  @Test
  public void get_TestOneWord() {
    String uri = "http://dbpedia.org/ontology/birthPlace";
    Set<String> set = dictionary.get("birthplace");
    Assert.assertTrue(set.contains(uri));

    uri = "http://dbpedia.org/ontology/spouse";
    set = dictionary.get("wife");
    Assert.assertTrue(set.contains(uri));
  }

}
