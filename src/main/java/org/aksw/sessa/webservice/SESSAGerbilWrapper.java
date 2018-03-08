package org.aksw.sessa.webservice;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.RdfFileHandler;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.energy.LevenshteinDistanceFunction;
import org.aksw.sessa.importing.dictionary.implementation.LuceneDictionary;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.aksw.sessa.main.SESSA;
import org.dice.qa.AbstractQASystem;
import org.dice.qa.AnswerContainer;
import org.dice.qa.AnswerContainer.AnswerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SESSAGerbilWrapper extends AbstractQASystem {

  private static final Logger log = LoggerFactory.getLogger(SESSAGerbilWrapper.class);
  private SESSA sessa;
  private String RDF_labels = "src/main/resources/dbpedia_3Eng_class.ttl";
  private String RDF_ontology = "src/main/resources/dbpedia_3Eng_property.ttl";
  private String RDF_DBpedia_Ontology = "src/main/resources/dbpedia_2016-10.nt";
  private String RDF_DBpedia_Labels = "src/main/resources/labels_en.ttl";

  public SESSAGerbilWrapper() {
    sessa = new SESSA();
    long startTime = System.nanoTime();
    try {
      Path path = FileSystems.getDefault().getPath(LuceneDictionary.DEFAULT_PATH_TO_INDEX);
      if (!Files.exists(path)) {
        log.info("No Lucene Dictionary found.");
        log.info("Building Lucene Dictionary from RDF files. This could take some time!");
        //Change the handler and the file to be handled here
        sessa.loadFileToLuceneDictionary(new RdfFileHandler(RDF_labels));
        sessa.loadFileToLuceneDictionary(new RdfFileHandler(RDF_ontology));
        sessa.loadFileToLuceneDictionary(new RdfFileHandler(RDF_DBpedia_Ontology));
        sessa.loadFileToLuceneDictionary(new RdfFileHandler(RDF_DBpedia_Labels));
        long endTime = System.nanoTime();
        log.info("Finished importing Lucene Dictionary (in {}sec).",
            (endTime - startTime) / (1000 * 1000 * 1000));
      } else {
        log.info(
            "Found existing Lucene Dictionary. If you want to build a new one, delete the dictionary!");
        sessa.loadFileToLuceneDictionary(null);
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
    addFiltersAndEnergyFunction();
  }

  @Override
  public AnswerContainer retrieveAnswers(String question, String lang) {
    //Create an empty container for your answers
    AnswerContainer answers = new AnswerContainer();

    //Create your answers as a Set
    Set<String> answerSet = new HashSet<>();
    /**
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

  @Override
  public void close(){
    super.close();
    //This will called as soon as the system will be shutdown
    //Use it to open resources etc.
  }

  /**
   * Use this method for adding filters and manipulate the energy score.
   */
  private void addFiltersAndEnergyFunction() {
    Filter lFilter = new Filter(new LevenshteinDistanceFunction(), 5);
    //Filter pFilter = new Filter(new PagerRankFunction(), 3);

    EnergyFunctionInterface lFunction = new LevenshteinDistanceFunction();
    sessa.addFilter(lFilter);
    sessa.setEnergyFunction(lFunction);
    //sessa.addFilter(pRFilter);

  }
}
