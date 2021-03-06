package org.aksw.sessa.candidate;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsNot.not;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.importing.dictionary.implementation.SimpleMapDictionary;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.aksw.sessa.query.models.NGramHierarchy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 08.06.17.
 */
public class CandidateGeneratorTest {

  CandidateGenerator candidateGenerator;
  Map<NGramEntryPosition, Set<Candidate>> candidateMapping;

  HashSet<String> billGates;
  HashSet<String> wife;
  HashSet<String> spouse;
  HashSet<String> birthPlace;
  HashSet<String> gates;

  @Before
  public void initialize() {
    Map<String, Set<String>> candidateEntities = new HashMap<>();
    spouse = new HashSet<>();
    spouse.add("dbo:spouse");
    candidateEntities.put("spouse", spouse);
    candidateEntities.put("husband", spouse);

    wife = new HashSet<>();
    wife.add("dbo:spouse");
    wife.add("dbpedia:Wife");
    wife.add("dbpedia:The_Wife");
    candidateEntities.put("wife", wife);

    billGates = new HashSet<>();
    billGates.add("dbr:Bill_Gates");
    candidateEntities.put("bill gates", billGates);

    gates = new HashSet<>(billGates);
    gates.add("dbpedia:The_Gates");
    candidateEntities.put("gates", gates);

    birthPlace = new HashSet<>();
    birthPlace.add("dbo:birthplace");
    birthPlace.add("dbo:Place_of_birth");

    candidateEntities.put("birthplace", birthPlace);

    candidateGenerator = new CandidateGenerator(new SimpleMapDictionary(candidateEntities));

    NGramHierarchy runningExample = new NGramHierarchy("birthplace bill gates wife");

    candidateMapping = candidateGenerator.getCandidateMapping(runningExample);

  }

  @Test
  public void testGet_BillGatesContent() {
    Set<Candidate> candidates = candidateMapping.get(new NGramEntryPosition(2, 1));
    Candidate billGates = new Candidate("dbr:Bill_Gates", "bill gates");
    Assert.assertThat(candidates, hasItem(billGates));
  }

  @Test
  public void testGet_PrunedGatesContent() {
    // dbr:Bill_Gates pruned, because "bill gates" is father of "gates"
    Set<Candidate> candidates = candidateMapping.get(new NGramEntryPosition(1, 2));
    Candidate billGates = new Candidate("dbr:Bill_Gates", "bill gates");
    Candidate noBillGates = new Candidate("dbpedia:The_Gates", "gates");
    Assert.assertThat(candidates, hasItem(noBillGates));
    Assert.assertThat(candidates, not(hasItem(billGates)));
  }

}
