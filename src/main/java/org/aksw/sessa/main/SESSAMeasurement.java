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
		String RDF_labels = "src/main/resources/labels_en.ttl";
		String RDF_ontology = "src/main/resources/dbpedia_2016-10.nt";
		sessa.loadFileToDictionaryRDF(RDF_labels);
		sessa.loadFileToDictionaryRDF(RDF_ontology);
//    sessa.loadFileToDictionaryReverseTSV("src/main/resources/dictionary.tsv");
		log.info("Finished building Dictionary");
		System.gc();
	}

	public static void main(String[] args) {
		SESSAMeasurement myMess = new SESSAMeasurement();
		// for (Dataset d : Dataset.values()) {
		Dataset qald7TrainMultilingual = Dataset.QALD7_Train_Multilingual;
		List<IQuestion> questions = LoaderController.load(qald7TrainMultilingual);
		double avg_fmeasure = 0;
		int numberOfUsableAnswers = 0;
		double answer_fmeasure = 0;
		for (IQuestion q : questions) {
			List<String> x = q.getLanguageToKeywords().get("en");
			String keyphrase = Joiner.on(" ").join(x);
			log.info("{}", x);
			Set<String> answers = myMess.sessa.answer(keyphrase);
			log.info("\tSESSA: {}", answers);
			log.info("\tGOLD: {}", q.getGoldenAnswers());
			double fmeasure = AnswerBasedEvaluation.fMeasure(answers, q);
			log.info("\t==> {}", fmeasure);
			avg_fmeasure += fmeasure;
			if(fmeasure > 0){
				numberOfUsableAnswers++;
				answer_fmeasure += fmeasure;
			}
		}
		log.info("Final average F-measure: {}", avg_fmeasure / questions.size());
		log.info("Final F-measure for questions which where at least partially answered: {}",
				answer_fmeasure / numberOfUsableAnswers);
	}

}
