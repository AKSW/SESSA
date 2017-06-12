package org.aksw.sessa.main;

import org.aksw.sessa.query.models.NGramHierarchy;
import org.aksw.sessa.query.processing.QueryProcessingInterface;
import org.aksw.sessa.query.processing.implementation.SimpleQueryProcessing;

/**
 * Main class of project SESSA, which returns answers to asked questions.
 */
public class SESSA {

  public SESSA() {

  }

  public String answer(String question){
    if (question.equals("")) {
      return "";
    } else {

      return null;
    }
  }
}
