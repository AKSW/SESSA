package org.aksw.sessa.main;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.candidate.CandidateGenerator;
import org.aksw.sessa.colorspreading.ColorSpreader;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.importing.dictionary.FileBasedDictionaryInterface;
import org.aksw.sessa.importing.dictionary.implementation.HashMapDictionary;
import org.aksw.sessa.importing.dictionary.implementation.LuceneDictionary;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.aksw.sessa.query.models.NGramHierarchy;
import org.aksw.sessa.query.processing.QueryProcessingInterface;
import org.aksw.sessa.query.processing.implementation.SimpleQueryProcessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of project SESSA, which returns answers to asked questions.
 */
public class SESSA {

  private DictionaryInterface dictionary;
  private QueryProcessingInterface queryProcess;
  private static final Logger log = LoggerFactory.getLogger(SESSA.class);

  /**
   *
   */

  public SESSA() {
    queryProcess = new SimpleQueryProcessing();
  }

  public void loadFileToHashMapDictionary(FileHandlerInterface handler){
    if (dictionary == null) {
      dictionary = new HashMapDictionary(handler);
    } else if (dictionary instanceof HashMapDictionary) {
      ((FileBasedDictionaryInterface) dictionary).putAll(handler);
    }
  }

  public void loadFileToLuceneDictionary(FileHandlerInterface handler) {
    if (dictionary == null) {
      dictionary = new LuceneDictionary(handler);
    } else if (dictionary instanceof LuceneDictionary) {
      ((LuceneDictionary) dictionary).putAll(handler);
    }
  }

  /**
   * This method tries to answer the given question using the method described in the <a href=
   * "https://docs.google.com/viewer?a=v&pid=sites&srcid=ZGVmYXVsdGRvbWFpbnxubGl3b2QyMDE0fGd4Ojc5NjU1YjhhMzNhMDczNWI"
   * >corresponding paper</a>. The answer is a set of strings containing the URIs with the highest
   * likelihood to be the answer (i.e. with the highest explanation score).
   *
   * @param question the question that should be answered (for now keyword based, i.e. 'birthplace
   * bill gates wife' instead of "Where was Bill Gates' wife born")
   * @return set of URIs with the highest explanation score
   */
  public Set<String> answer(String question) {
    if (question.equals("")) {
      return null;
    } else {
      NGramHierarchy nGramHierarchy = queryProcess.processQuery(question);
      CandidateGenerator canGen = new CandidateGenerator(dictionary);
      Map<NGramEntryPosition, Set<String>> canMap = canGen.getCandidateMapping(nGramHierarchy);
      log.debug("Candidate map content:");
      for (Entry<NGramEntryPosition, Set<String>> entry : canMap.entrySet()) {
        NGramEntryPosition pos = entry.getKey();
        log.debug("\t{}='{}'=>{}",
            pos,
            nGramHierarchy.getNGram(pos.getLength(), pos.getPosition()),
            entry.getValue());
      }
      ColorSpreader colorSpreader = new ColorSpreader(canMap);
      colorSpreader.spreadColors();
      log.debug("{}", colorSpreader.getGraph());
      Set<Node> results = colorSpreader.getResult();
      Set<String> stringResults = new HashSet<>();
      for (Node result : results) {
        stringResults.add(result.getContent().toString());
      }
      return stringResults;
    }
  }

  /**
   * This method is mainly for testing purposes.
   * It returns the graph for a given question
   * @param question question for which a graph should be returned
   * @return fully colored graph
   */
  GraphInterface getGraphFor(String question){
    NGramHierarchy nGramHierarchy = queryProcess.processQuery(question);
    CandidateGenerator canGen = new CandidateGenerator(dictionary);
    Map<NGramEntryPosition, Set<String>> canMap = canGen.getCandidateMapping(nGramHierarchy);
    log.debug("Candidate map content:");
    for (Entry<NGramEntryPosition, Set<String>> entry : canMap.entrySet()) {
      NGramEntryPosition pos = entry.getKey();
      log.debug("\t{}='{}'=>{}",
          pos,
          nGramHierarchy.getNGram(pos.getLength(), pos.getPosition()),
          entry.getValue());
    }
    ColorSpreader colorSpreader = new ColorSpreader(canMap);
    colorSpreader.spreadColors();
    return colorSpreader.getGraph();
  }
}
