package org.aksw.sessa.helper.files.handler;

import static org.hamcrest.CoreMatchers.hasItem;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 24.11.17.
 */
public class RdfFileHandlerTest {

  private final String FILE = "src/test/resources/file_sample_dbpedia_labels.nt";
  private Set<Entry<String, String>> entrySet;

  @Before
  public void init() throws Exception {
    entrySet = new HashSet<>();
    FileHandlerInterface handler = new RdfFileHandler(FILE);
    for (Entry<String, String> entry = handler.nextEntry(); entry != null;
        entry = handler.nextEntry()) {
      entrySet.add(entry);
    }
    handler.close();
  }

  @Test
  public void testNextEntry_CountEntries() {
    // dictionary size is read directly from file
    final int SIZE = 65;
    Assert.assertEquals(SIZE, entrySet.size());
  }

  @Test
  public void testNextEntry_testFirstEntry() throws Exception {
    Entry<String, String> expected = new SimpleEntry<>(
        "accessiblecomputing",
        "http://dbpedia.org/resource/AccessibleComputing");
    Assert.assertThat(entrySet, hasItem(expected));
  }

  @Test
  public void testNextEntry_testLastEntry() throws Exception {
    Entry<String, String> expected = new SimpleEntry<>(
        "a",
        "http://dbpedia.org/resource/A");
    Assert.assertThat(entrySet, hasItem(expected));
  }

  @Test
  public void testNextEntry_testEntryWithTwoWordsLiteral() throws Exception {
    Entry<String, String> expected = new SimpleEntry<>(
        "arturo vidal",
        "http://dbpedia.org/resource/Arturo_Vidal");
    Assert.assertThat(entrySet, hasItem(expected));
  }

  @Test
  public void testNextEntry_testEntryWithOtherLanguage() throws Exception {
    Entry<String, String> expected = new SimpleEntry<>(
        "abece darians",
        "http://dbpedia.org/resource/AbeceDarians");
    Assert.assertThat(entrySet, hasItem(expected));
  }

  @Test(expected = IOException.class)
  public void testIOException() throws Exception{
    new RdfFileHandler(FILE + "NotThere");
  }

}