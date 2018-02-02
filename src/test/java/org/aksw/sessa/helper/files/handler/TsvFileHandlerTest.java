package org.aksw.sessa.helper.files.handler;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;

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
public class TsvFileHandlerTest {

  private final String FILE = "src/test/resources/en_surface_forms_small.tsv";
  private Set<Entry<String, String>> entrySet;

  @Before
  public void init() throws Exception {
    entrySet = new HashSet<>();
    FileHandlerInterface handler = new TsvFileHandler(FILE);
    for (Entry<String, String> entry = handler.nextEntry(); entry != null;
        entry = handler.nextEntry()) {
      entrySet.add(entry);
    }
    handler.close();
  }


  @Test
  public void testNextEntry_testFirstEntry() throws Exception {
    Entry<String, String> expected = new SimpleEntry<>(
        "Asiya bint Muzahim".toLowerCase(),
        "http://dbpedia.org/resource/Asiya,_wife_of_the_Pharaoh");
    Assert.assertThat(entrySet, hasItem(expected));
  }

  @Test
  public void testNextEntry_testLastEntry() throws Exception {
    Entry<String, String> expected = new SimpleEntry<>(
        "fiance",
        "http://dbpedia.org/ontology/spouse");
    Assert.assertThat(entrySet, hasItem(expected));
  }

  @Test
  public void testNextEntry_testSomeMiddleEntry() throws Exception {
    Entry<String, String> expected = new SimpleEntry<>(
        "Stratonice".toLowerCase(),
        "http://dbpedia.org/resource/Stratonice_(wife_of_Antigonus)");
    Assert.assertThat(entrySet, hasItem(expected));
  }

  @Test
  public void testNextEntry_testEntryWithTwoValues() throws Exception {
    String values = "http://dbpedia.org/ontology/birthPlace";
    Entry<String, String> expected1 = new SimpleEntry<>(
        "birthplace",
        values);
    Entry<String, String> expected2 = new SimpleEntry<>(
        "place of birth",
        values);
    Assert.assertThat(entrySet, hasItems(expected1, expected2));
  }

}