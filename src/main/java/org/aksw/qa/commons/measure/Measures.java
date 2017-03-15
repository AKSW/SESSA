package org.aksw.qa.commons.measure;

import java.util.Set;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.hawk.controller.EvalObj;
import org.aksw.hawk.datastructures.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Measures {
	static Logger log = LoggerFactory.getLogger(Measures.class);


	
	public static EvalObj measureIQuestion(final Answer answer, final IQuestion q) {
		// calculate precision, recall, f1 measure for each answer
		Set<String> answerSet = answer.answerStr;

		System.out.println("AnswerType--------------------------------");
		System.out.println(q.getAnswerType());
		
			
			double precision = AnswerBasedEvaluation.precision(answerSet, q);
			System.out.println("precision--------------------------------");
			System.out.println(precision);
			double recall = AnswerBasedEvaluation.recall(answerSet, q);
			System.out.println("recall--------------------------------");
			System.out.println(recall);
			double fMeasure = AnswerBasedEvaluation.fMeasure(precision, recall);
			System.out.println("fMeasure--------------------------------");
			System.out.println(fMeasure);

			log.debug("Measure @" + q.getLanguageToQuestion().get("en") + "P=" + precision + " R=" + recall + " F=" + fMeasure);
			EvalObj eval = new EvalObj(q.getId(), q.getLanguageToQuestion().get("en"), fMeasure, precision, recall, "Measure @" + q.getLanguageToQuestion().get("en"), answer);
		
		return eval;
	}
}
