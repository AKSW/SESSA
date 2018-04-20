package org.aksw.sessa.webservice;

import java.util.HashSet;
import java.util.Set;
import org.aksw.sessa.importing.config.exception.MalformedConfigurationException;
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

  /**
   * Initialize SESSA with the given dictionary files.
   */
  public SESSAGerbilWrapper() throws MalformedConfigurationException {
    sessa = new SESSA();
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
