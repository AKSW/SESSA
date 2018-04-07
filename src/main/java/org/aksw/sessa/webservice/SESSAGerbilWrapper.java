package org.aksw.sessa.webservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.RdfFileHandler;
import org.aksw.sessa.helper.files.handler.ReverseTsvFileHandler;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.aksw.sessa.importing.config.ConfigurationInitializer;
import org.aksw.sessa.importing.config.exception.MalformedConfigurationException;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.energy.LevenshteinDistanceFunction;
import org.aksw.sessa.importing.dictionary.energy.PageRankFunction;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.aksw.sessa.main.SESSA;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.dice.qa.AbstractQASystem;
import org.dice.qa.AnswerContainer;
import org.dice.qa.AnswerContainer.AnswerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is acts as a wrapper for the {@link org.dice.qa.AbstractQASystem AbstractQASystem}. It
 * initializes SESSA for the web service with a dictionary.
 */
public class SESSAGerbilWrapper extends AbstractQASystem {

  private static final Logger log = LoggerFactory.getLogger(SESSAGerbilWrapper.class);
  private static final String LUCENE_OVERRIDE_KEY = "dictionary.lucene.override_on_start";
  private static final String DICTIONARY_TYPE = "dictionary.type";
  private static final String FILES_KEY = "dictionary.files.location";
  private static final String FILTER_NAMES_KEY = "dictionary.filter.names";
  private static final String FILTER_LIMITS_KEY = "dictionary.filter.limits";
  private static final String ENERGY_FUNCTION_KEY = "dictionary.energy_function";

  private SESSA sessa;

  /**
   * Initialize SESSA with the standard dictionary files if there is no Lucene index already.
   */
  public SESSAGerbilWrapper() throws MalformedConfigurationException {
    sessa = new SESSA();
    BaseHierarchicalConfiguration configuration = ConfigurationInitializer.getConfiguration();
    long startTime = System.nanoTime();
    if (configuration.getBoolean(LUCENE_OVERRIDE_KEY) &&
        configuration.getString(DICTIONARY_TYPE).equals("lucene")) {
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
              sessa.loadFileToDictionary(handler);
            } catch (IOException ioE) {
              log.error(ioE.getLocalizedMessage());
            }
          });
    } catch (IOException ioE) {
      log.error(ioE.getLocalizedMessage());
      log.info(
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
      sessa.addFilter(filter);
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
    sessa.setEnergyFunction(lFunction);
  }

  /**
   * Asks SESSA for answers and constructs the AnswerContainer
   *
   * @param question question the question that should be answered by SESSA (for now keyword based,
   * i.e. 'birthplace bill gates wife' instead of "Where was Bill Gates' wife born")
   * @param lang obsolete for now
   * @return AnswerContainer, containing the answers, the type (always 'RESOURCE') and the
   * SPARQL-query (which is null, because SESSA does not use just a single query)
   */
  @Override
  public AnswerContainer retrieveAnswers(String question, String lang) {
    //Create an empty container for your answers
    AnswerContainer answers = new AnswerContainer();

    //Create your answers as a Set
    Set<String> answerSet = new HashSet<>();
    /*
     * Here you have to actually ask your system for answers, the types and the sparql query
     */
    log.debug("Start answering question.");
    answerSet.addAll(sessa.answer(question));
    log.debug("Answers are: {}", answerSet);

    //Set your answers
    answers.setAnswers(answerSet);

    //Get the type (RESOURCE, BOOLEAN, NUMBER, DATE)
    AnswerType type = AnswerType.RESOURCE;
    answers.setType(type);

    //Set the sparql query your system used
    answers.setSparqlQuery(null);

    return answers;
  }

}
