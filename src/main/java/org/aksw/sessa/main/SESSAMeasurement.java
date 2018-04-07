package org.aksw.sessa.main;

import com.google.common.base.Joiner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.measure.AnswerBasedEvaluation;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.RdfFileHandler;
import org.aksw.sessa.helper.files.handler.ReverseTsvFileHandler;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.aksw.sessa.importing.config.ConfigurationInitializer;
import org.aksw.sessa.importing.config.exception.MalformedConfigurationException;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.energy.LevenshteinDistanceFunction;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Simon Bordewisch on 27.07.17.
 */
public class SESSAMeasurement {

  private static final Logger log = LoggerFactory.getLogger(SESSAMeasurement.class);
  private static final String FILES_KEY = "dictionary.files.location";


  private SESSA sessa;

  public SESSAMeasurement() throws MalformedConfigurationException {
    long startTime = System.nanoTime();
    BaseHierarchicalConfiguration configuration = ConfigurationInitializer.getConfiguration();
    log.info("Building dictionary from files. This could take some time!");
    sessa = new SESSA(configuration);
    loadDictionaries(sessa, configuration);
    long endTime = System.nanoTime();
    log.info("Finished importing dictionary (in {}sec).",
        (endTime - startTime) / (1000 * 1000 * 1000));
    addFiltersAndEnergyFunction();
  }


  public static void main(String[] args) throws MalformedConfigurationException {
    SESSAMeasurement myMess = new SESSAMeasurement();
    long startTime = System.nanoTime();
    Dataset qaldTrainMultilingual = Dataset.QALD3_Test_dbpedia;
    List<IQuestion> questions = LoaderController.load(qaldTrainMultilingual);
    double avgFMeasure = 0;
    int numberOfQuestions = 0;
    double answerFMeasure = 0;
    Set<String> questionsAnswered = new HashSet<>();
    for (IQuestion q : questions) {
      if (q.getAnswerType().matches("resource")) {
        List<String> x = q.getLanguageToKeywords().get("en");
        String keyphrase = Joiner.on(" ").join(x);
        log.info("{}", x);
        Set<String> answers = myMess.sessa.answer(keyphrase);
        log.info("\tSESSA: {}", answers);
        log.info("\tGOLD: {}", q.getGoldenAnswers());
        double fMeasure = AnswerBasedEvaluation.fMeasure(answers, q);
        log.info("\t==> {}", fMeasure);
        avgFMeasure += fMeasure;
        numberOfQuestions++;
        if (fMeasure > 0) {
          questionsAnswered.add(keyphrase);
          answerFMeasure += fMeasure;
        }
      }
    }
    long endTime = System.nanoTime();
    log.info("Finished questioning (in {}sec).", (endTime - startTime) / (1000 * 1000 * 1000));
    log.info("Number of questions asked: {}.", numberOfQuestions);
    log.info("Final average F-measure: {}", avgFMeasure / numberOfQuestions);
    log.debug("Number of partially right answered questions: {}.", questionsAnswered.size());
    log.debug("The questions are: {}", questionsAnswered);
    log.debug("Final F-measure for questions which where at least partially answered correct: {}",
        answerFMeasure / questionsAnswered.size());
  }

  /**
   * Use this method for adding filters and manipulate the energy score.
   */
  private void addFiltersAndEnergyFunction() {
    Filter lFilter = new Filter(new LevenshteinDistanceFunction(), 5);
    //Filter pFilter = new Filter(new PageRankFunction(), 3);

    EnergyFunctionInterface lFunction = new LevenshteinDistanceFunction();
    sessa.addFilter(lFilter);
    sessa.setEnergyFunction(lFunction);
    //sessa.addFilter(pRFilter);
  }

  private void loadDictionaries(SESSA sessa, BaseHierarchicalConfiguration configuration) {
    HierarchicalConfiguration subConfig = configuration.configurationAt(FILES_KEY);
    if (subConfig.containsKey("rdf")) {
      log.info("Found entry for rdf-files in configuration file. Importing...");
      loadSingleDictionary(sessa, new RdfFileHandler(), subConfig.getString("rdf"));
    }
    if (subConfig.containsKey("tsv")) {
      log.info("Found entry for tsv-files in configuration file. Importing...");
      loadSingleDictionary(sessa, new TsvFileHandler(), subConfig.getString("tsv"));
    }
    if (subConfig.containsKey("reverse_tsv")) {
      log.info("Found entry for reverse tsv-files in configuration file. Importing...");
      loadSingleDictionary(sessa, new ReverseTsvFileHandler(), subConfig.getString("reverse_tsv"));
    }
  }


  private void loadSingleDictionary(SESSA sessa, FileHandlerInterface handler,
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

}
