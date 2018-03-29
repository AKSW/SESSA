package org.aksw.sessa.importing.dictionary.implementation;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.empty;

import java.io.IOException;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.ReverseTsvFileHandler;
import org.aksw.sessa.importing.dictionary.FileBasedDictionary;
import org.aksw.sessa.candidate.Candidate;
import org.junit.Assert;
import org.junit.Test;

public abstract class FileBasedDictionaryTest {

  final String TEST_FILE1 = "src/test/resources/en_surface_forms_small.tsv";
  final String TEST_FILE2 = "src/test/resources/small_reverse_dictionary.tsv";
  FileBasedDictionary dictionary;

  @Test
  public void get_TestTwoWords_Bill() {
    String uri = "http://dbpedia.org/resource/Bill_Gates";
    String nGram = "bill gates";
    Candidate candidate = new Candidate(uri, nGram);
    Assert.assertThat(dictionary.get(nGram), hasItem(candidate));
  }

  @Test
  public void get_TestTwoWords_barack() {
    String uri = "http://dbpedia.org/resource/Barack_Obama";
    String nGram = "Barack Obama".toLowerCase();
    Candidate candidate = new Candidate(uri, nGram);
    Assert.assertThat(dictionary.get(nGram), hasItem(candidate));
  }

  @Test
  public void get_TestOneWord() {
    String uri = "http://dbpedia.org/ontology/birthPlace";
    String nGram = "birthplace";
    Candidate candidate = new Candidate(uri, nGram);
    Assert.assertThat(dictionary.get(nGram), hasItem(candidate));

    uri = "http://dbpedia.org/ontology/spouse";
    nGram = "wife";
    candidate = new Candidate(uri, nGram);
    Assert.assertThat(dictionary.get(nGram), hasItem(candidate));
  }

  @Test
  public void get_TestNoMatch() {
    String nGram = "DoesNotExist";
    Assert.assertThat(dictionary.get(nGram), empty());
  }

  @Test
  public void putAll_NewEntries() throws IOException {
    String nGram = "hitchenko";
    String uri = "http://dbpedia.org/resource/Andriy_Hitchenko";
    Candidate candidate = new Candidate(uri, nGram);
    Assert.assertThat(dictionary.get(nGram), hasItem(candidate));

    FileHandlerInterface reverseHandler = new ReverseTsvFileHandler(TEST_FILE2);
    dictionary.putAll(reverseHandler);
    candidate = new Candidate(uri, nGram);
    Assert.assertThat(dictionary.get(nGram), hasItem(candidate));

    uri = "http://dbpedia.org/ontology/spouse";
    nGram = "wife";
    candidate = new Candidate(uri, nGram);
    Assert.assertThat(dictionary.get(nGram), hasItem(candidate));
  }
}
