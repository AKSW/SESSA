package org.aksw.sessa.importing.dictionary.filter;

import static org.hamcrest.core.IsCollectionContaining.hasItem;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.energy.LevenshteinDistanceFunction;
import org.aksw.sessa.importing.dictionary.energy.PagerRankFunction;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.junit.Assert;
import org.junit.Test;

public class FilterTest {

    @Test
  public void testFilter_WithLevenshtein(){
    Filter filter = new Filter(new LevenshteinDistanceFunction(),2);
    Set<Entry<String, String>> testSet = new HashSet<>();
    SimpleEntry<String, String> exactEntry = new SimpleEntry<>("stadium", "http://dbpedia.org/resource/Stadium");
    testSet.add(exactEntry);
    testSet.add(new SimpleEntry<>("stadiumx", "http://dbpedia.org/resource/StadiumX"));
    testSet.add(new SimpleEntry<>("stadium 2", "http://dbpedia.org/resource/Stadium2"));
    testSet.add(new SimpleEntry<>("stadiums", "http://dbpedia.org/resource/Stadiums"));
    Assert.assertThat(filter.filter("stadium", testSet), hasItem(exactEntry));
  }

    @Test
  public void testFilter_WithPageRank(){
      Filter filter = new Filter(new PagerRankFunction(),2);
    Set<Entry<String, String>> testSet = new HashSet<>();
    SimpleEntry<String, String> exactEntry = new SimpleEntry<>("stadium", "http://dbpedia.org/resource/Stadium");
    testSet.add(exactEntry);
    testSet.add(new SimpleEntry<>("stadiumx", "http://dbpedia.org/resource/StadiumX"));
    testSet.add(new SimpleEntry<>("stadium 2", "http://dbpedia.org/resource/Stadium_2"));
    testSet.add(new SimpleEntry<>("stadiums", "http://dbpedia.org/resource/Stadiums"));
    testSet.add(new SimpleEntry<>("stardium", "http://dbpedia.org/resource/Stardium"));
    testSet.add(new SimpleEntry<>("stadium_mk", "http://dbpedia.org/resource/Stadium_mk"));
    Assert.assertThat(filter.filter("stadium", testSet), hasItem(exactEntry));
  }

}
