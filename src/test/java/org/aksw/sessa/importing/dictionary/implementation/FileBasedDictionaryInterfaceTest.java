package org.aksw.sessa.importing.dictionary.implementation;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;

import java.io.IOException;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.ReverseTsvFileHandler;
import org.aksw.sessa.importing.dictionary.FileBasedDictionaryInterface;
import org.junit.Assert;
import org.junit.Test;

public abstract class FileBasedDictionaryInterfaceTest {

  FileBasedDictionaryInterface dictionary;
  final String TEST_FILE1 = "src/test/resources/en_surface_forms_small.tsv";
  final String TEST_FILE2 = "src/test/resources/small_reverse_dictionary.tsv";

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
    Assert.assertThat(dictionary.get(nGram), hasItem(uri));

    uri = "http://dbpedia.org/ontology/spouse";
    nGram = "wife";
    Assert.assertThat(dictionary.get(nGram), hasItem(uri));
  }
}
