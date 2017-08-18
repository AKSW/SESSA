package org.aksw.sessa.main;

import java.util.Set;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.measure.AnswerBasedEvaluation;

/**
 * Created by Simon Bordewisch on 27.07.17.
 */
public class SESSAMeasurement {

  private SESSA sessa;

  public SESSAMeasurement(){
    sessa = new SESSA("src/test/resources/en_surface_forms.tsv");
  }
  public double precision(IQuestion question){
    Set<String> answers = sessa.answer(question.getLanguageToQuestion().get("en"));
    return AnswerBasedEvaluation.precision(answers,question);
  }

  public double recall(IQuestion question){
    Set<String> answers = sessa.answer(question.getLanguageToQuestion().get("en"));
    return AnswerBasedEvaluation.recall(answers,question);
  }

  public double fMeasure(IQuestion question){
    Set<String> answers = sessa.answer(question.getLanguageToQuestion().get("en"));
    return AnswerBasedEvaluation.fMeasure(answers,question);
  }

}
