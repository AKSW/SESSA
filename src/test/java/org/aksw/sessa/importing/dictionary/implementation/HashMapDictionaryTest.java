package org.aksw.sessa.importing.dictionary.implementation;

import java.io.IOException;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 21.06.17.
 */
public class HashMapDictionaryTest {

  private DictionaryImportInterface dictionary = null;
  private final String fileName = "src/test/resources/en_surface_forms_small.tsv";

  @Before
  public void init() throws IOException{
    if (dictionary == null) {
      FileHandlerInterface handler = new TsvFileHandler(fileName);
      dictionary = new HashMapDictionary(handler);
    }
  }

  //TODO: find a way to avoid code duplication (see LuceneDictionaryTest)
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
