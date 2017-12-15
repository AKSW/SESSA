package org.aksw.sessa.importing.dictionary.filter;

import static org.hamcrest.CoreMatchers.hasItem;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class LevenshteinDistanceFilterTest {

  @Test
  public void testFilter(){
    LevenshteinDistanceFilter filter = new LevenshteinDistanceFilter(2);
    Set<Entry<String, String>> testSet = new HashSet<>();
    SimpleEntry<String, String> exactEntry = new SimpleEntry<String, String>("stadium", "http://dbpedia.org/resource/Stadium");
    testSet.add(exactEntry);
    testSet.add(new SimpleEntry<String, String>("stadiumx", "http://dbpedia.org/resource/StadiumX"));
    testSet.add(new SimpleEntry<String, String>("stadium 2", "http://dbpedia.org/resource/Stadium2"));
    testSet.add(new SimpleEntry<String, String>("stadiums", "http://dbpedia.org/resource/Stadiums"));
    Assert.assertThat(filter.filter("stadium", testSet), hasItem(exactEntry));
  }
}
