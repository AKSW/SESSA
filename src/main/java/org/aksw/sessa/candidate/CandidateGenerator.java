package org.aksw.sessa.candidate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.query.models.Candidate;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.aksw.sessa.query.models.NGramHierarchy;

/**
 * Created by Simon Bordewisch on 08.06.17. Given a mapping of n-grams to URIs, provides a mapping
 * of URI-candidates for given n-gram hierarchy.
 */
public class CandidateGenerator {

  private DictionaryInterface dictionary;

  /**
   * Initialize with a mapping of n-grams to URIs.
   *
   * @param dictionary mapping of n-grams to URIs
   */
  public CandidateGenerator(DictionaryInterface dictionary) {
    this.dictionary = dictionary;
  }

  /**
   * Given a n-gram hierarchy, provides the candidates for all n-grams. In this process, the
   * children will also be pruned of candidates which already present in their parents.
   *
   * @param nGramHierarchy n-gram hierarchy, for which the candidates should be found
   */
  public Map<NGramEntryPosition, Set<Candidate>> getCandidateMapping(
      NGramHierarchy nGramHierarchy) {
    Map<NGramEntryPosition, Set<Candidate>> candidateMap = new HashMap<>();

    // first iteration: only add to candidateMap
    for (NGramEntryPosition nGram : nGramHierarchy.getAllPositions()) {
      Set<Candidate> nGramMappings;
      String nGram2 = nGramHierarchy.getNGram(nGram);
      nGramMappings = dictionary.get(nGram2);
      if (nGramMappings == null) {
        nGramMappings = new HashSet<>();
      }
      candidateMap.put(nGram, nGramMappings);
    }

    // second iteration: prune from children
    for (NGramEntryPosition parent : candidateMap.keySet()) {
      for (NGramEntryPosition child : parent.getAllDescendants()) {
        Set<Candidate> parentCandidates = candidateMap.get(parent);
        Set<Candidate> childCandidates = candidateMap.get(child);
        childCandidates.removeAll(parentCandidates);
      }
    }
    return candidateMap;
  }

}
