package org.aksw.sessa.candidate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.aksw.sessa.query.models.NGramHierarchy;

/**
 * Created by Simon Bordewisch on 08.06.17.
 * Given a mapping of n-grams to URIs,
 * provides a mapping of URI-candidates for given n-gram hierarchy.
 */
public class CandidateGenerator {

  private Map<String, Set<String>> candidateEntities;

  /**
   * Initialize with a mapping of n-grams to URIs.
   *
   * @param candidateEntities mapping of n-grams to URIs
   */
  public CandidateGenerator(Map<String, Set<String>> candidateEntities) {
    this.candidateEntities = candidateEntities;
  }

  /**
   * Given a n-gram hierarchy, provides the candidates for all n-grams.
   * In this process, the children will also be pruned of candidates
   * which already present in their parents.
   *
   * @param nGramHierarchy n-gram hierarchy, for which the candidates should be found
   */
  public Map<NGramEntryPosition, Set<String>> getCandidateMapping(NGramHierarchy nGramHierarchy) {
    Map<NGramEntryPosition, Set<String>> candidateMap = new HashMap<>();

    // first iteration: only add to candidateMap
    for (NGramEntryPosition nGram : nGramHierarchy.getAllPositions()) {
      Set<String> nGramMappings;
      if (candidateEntities.containsKey(nGramHierarchy.getNGram(nGram))) {
        nGramMappings = candidateEntities.get(nGramHierarchy.getNGram(nGram));
      } else {
        nGramMappings = new HashSet<>();
      }
      candidateMap.put(nGram, nGramMappings);
    }

    // second iteration: prune from children
    for (NGramEntryPosition parent : candidateMap.keySet()) {
      for (NGramEntryPosition child : parent.getAllDescendants()) {
        Set<String> parentCandidates = candidateMap.get(parent);
        Set<String> childCandidates = candidateMap.get(child);
        childCandidates.removeAll(parentCandidates);
      }
    }
    return candidateMap;
  }

}
