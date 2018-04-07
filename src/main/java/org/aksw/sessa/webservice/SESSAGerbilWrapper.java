package org.aksw.sessa.webservice;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.RdfFileHandler;
import org.aksw.sessa.importing.config.exception.MalformedConfigurationException;
import org.aksw.sessa.importing.dictionary.energy.EnergyFunctionInterface;
import org.aksw.sessa.importing.dictionary.energy.LevenshteinDistanceFunction;
import org.aksw.sessa.importing.dictionary.util.Filter;
import org.aksw.sessa.main.SESSA;
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
  private SESSA sessa;
  private String RDF_labels = "src/main/resources/dbpedia_3Eng_class.ttl";
  private String RDF_ontology = "src/main/resources/dbpedia_3Eng_property.ttl";
  private String RDF_DBpedia_Ontology = "src/main/resources/dbpedia_2016-10.nt";
  private String RDF_DBpedia_Labels = "src/main/resources/labels_en.ttl";

  /**
   * Initialize SESSA with the standard dictionary files if there is no Lucene index already.
   */
  public SESSAGerbilWrapper() throws MalformedConfigurationException {
    sessa = new SESSA();
    long startTime = System.nanoTime();
    try {
      log.info("No Lucene Dictionary found.");
      log.info("Building Lucene Dictionary from RDF files. This could take some time!");
      //Change the handler and the file to be handled here
      sessa.loadFileToDictionary(new RdfFileHandler(RDF_labels));
      sessa.loadFileToDictionary(new RdfFileHandler(RDF_ontology));
      sessa.loadFileToDictionary(new RdfFileHandler(RDF_DBpedia_Ontology));
      sessa.loadFileToDictionary(new RdfFileHandler(RDF_DBpedia_Labels));
      long endTime = System.nanoTime();
      log.info("Finished importing Lucene Dictionary (in {}sec).",
          (endTime - startTime) / (1000 * 1000 * 1000));
    } catch (IOException e) {
      log.error(e.getLocalizedMessage());
    }
    addFiltersAndEnergyFunction();
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
