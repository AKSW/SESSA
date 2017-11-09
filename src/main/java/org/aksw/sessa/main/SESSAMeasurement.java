package org.aksw.sessa.main;

import java.util.List;
import java.util.Set;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.measure.AnswerBasedEvaluation;
import org.apache.jena.ext.com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Simon Bordewisch on 27.07.17.
 */
public class SESSAMeasurement {

  private SESSA sessa;
  private static final Logger log = LoggerFactory.getLogger(SESSAMeasurement.class);

  public SESSAMeasurement() {
    sessa = new SESSA();
    log.info("Building Dictionary");
    sessa.loadFileToDictionaryReverseTSV("src/main/resources/dictionary.tsv");
    System.gc();
  }

  public static void main(String[] args) {
    long startTime = System.nanoTime();
    SESSAMeasurement myMess = new SESSAMeasurement();
    long endTime = System.nanoTime();
    log.info("Finished building Dictionary (in {}sec).",
        (endTime - startTime) / 1000 * 1000 * 1000);
    startTime = endTime;
    // for (Dataset d : Dataset.values()) {
    Dataset qald7TrainMultilingual = Dataset.QALD7_Train_Multilingual;
    List<IQuestion> questions = LoaderController.load(qald7TrainMultilingual);
    double avg_fmeasure = 0;
    int numberOfUsableAnswers = 0;
    int numberOfQuestions = 0;
    double answer_fmeasure = 0;
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
        avg_fmeasure += fMeasure;
        numberOfQuestions++;
        if (fMeasure > 0) {
          numberOfUsableAnswers++;
          answer_fmeasure += fMeasure;
        }
      }
    }
    endTime = System.nanoTime();
    log.info("Finished questioning (in {}sec).", (endTime - startTime) / 1000 * 1000 * 1000);
    log.info("Number of questions asked: {}.", numberOfQuestions);
    log.info("Final average F-measure: {}", avg_fmeasure / numberOfQuestions);
    log.info("Number of partially right answered questions: {}.", numberOfUsableAnswers);
    log.info("Final F-measure for questions which where at least partially answered correct: {}",
        answer_fmeasure / numberOfUsableAnswers);
  }

}
