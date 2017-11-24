package org.aksw.sessa.helper.files.handler;

import static org.hamcrest.CoreMatchers.hasItem;

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
public class ReverseTsvFileHandlerTest {

  private final String FILE = "src/test/resources/small_reverse_dictionary.tsv";
  private Set<Entry<String, String>> entrySet;

  @Before
  public void init() throws Exception{
    entrySet = new HashSet<>();
    FileHandlerInterface handler = new ReverseTsvFileHandler(FILE);
    for(Entry<String,String> entry = handler.nextEntry(); entry != null;entry = handler.nextEntry()) {
      entrySet.add(entry);
    }
    handler.close();
  }

  @Test
  public void testNextEntry_CountEntries(){
    // dictionary size is read directly from file
    final int SIZE = 9;
    Assert.assertEquals(SIZE, entrySet.size());
  }

  @Test
  public void testNextEntry_testFirstEntry() throws Exception {
    Entry<String, String> expected = new SimpleEntry<>(
        "list of food companies",
        "http://dbpedia.org/resource/List_of_food_companies");
    Assert.assertThat(entrySet, hasItem(expected));
  }

  @Test
  public void testNextEntry_testLastEntry() throws Exception {
    Entry<String, String> expected = new SimpleEntry<>(
        "hitchenko",
        "http://dbpedia.org/resource/Andriy_Hitchenko");
    Assert.assertThat(entrySet, hasItem(expected));
  }

  @Test
  public void testNextEntry_testSomeMiddleEntry() throws Exception {
    Entry<String, String> expected = new SimpleEntry<>(
        "1996 los angeles dodgers season",
        "http://dbpedia.org/resource/1996_Los_Angeles_Dodgers_season");
    Assert.assertThat(entrySet, hasItem(expected));
  }

}