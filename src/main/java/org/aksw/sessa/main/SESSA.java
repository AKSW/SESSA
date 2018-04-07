package org.aksw.sessa.main;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.candidate.Candidate;
import org.aksw.sessa.candidate.CandidateGenerator;
import org.aksw.sessa.colorspreading.ColorSpreader;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.importing.config.ConfigurationInitializer;
import org.aksw.sessa.importing.config.exception.MalformedConfigurationException;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.importing.dictionary.FileBasedDictionary;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.implementation.HashMapDictionary;
import org.aksw.sessa.importing.dictionary.implementation.LuceneDictionary;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.aksw.sessa.query.models.NGramHierarchy;
import org.aksw.sessa.query.models.QAModel;
import org.aksw.sessa.query.processing.post.PostProcessing;
import org.aksw.sessa.query.processing.pre.QueryProcessingInterface;
import org.aksw.sessa.query.processing.pre.implementation.SimpleQueryProcessing;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of project SESSA, which returns answers to asked questions.
 */
public class SESSA {

  private static final Logger log = LoggerFactory.getLogger(SESSA.class);

  private static final String DICTIONARY_TYPE = "dictionary.type";
  private static final String LUCENE_OVERRIDE_KEY = "dictionary.lucene.override_on_start";

  private FileBasedDictionary dictionary;
  private QueryProcessingInterface queryProcess;

  private Configuration configuration;


  public SESSA() throws MalformedConfigurationException {
    this(ConfigurationInitializer.getConfiguration());
  }


  public SESSA(Configuration configuration) throws MalformedConfigurationException {
    queryProcess = new SimpleQueryProcessing();
    this.configuration = configuration;
    dictionary = initDictionary();
  }

  public void loadFileToDictionary(FileHandlerInterface handler) {
    dictionary.putAll(handler);
  }


  public void addFilter(Filter filter) {
    dictionary.addFilter(filter);
  }

  public void setEnergyFunction(EnergyFunctionInterface function) {
    dictionary.setEnergyFunction(function);
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
      QAModel qaModel = new QAModel();
      qaModel.setQuestion(question);
      NGramHierarchy nGramHierarchy = queryProcess.processQuery(question);
      qaModel.setNGramHierarchy(nGramHierarchy);
      CandidateGenerator canGen = new CandidateGenerator(dictionary);
      Map<NGramEntryPosition, Set<Candidate>> canMap = canGen.getCandidateMapping(nGramHierarchy);
      qaModel.setCandidateMap(canMap);
      log.debug("Candidate map content:");
      for (Entry<NGramEntryPosition, Set<Candidate>> entry : canMap.entrySet()) {
        NGramEntryPosition pos = entry.getKey();
        log.debug("\t{}='{}'=>{}",
            pos,
            nGramHierarchy.getNGram(pos.getLength(), pos.getPosition()),
            entry.getValue());
      }
      ColorSpreader colorSpreader = new ColorSpreader(canMap);
      colorSpreader.spreadColors();
      log.debug("{}", colorSpreader.getGraph());
      qaModel.setResults(colorSpreader.getResult());
      PostProcessing postProc = new PostProcessing();
      qaModel = postProc.process(qaModel);
      Set<String> stringResults = new HashSet<>();
      for (Node result : qaModel.getResults()) {
        stringResults.add(result.getContent().toString());
      }
      return stringResults;
    }
  }

  /**
   * This method is mainly for testing purposes. It returns the graph for a given question
   *
   * @param question question for which a graph should be returned
   * @return fully colored graph
   */
  GraphInterface getGraphFor(String question) {
    NGramHierarchy nGramHierarchy = queryProcess.processQuery(question);
    CandidateGenerator canGen = new CandidateGenerator(dictionary);
    Map<NGramEntryPosition, Set<Candidate>> canMap = canGen.getCandidateMapping(nGramHierarchy);
    log.debug("Candidate map content:");
    for (Entry<NGramEntryPosition, Set<Candidate>> entry : canMap.entrySet()) {
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

  QAModel[] getQAModels(String question) {
    if (question.equals("")) {
      return null;
    } else {
      QAModel qaModel = new QAModel();
      qaModel.setQuestion(question);
      NGramHierarchy nGramHierarchy = queryProcess.processQuery(question);
      qaModel.setNGramHierarchy(nGramHierarchy);
      CandidateGenerator canGen = new CandidateGenerator(dictionary);
      Map<NGramEntryPosition, Set<Candidate>> canMap = canGen.getCandidateMapping(nGramHierarchy);
      qaModel.setCandidateMap(canMap);
      log.debug("Candidate map content:");
      for (Entry<NGramEntryPosition, Set<Candidate>> entry : canMap.entrySet()) {
        NGramEntryPosition pos = entry.getKey();
        log.debug("\t{}='{}'=>{}",
            pos,
            nGramHierarchy.getNGram(pos.getLength(), pos.getPosition()),
            entry.getValue());
      }
      ColorSpreader colorSpreader = new ColorSpreader(canMap);
      colorSpreader.spreadColors();
      log.debug("{}", colorSpreader.getGraph());
      qaModel.setGraph(colorSpreader.getGraph());
      qaModel.setResults(colorSpreader.getResult());
      PostProcessing postProc = new PostProcessing();
      QAModel postQaModel = postProc.process(qaModel);
      return new QAModel[]{qaModel, postQaModel};
    }
  }

  private FileBasedDictionary initDictionary()
      throws MalformedConfigurationException {
    switch (configuration.getString(DICTIONARY_TYPE)) {
      case "lucene":
        LuceneDictionary dict = new LuceneDictionary();
        if (configuration.getBoolean(LUCENE_OVERRIDE_KEY)) {
          dict.clearIndex();
        }
        return dict;
      case "hashmap":
        return new HashMapDictionary();
      default:
        throw new MalformedConfigurationException(
            String.format("Could not determine value of property '%s'", DICTIONARY_TYPE));
    }
  }
}