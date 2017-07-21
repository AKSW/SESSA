package org.aksw.sessa.main;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import org.aksw.sessa.candidate.CandidateGenerator;
import org.aksw.sessa.colorspreading.ColorSpreader;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.aksw.sessa.importing.dictionary.implementation.TsvDictionaryImport;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.aksw.sessa.query.models.NGramHierarchy;

/**
 * Main class of project SESSA, which returns answers to asked questions.
 */
public class SESSA {
  
  private Map<String, Set<String>> dictionary;

  public SESSA(String fileName) {
    DictionaryImportInterface dictImporter = new TsvDictionaryImport();
    dictionary = dictImporter.getDictionary(fileName);
  }

  public Set<String> answer(String question){
    if (question.equals("")) {
      return null;
    } else {
      NGramHierarchy nGramHierarchy = new NGramHierarchy(question);
      CandidateGenerator canGen = new CandidateGenerator(dictionary);
      Map<NGramEntryPosition,Set<String>> canMap = canGen.getCandidateMapping(nGramHierarchy);
      ColorSpreader colorSpreader = new ColorSpreader(canMap);
      colorSpreader.spreadColors();
      Set<Node> results = colorSpreader.getResult();
      System.out.println(colorSpreader.getGraph());
      Set<String> stringResults = new HashSet<>();
      for(Node result : results){
        stringResults.add(result.getContent().toString());
      }
      return stringResults;
    }
  }
}
