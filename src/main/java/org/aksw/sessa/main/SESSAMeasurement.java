package org.aksw.sessa.main;

import java.io.File;
import java.util.List;
import java.util.Set;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.measure.AnswerBasedEvaluation;
import org.aksw.sessa.helper.files.saver.ReversedTsvDictionarySaver;
import org.apache.jena.ext.com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Simon Bordewisch on 27.07.17.
 */
public class SESSAMeasurement {

  private SESSA sessa;
  public final String REVERSE_TSV_FILE = "src/main/resources/dictionary.tsv";
  private static final Logger log = LoggerFactory.getLogger(SESSAMeasurement.class);

  private String RDF_labels = "src/main/resources/dbpedia_3Eng_class.ttl";
  private String RDF_ontology = "src/main/resources/dbpedia_3Eng_property.ttl";

  public SESSAMeasurement() {
    sessa = new SESSA();
    checkDictionary();
    log.info("Importing dictionary. This could take some time!");
    long startTime = System.nanoTime();
    sessa.loadFileToDictionaryReverseTSV(REVERSE_TSV_FILE);
    System.gc();
    long endTime = System.nanoTime();
    log.info("Finished importing Dictionary (in {}sec).",
        (endTime - startTime) / (1000 * 1000 * 1000));
  }

  private void checkDictionary() {
    File f = new File(REVERSE_TSV_FILE);
    if (f.exists() && !f.isDirectory()) {
      log.info("Found reverse tsv dictionary.");
    } else {
      log.info("Reverse tsv dictionary not found!");
      log.info("Building Dictionary. This could take some time!");
      long startTime = System.nanoTime();
      ReversedTsvDictionarySaver.saveDictionary(REVERSE_TSV_FILE,
          RDF_labels, RDF_ontology);
      long endTime = System.nanoTime();
      log.info("Finished building Dictionary (in {}sec).",
          (endTime - startTime) / (1000 * 1000 * 1000));
      System.gc();
    }
  }

  public static void main(String[] args) {
    SESSAMeasurement myMess = new SESSAMeasurement();
    long startTime = System.nanoTime();
    // for (Dataset d : Dataset.values()) {
    Dataset qald7TrainMultilingual = Dataset.QALD7_Train_Multilingual;
    List<IQuestion> questions = LoaderController.load(qald7TrainMultilingual);
    double avgFMeasure = 0;
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
        avgFMeasure += fMeasure;
        numberOfQuestions++;
        if (fMeasure > 0) {
          numberOfUsableAnswers++;
          answer_fmeasure += fMeasure;
        }
      }
    }
    long endTime = System.nanoTime();
    log.info("Finished questioning (in {}sec).", (endTime - startTime) / (1000 * 1000 * 1000));
    log.info("Number of questions asked: {}.", numberOfQuestions);
    log.info("Final average F-measure: {}", avgFMeasure / numberOfQuestions);
    log.info("Number of partially right answered questions: {}.", numberOfUsableAnswers);
    log.info("Final F-measure for questions which where at least partially answered correct: {}",
        answer_fmeasure / numberOfUsableAnswers);
  }

}
