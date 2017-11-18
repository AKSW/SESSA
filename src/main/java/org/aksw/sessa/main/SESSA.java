package org.aksw.sessa.main;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.candidate.CandidateGenerator;
import org.aksw.sessa.colorspreading.ColorSpreader;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.RdfFileHandler;
import org.aksw.sessa.helper.files.handler.ReverseTsvFileHandler;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.aksw.sessa.importing.dictionary.FileBasedDictionaryImport;
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

  private DictionaryImportInterface dictionary;
  private QueryProcessingInterface queryProcess;
  private static final Logger log = LoggerFactory.getLogger(SESSA.class);

  /**
   *
   */

  public SESSA() {
    queryProcess = new SimpleQueryProcessing();
  }

  /**
   * loading of files to a dictionary there by reading all rdfs:labels from a file
   */
  private void loadFileToDictionaryRDF(String file) {
    log.info("Loading RDF file '{}' to dictionary.", file);
    try (FileHandlerInterface handler = new RdfFileHandler(file)) {
      if (dictionary == null) {
        dictionary = new HashMapDictionary(handler);
      } else if (dictionary instanceof HashMapDictionary) {
        ((FileBasedDictionaryImport) dictionary).putAll(handler);
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }


  /**
   * Loads a dictionary from a tsv file which contains a dictionary with URIs as key and keywords to
   * the URI as values (tab separated). This is needed for the candidate mapping.
   *
   * @param file tsv file which contains mapping.
   */

  public void loadFileToDictionaryTSV(String file) {
    log.info("Loading TSV file '{}' to dictionary.", file);
    try (FileHandlerInterface handler = new TsvFileHandler(file)) {
      if (dictionary == null) {
        dictionary = new HashMapDictionary(handler);
      } else if (dictionary instanceof HashMapDictionary) {
        ((FileBasedDictionaryImport) dictionary).putAll(handler);
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }


  public void loadFileToDictionaryReverseTSV(String file) {
    log.info("Loading reverse TSV file '{}' to dictionary.", file);
    try (FileHandlerInterface handler = new ReverseTsvFileHandler(file)) {
      if (dictionary == null) {
        dictionary = new LuceneDictionary(handler);
      } else if (dictionary instanceof HashMapDictionary) {
        ((FileBasedDictionaryImport) dictionary).putAll(handler);
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
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
      for(Entry entry:  canMap.entrySet()){
        log.debug("\t{}", entry);
      }
      ColorSpreader colorSpreader = new ColorSpreader(canMap);
      colorSpreader.spreadColors();
      Set<Node> results = colorSpreader.getResult();
      Set<String> stringResults = new HashSet<>();
      for (Node result : results) {
        stringResults.add(result.getContent().toString());
      }
      return stringResults;
    }
  }
}
