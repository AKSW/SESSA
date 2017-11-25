package org.aksw.sessa.importing.dictionary.implementation;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;

import java.io.IOException;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.ReverseTsvFileHandler;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LuceneDictionaryTest {

  private final String TEST_FILE1 = "src/test/resources/en_surface_forms_small.tsv";
  private final String TEST_FILE2 = "src/test/resources/small_reverse_dictionary.tsv";
  private final String TEST_INDEX_LOCATION = "src/test/resources/index";
  private LuceneDictionary dictionary;

  @Before
  public void init() throws IOException {
    FileHandlerInterface handler = new TsvFileHandler(TEST_FILE1);
    dictionary = new LuceneDictionary(null, TEST_INDEX_LOCATION);
    dictionary.clearIndex();
    dictionary.putAll(handler);
  }

  @After
  public void end() throws IOException {
    dictionary.clearIndex();
    dictionary.close();
  }

  //TODO: find a way to avoid code duplication (see HashMapDictionaryTest)
  @Test
  public void get_TestTwoWords() {
    String uri = "http://dbpedia.org/resource/Bill_Gates";
    String nGram = "bill gates";
    Assert.assertThat(dictionary.get(nGram), hasItem(uri));
  }

  @Test
  public void get_TestOneWord() {
    String uri = "http://dbpedia.org/ontology/birthPlace";
    String nGram = "birthplace";
    Assert.assertThat(dictionary.get(nGram), hasItem(uri));

    uri = "http://dbpedia.org/ontology/spouse";
    nGram = "wife";
    Assert.assertThat(dictionary.get(nGram), hasItem(uri));
  }

  @Test
  public void putAll_NewEntries() throws IOException {
    String nGram = "hitchenko";
    String uri = "http://dbpedia.org/resource/Andriy_Hitchenko";
    Assert.assertThat(dictionary.get(nGram), not(hasItem(uri)));

    FileHandlerInterface reverseHandler = new ReverseTsvFileHandler(TEST_FILE2);
    dictionary.putAll(reverseHandler);
    //Assert.assertThat(dictionary.get(nGram), hasItem(uri));

    uri = "http://dbpedia.org/ontology/spouse";
    nGram = "wife";
    Assert.assertThat(dictionary.get(nGram), hasItem(uri));
  }

  @Test
  public void clearIndex_ClearsEntries() throws IOException {
    String nGram = "birthplace";
    String uri = "http://dbpedia.org/ontology/birthPlace";
    Assert.assertThat(dictionary.get(nGram), hasItem(uri));
    dictionary.clearIndex();
    Assert.assertThat(dictionary.get(nGram), not(hasItem(uri)));
  }

}
