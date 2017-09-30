package org.aksw.sessa.main;

import java.util.List;
import java.util.Set;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.measure.AnswerBasedEvaluation;

/**
 * Created by Simon Bordewisch on 27.07.17.
 */
public class SESSAMeasurement {

	private SESSA sessa;

	public SESSAMeasurement() {
		sessa = new SESSA();

		String RDF_labels = "src/main/resources/labels_en.ttl";
		String RDF_ontology = "src/main/resources/dbpedia_2016-10.nt";
		sessa.loadFileToDictionaryRDF(RDF_labels);
		sessa.loadFileToDictionaryRDF(RDF_ontology);
		System.gc();
	}

	public static void main(String[] args) {
		SESSAMeasurement myMess = new SESSAMeasurement();
		// for (Dataset d : Dataset.values()) {
		Dataset qald7TrainMultilingual = Dataset.QALD7_Train_Multilingual;
		List<IQuestion> questions = LoaderController.load(qald7TrainMultilingual);

		for (IQuestion q : questions) {
			String x = q.getLanguageToQuestion().get("en");
			Set<String> answers = myMess.sessa.answer(x);
			System.out.println(x);
			System.out.println("\t SESSA: " + answers);
			System.out.println("\t GOLD:  " + q.getGoldenAnswers());
			double fmeasure = AnswerBasedEvaluation.fMeasure(answers, q);
			System.out.println("\t==> " + fmeasure);

		}
	}

}
