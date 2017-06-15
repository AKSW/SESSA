package org.aksw.sessa.candidate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.query.models.NGramHierarchy;

/**
 * Created by Simon Bordewisch on 08.06.17.
 * Given a mapping of n-grams to URIs,
 * provides a mapping of URI-candidates for given n-gram hierarchy.
 */
public class CandidateGenerator {

  private Map<String,HashSet<String>> candidateEntities;

  /**
   * Initialize with a mapping of n-grams to URIs.
   * @param candidateEntities mapping of n-grams to URIs
   */
  public CandidateGenerator(Map<String, HashSet<String>> candidateEntities){
    this.candidateEntities = candidateEntities;
  }

  /**
   * Given a n-gram hierarchy, provides the candidates for all n-grams.
   * In this process, the children will also be pruned of candidates
   * which already present in their parents.
   * @param nGramHierarchy n-gram hierarchy, for which the candidates should be found
   */
  public Map<String,HashSet<String>> getCandidateMapping(NGramHierarchy nGramHierarchy) {
    Map<String,HashSet<String>> candidateMap = new HashMap<>();

    // first iteration: only add to candidateMap
    for(String nGram : nGramHierarchy.toStringArray()) {
      HashSet<String> nGramMappings;
      if(candidateEntities.containsKey(nGram)) {
        nGramMappings = candidateEntities.get(nGram);
      } else {
        nGramMappings = new HashSet<>();
      }
      candidateMap.put(nGram, nGramMappings);
    }

    // second iteration: prune from children
    for(int length=1; length < nGramHierarchy.getNGramLength(); length++){
      for(int index=0; index + length < nGramHierarchy.getNGramLength(); index++){
        String childNgram = nGramHierarchy.getNGram(index, length);
        String[] parents = nGramHierarchy.getParents(index, length);
        HashSet<String> childCandidates = candidateMap.get(childNgram);

        for(String parent : parents) {
          Set<String> parentCandidates = candidateMap.get(parent);
          for (String childCandidate : childCandidates) {
            if (parentCandidates.contains(childCandidate)) {
              childCandidates.remove(childCandidate);
            }
          }
        }
        candidateMap.put(childNgram, childCandidates);
      }
    }
    return candidateMap;
  }

}
