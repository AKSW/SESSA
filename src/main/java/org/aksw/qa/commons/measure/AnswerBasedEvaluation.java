package org.aksw.qa.commons.measure;

import java.util.Set;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.utils.CollectionUtils;
import org.aksw.qa.commons.utils.SPARQLExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnswerBasedEvaluation {
	static Logger log = LoggerFactory.getLogger(AnswerBasedEvaluation.class);
	private static String sparqlEndpoint = "http://dbpedia.org/sparql";

	public static double precision(Set<String> systemAnswer, IQuestion question) {
		if (systemAnswer.isEmpty()) {
			return 0;
		}
		double precision = 0;


		Set<String> goldenStrings = null;
		
		if (question.getSparqlQuery() != null) {
			System.out.println("getSparqlQuery----------------------");
			System.out.println(question.getSparqlQuery());
			goldenStrings = SPARQLExecutor.executeSelect(question.getSparqlQuery(), sparqlEndpoint).getStringSet();
			
			if (isSelectType(question.getSparqlQuery())) {
				
				Set<String> intersection = CollectionUtils.intersection(goldenStrings, systemAnswer);
				System.out.println("intersection----------------------------------");
				System.out.println(intersection);

				if (systemAnswer.size() != 0) {
					precision = (double) intersection.size() / (double) systemAnswer.size();
				}
			} else if (isAskType(question.getSparqlQuery())) {
				if (systemAnswer.size() == 1) {
					String ans = systemAnswer.iterator().next();
					String goldstandardAns = goldenStrings.iterator().next();
					if (ans.toString().equals(goldstandardAns.toString())) {
						precision = 1;
					}
				}
			} else {
				log.error("Unsupported Query Type" + question.getSparqlQuery());
			}
		} else if (question.getPseudoSparqlQuery() != null) {
			goldenStrings = SPARQLExecutor.executeSelect(question.getPseudoSparqlQuery(), sparqlEndpoint).getStringSet();
			System.out.println("getPseudoSparqlQuery----------------------");
			System.out.println(question.getPseudoSparqlQuery());
			if (isSelectType(question.getPseudoSparqlQuery())) {
				Set<String> intersection = CollectionUtils.intersection(goldenStrings, systemAnswer);
				System.out.println("intersection----------------------------------");
				System.out.println(intersection);

				if (systemAnswer.size() != 0) {
					precision = (double) intersection.size() / (double) systemAnswer.size();
				}
			} else if (isAskType(question.getPseudoSparqlQuery())) {
				if (systemAnswer.size() == 1) {
					String ans = systemAnswer.iterator().next();
					String goldstandardAns = goldenStrings.iterator().next();
					if (ans.toString().equals(goldstandardAns.toString())) {
						precision = 1;
					}
				}
			} else {
				log.error("Unsupported Query Type" + question.getPseudoSparqlQuery());
			}
		}
		else{
			goldenStrings = question.getGoldenAnswers();
			Set<String> intersection = CollectionUtils.intersection(goldenStrings, systemAnswer);
			System.out.println("intersection----------------------------------");
			System.out.println(intersection);
			if (systemAnswer.size() != 0) {
				precision = (double) intersection.size() / (double) systemAnswer.size();
			}		
		}

		log.info("AnswerStrings: "+goldenStrings);
		log.info("SessaAnswer: "+systemAnswer);

		
		return precision;
	}

	public static double recall(Set<String> systemAnswer, IQuestion question) {
		if (systemAnswer.isEmpty()) {
			return 0;
		}
		double recall = 0;
		
		Set<String> goldenStrings = null;
		
		if (question.getSparqlQuery() != null) {
		
			goldenStrings = SPARQLExecutor.executeSelect(question.getSparqlQuery(), sparqlEndpoint).getStringSet();

			if (isSelectType(question.getSparqlQuery())) {
				
				// if queries contain aggregation return always 1
				if (question.getAggregation()) {
					recall = 1;
				}
				Set<String> intersection = CollectionUtils.intersection(systemAnswer, goldenStrings);
				if (goldenStrings.size() != 0) {
					recall = (double) intersection.size() / (double) goldenStrings.size();
				}
			} else if (isAskType(question.getSparqlQuery())) {
				// if queries are ASK queries return recall=1
				recall = 1;
			} else {
				log.error("Unsupported Query Type" + question.getSparqlQuery());
			}
		} else if (question.getPseudoSparqlQuery() != null) {
			goldenStrings = SPARQLExecutor.executeSelect(question.getPseudoSparqlQuery(), sparqlEndpoint).getStringSet();
			
			if (isSelectType(question.getPseudoSparqlQuery())) {
				// if queries contain aggregation return always 1
				if (question.getAggregation()) {
					recall = 1;
				}
				Set<String> intersection = CollectionUtils.intersection(systemAnswer, goldenStrings);
				if (goldenStrings.size() != 0) {
					recall = (double) intersection.size() / (double) goldenStrings.size();
				}
			} else if (isAskType(question.getPseudoSparqlQuery())) {
				// if queries are ASK queries return recall=1
				recall = 1;
			} else {
				log.error("Unsupported Query Type" + question.getPseudoSparqlQuery());
			}
		}
		else{
			goldenStrings = answersToString(question.getGoldenAnswers());
			Set<String> intersection = CollectionUtils.intersection(systemAnswer, goldenStrings);
			if (goldenStrings.size() != 0) {
				recall = (double) intersection.size() / (double) goldenStrings.size();
			}
		} 

		return recall;
	}

	public static double fMeasure(double precision, double recall) {
	
		double fMeasure = 0;
		if (precision + recall > 0) {
			fMeasure = 2 * precision * recall / (precision + recall);
		}
		return fMeasure;
	}

	private static Set<String> answersToString(Set<String> answers) {
		Set<String> tmp = CollectionUtils.newHashSet();
		for (String s : answers) {
			tmp.add(s);
		}
		return tmp;
	}
	
	public static boolean isAskType(String sparqlQuery) {
		return sparqlQuery.contains("\nASK\n") || sparqlQuery.contains("ASK ");
	}

	private static boolean isSelectType(String sparqlQuery) {
		return sparqlQuery.contains("\nSELECT\n") || sparqlQuery.contains("SELECT ");
	}

}
