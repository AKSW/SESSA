package org.aksw.qa.commons.measure;

import java.util.List;
import java.util.Set;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.measure.QALD4_EvaluationUtils;
import org.aksw.hawk.controller.EvalObj;
import org.aksw.hawk.datastructures.Answer;
import org.aksw.hawk.datastructures.HAWKQuestion;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class Measures {
	static Logger log = LoggerFactory.getLogger(Measures.class);

	public static List<EvalObj> measure(final List<Answer> rankedAnswer, final HAWKQuestion q, final int maxK) {
		// calculate precision, recall, f1 measure for each answer
		List<EvalObj> list = Lists.newArrayList();

		for (Answer answer : rankedAnswer) {
			Set<RDFNode> answerSet = answer.answerSet;
			double precision = QALD4_EvaluationUtils.precision(answerSet, q);
			System.out.println(precision);
			double recall = QALD4_EvaluationUtils.recall(answerSet, q);
			double fMeasure = QALD4_EvaluationUtils.fMeasure(answerSet, q);
			//System.out.println("Measure @" + (list.size() + 1) + "P=" + precision + " R=" + recall + " F=" + fMeasure);
			log.debug("Measure @" + (list.size() + 1) + "P=" + precision + " R=" + recall + " F=" + fMeasure);
			list.add(new EvalObj(q.getId(), q.getLanguageToQuestion().get("en"), fMeasure, precision, recall, "Measure @" + (list.size() + 1), answer));

			// only calculate top k measures
			if (list.size() > maxK) {
				break;
			}
		}
		return list;
	}
	
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
