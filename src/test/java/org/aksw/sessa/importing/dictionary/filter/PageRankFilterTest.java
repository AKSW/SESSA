package org.aksw.sessa.importing.dictionary.filter;

import static org.hamcrest.CoreMatchers.hasItem;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class PageRankFilterTest {

  @Test
  public void testFilter(){
    PageRankFilter filter = new PageRankFilter(2);
    Set<Entry<String, String>> testSet = new HashSet<>();
    SimpleEntry<String, String> exactEntry = new SimpleEntry<String, String>("stadium", "http://dbpedia.org/resource/Stadium");
    testSet.add(exactEntry);
    testSet.add(new SimpleEntry<String, String>("stadiumx", "http://dbpedia.org/resource/StadiumX"));
    testSet.add(new SimpleEntry<String, String>("stadium 2", "http://dbpedia.org/resource/Stadium_2"));
    testSet.add(new SimpleEntry<String, String>("stadiums", "http://dbpedia.org/resource/Stadiums"));
    testSet.add(new SimpleEntry<String, String>("stardium", "http://dbpedia.org/resource/Stardium"));
    testSet.add(new SimpleEntry<String, String>("stadium_mk", "http://dbpedia.org/resource/Stadium_mk"));
    Assert.assertThat(filter.filter("stadium", testSet), hasItem(exactEntry));
  }
}
