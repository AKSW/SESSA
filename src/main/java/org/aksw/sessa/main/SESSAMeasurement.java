package org.aksw.sessa.main;

import com.google.common.base.Joiner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.measure.AnswerBasedEvaluation;
import org.aksw.sessa.helper.files.handler.RdfFileHandler;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.energy.LevenshteinDistanceFunction;
import org.aksw.sessa.importing.dictionary.implementation.LuceneDictionary;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Simon Bordewisch on 27.07.17.
 */
public class SESSAMeasurement {

  private static final Logger log = LoggerFactory.getLogger(SESSAMeasurement.class);
  private static final String PROPERTIES_FILE = "application.properties";
  private static final String DEFAULT_PATH_KEY = "dictionary_files.rdf.location";
  private static final String DEFAULT_PATH = "src/main/resources";


  private SESSA sessa;
  private String RDF_labels = "src/main/resources/dbpedia_3Eng_class.ttl";
  private String RDF_ontology = "src/main/resources/dbpedia_3Eng_property.ttl";
  private String RDF_DBpedia_Ontology = "src/main/resources/dbpedia_2016-10.nt";
  private String RDF_DBpedia_Labels = "src/main/resources/labels_en.ttl";
  private Properties properties;

  public SESSAMeasurement() {
    sessa = new SESSA();
    long startTime = System.nanoTime();
    properties = new Properties(loadProperties());
    log.info("Building Lucene Dictionary from RDF files. This could take some time!");
    try (Stream<Path> paths = Files.walk(Paths.get(DEFAULT_PATH))) {
      paths
          .filter(Files::isRegularFile)
          .forEach(path -> {
            try {
              sessa.loadFileToLuceneDictionary(new RdfFileHandler(path.toString()));
            } catch (IOException ioE) {
              log.error(ioE.getLocalizedMessage());
            }
          });
    } catch (IOException ioE) {
      log.error(ioE.getLocalizedMessage());
      log.info("Could not load any file. Trying to intiate dictionary from previous run instead.");
      sessa.loadFileToLuceneDictionary(null);
    }

    long endTime = System.nanoTime();
    log.info("Finished importing Lucene Dictionary (in {}sec).",
        (endTime - startTime) / (1000 * 1000 * 1000));
    addFiltersAndEnergyFunction();
  }

  public static void main(String[] args) {
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

  private Properties loadProperties() {
    log.info("Trying to load properties file...");
    Properties properties = new Properties();
    // load default value
    properties.setProperty(DEFAULT_PATH_KEY, DEFAULT_PATH);

    // load properties from file
    try(FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
      log.info("Properties file found! Loading values...");
      properties.load(in);
    } catch (FileNotFoundException fnfE) {
      log.info("Properties file not found. Using default values instead.");
      log.debug("Path to properties file was {}", PROPERTIES_FILE);
    } catch(IOException ioE) {
      log.error(ioE.getLocalizedMessage());
      log.error("Using default values instead.");
    }
    return properties;
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

}
