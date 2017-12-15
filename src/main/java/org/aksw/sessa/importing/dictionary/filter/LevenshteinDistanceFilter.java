package org.aksw.sessa.importing.dictionary.filter;

import java.util.Map.Entry;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;

public class LevenshteinDistanceFilter extends AbstractFilter{


  public LevenshteinDistanceFilter(int numberOfResults) {
    super(numberOfResults);
  }

  @Override
  protected float getRank(String keyword, Entry<String, String> entry) {
    LuceneLevenshteinDistance levenshtein = new LuceneLevenshteinDistance();
    float rank = levenshtein.getDistance(keyword, entry.getKey());
    return rank;
  }
}
