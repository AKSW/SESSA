package org.aksw.sessa.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;
import org.aksw.sessa.candidate.Candidate;
import org.aksw.sessa.candidate.CandidateGenerator;
import org.aksw.sessa.colorspreading.ColorSpreader;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.RdfFileHandler;
import org.aksw.sessa.helper.files.handler.ReverseTsvFileHandler;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.importing.config.ConfigurationInitializer;
import org.aksw.sessa.importing.config.exception.MalformedConfigurationException;
import org.aksw.sessa.importing.dictionary.FileBasedDictionary;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.energy.LevenshteinDistanceFunction;
import org.aksw.sessa.importing.dictionary.energy.PageRankFunction;
import org.aksw.sessa.importing.dictionary.implementation.HashMapDictionary;
import org.aksw.sessa.importing.dictionary.implementation.LuceneDictionary;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.aksw.sessa.query.models.NGramHierarchy;
import org.aksw.sessa.query.models.QAModel;
import org.aksw.sessa.query.processing.post.PostProcessing;
import org.aksw.sessa.query.processing.pre.QueryProcessingInterface;
import org.aksw.sessa.query.processing.pre.implementation.SimpleQueryProcessing;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of project SESSA, which returns answers to asked questions.
 */
public class SESSA {

  private static final Logger log = LoggerFactory.getLogger(SESSA.class);

  private static final String DICTIONARY_TYPE = "dictionary.type";
  private static final String ENERGY_FUNCTION_KEY = "dictionary.energy_function";
  private static final String FILES_KEY = "dictionary.files.location";
  private static final String FILTER_LIMITS_KEY = "dictionary.filter.limits";
  private static final String FILTER_NAMES_KEY = "dictionary.filter.names";
  private static final String LUCENE_LOCATION_KEY = "dictionary.lucene.location";
  private static final String LUCENE_OVERRIDE_KEY = "dictionary.lucene.override_on_start";

  private FileBasedDictionary dictionary;
  private QueryProcessingInterface queryProcess;

  private Configuration configuration;


  /**
   * Loads SESSA with default configuration or configuration given via application param.
   */
  public SESSA() throws MalformedConfigurationException {
    this(ConfigurationInitializer.getConfiguration());
  }


  /**
   * Loads SESSA with the given configuration. All dictionaries in the configuration will also be
   * imported.
   *
   * @param configuration configuration that should be applied to SESSA
   * @throws MalformedConfigurationException is thrown when the configuration file is malformed
   */
  public SESSA(BaseHierarchicalConfiguration configuration) throws MalformedConfigurationException {
    long startTime = System.nanoTime();
    queryProcess = new SimpleQueryProcessing();
    this.configuration = configuration;
    dictionary = initDictionary();

    if (!configuration.getBoolean(LUCENE_OVERRIDE_KEY) &&
        dictionary.size() > 0) {
      log.info("Skipping building dictionary.");
    } else {
      log.info("Building dictionary from files. This could take some time!");
      loadDictionaries(configuration);
      long endTime = System.nanoTime();
      log.info("Finished importing dictionary (in {}sec).",
          (endTime - startTime) / (1000 * 1000 * 1000));
    }
    addFilters(configuration);
    applyEnergyFunction(configuration);
  }

  /**
   * Loads given file (in the handler) to the dictionary.
   *
   * @param handler Handler that contains file supported by the handler
   */
  public void loadFileToDictionary(FileHandlerInterface handler) {
    dictionary.putAll(handler);
  }


  /**
   * Adds a filter to the dictionary.
   *
   * @param filter the filter that should be applied to the dictionary results before returning the
   * candidates.
   */
  public void addFilter(Filter filter) {
    dictionary.addFilter(filter);
  }

  /**
   * Sets the applied energy function.
   *
   * @param function energy function to be applied to all nodes
   */
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
        log.info("Using Lucene Dictionary.");
        LuceneDictionary dict = new LuceneDictionary();
        if (configuration.getBoolean(LUCENE_OVERRIDE_KEY)) {
          log.debug("Application configured to delete index on startup. Deleting...");
          dict.clearIndex();
        }
        return dict;
      case "hashmap":
        log.info("Using HashMap-based Dictionary.");
        return new HashMapDictionary();
      default:
        throw new MalformedConfigurationException(
            String.format("Could not determine value of property '%s'", DICTIONARY_TYPE));
    }
  }


  private void loadDictionaries(BaseHierarchicalConfiguration configuration) {
    HierarchicalConfiguration subConfig = configuration.configurationAt(FILES_KEY);
    if (subConfig.containsKey("rdf")) {
      log.info("Found entry for rdf-files in configuration file. Importing...");
      loadSingleDictionary(new RdfFileHandler(), subConfig.getString("rdf"));
    }
    if (subConfig.containsKey("tsv")) {
      log.info("Found entry for tsv-files in configuration file. Importing...");
      loadSingleDictionary(new TsvFileHandler(), subConfig.getString("tsv"));
    }
    if (subConfig.containsKey("reverse_tsv")) {
      log.info("Found entry for reverse tsv-files in configuration file. Importing...");
      loadSingleDictionary(new ReverseTsvFileHandler(), subConfig.getString("reverse_tsv"));
    }
  }


  private void loadSingleDictionary(FileHandlerInterface handler,
      String pathString) {
    log.debug("Path to files is '{}'", pathString);
    try (Stream<Path> path = Files.walk(Paths.get(pathString))) {
      path
          .filter(Files::isRegularFile)
          .forEach((Path file) -> {
            try {
              log.info("Loading file '{}' to dictionary via {}.",
                  file.toString(),
                  handler.getClass().getSimpleName());
              handler.loadFile(file.toString());
              loadFileToDictionary(handler);
            } catch (IOException ioE) {
              log.error(ioE.getLocalizedMessage());
            }
          });
    } catch (IOException ioE) {
      log.warn(
          "Could not load any file in given path '{}'.", pathString);
    }
  }

  /**
   * Use this method for adding filters and manipulate the energy score.
   */
  private void addFilters(BaseHierarchicalConfiguration configuration)
      throws MalformedConfigurationException {
    String[][] filters = new String[2][];
    filters[0] = configuration.getStringArray(FILTER_NAMES_KEY);
    filters[1] = configuration.getStringArray(FILTER_LIMITS_KEY);
    if (filters[0].length < filters[1].length) {
      log.info(
          "The number of filters and limits in the configuration is not the same! Using smaller value.");
    }
    int length = filters[0].length < filters[1].length ? filters[0].length : filters[1].length;
    for (int i = 0; i < length; i++) {
      EnergyFunctionInterface function = getFunction(filters[0][i]);
      int limit;
      try {
        limit = Integer.parseInt(filters[1][i]);
        log.debug("\t Limit is {}", limit);
      } catch (NumberFormatException nfE) {
        throw new MalformedConfigurationException(String
            .format("Error in %s: Given limit is not an integer. #%d:%s", FILTER_LIMITS_KEY, i,
                filters[1][i]));
      }
      Filter filter = new Filter(function, limit);
      addFilter(filter);
    }
  }

  private EnergyFunctionInterface getFunction(String functionName)
      throws MalformedConfigurationException {
    switch (functionName) {
      case "levenshtein":
        log.debug("Add Levenshtein filter.");
        return new LevenshteinDistanceFunction();
      case "pagerank":
        log.debug("Add PageRank filter.");
        return new PageRankFunction();
      default:
        throw new MalformedConfigurationException(
            String.format("Could not determine value of property '%s'. Given value: %s",
                FILTER_NAMES_KEY, functionName));
    }
  }


  private void applyEnergyFunction(BaseHierarchicalConfiguration configuration)
      throws MalformedConfigurationException {
    String energyFunctionName = configuration.getString(ENERGY_FUNCTION_KEY);
    EnergyFunctionInterface lFunction = getFunction(energyFunctionName);
    setEnergyFunction(lFunction);
  }
}