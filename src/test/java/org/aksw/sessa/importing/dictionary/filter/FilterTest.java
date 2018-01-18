package org.aksw.sessa.importing.dictionary.filter;

import static org.hamcrest.core.IsCollectionContaining.hasItem;

import java.util.HashSet;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.energy.LevenshteinDistanceFunction;
import org.aksw.sessa.importing.dictionary.energy.PagerRankFunction;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.aksw.sessa.query.models.Candidate;
import org.junit.Assert;
import org.junit.Test;

public class FilterTest {

    @Test
  public void testFilter_WithLevenshtein(){
    Filter filter = new Filter(new LevenshteinDistanceFunction(),2);
    Set<Candidate> testSet = new HashSet<>();
    Candidate exactEntry = new Candidate("http://dbpedia.org/resource/Stadium", "stadium");
    testSet.add(exactEntry);
    testSet.add(new Candidate("http://dbpedia.org/resource/StadiumX","stadiumx"));
    testSet.add(new Candidate("http://dbpedia.org/resource/Stadium2","stadium 2"));
    testSet.add(new Candidate("http://dbpedia.org/resource/Stadiums","stadiums"));
    Assert.assertThat(filter.filter("stadium", testSet), hasItem(exactEntry));
  }

    @Test
  public void testFilter_WithPageRank(){
      Filter filter = new Filter(new PagerRankFunction(),2);
      Set<Candidate> testSet = new HashSet<>();
      Candidate exactEntry = new Candidate("http://dbpedia.org/resource/Stadium", "stadium");
      testSet.add(exactEntry);
      testSet.add(new Candidate("http://dbpedia.org/resource/StadiumX","stadiumx"));
      testSet.add(new Candidate("http://dbpedia.org/resource/Stadium2","stadium 2"));
      testSet.add(new Candidate("http://dbpedia.org/resource/Stadiums","stadiums"));
      testSet.add(new Candidate("http://dbpedia.org/resource/Stardium","stardium"));
      testSet.add(new Candidate("http://dbpedia.org/resource/Stadium_mk","stadium_mk"));
      Assert.assertThat(filter.filter("stadium", testSet), hasItem(exactEntry));
  }

}
