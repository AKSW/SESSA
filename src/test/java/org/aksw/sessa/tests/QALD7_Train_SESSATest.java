//package org.aksw.sessa.tests;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//import org.aksw.hawk.datastructures.Answer;
//import org.aksw.qa.commons.datastructure.IQuestion;
//import org.aksw.qa.commons.load.Dataset;
//import org.aksw.qa.commons.load.LoaderController;
//import org.aksw.sessa.main.Initializer.Initializer;
//import org.aksw.sessa.main.Initializer.interfaces.InitializerInterface;
//import org.aksw.sessa.main.datastructures.ListFunctions;
//import org.aksw.sessa.main.datastructures.ResultDataStruct;
//import org.aksw.sessa.main.ngramgenerator.NGramModel;
//import org.aksw.sessa.main.ngramgenerator.interfaces.NGramInterface;
//import org.aksw.sessa.main.Propagator.Propagator;
//import org.aksw.sessa.main.Propagator.interfaces.PropagatorInterface;
//import org.aksw.sessa.main.urimapper.Mapper;
//import org.aksw.sessa.main.urimapper.interfaces.MapperInterface;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class QALD7_Train_SESSATest {
//	static Logger log = LoggerFactory.getLogger(QALD7_Train_SESSA.class);
//	private static Qald_SESSA_Init sessainit = new Qald_SESSA_Init();
//
//
//	public static Answer getSessaResults(IQuestion q){
//
//		Answer answer = new Answer();
//		answer.answerStr = new HashSet<String>();
//		String keywords = "";
//		for (String str : q.getLanguageToKeywords().get("en")) {
//			keywords =  keywords + "" + str;
//		}
//
//		NGramInterface ngram = new NGramModel();
//
//
//		ngram.createNGramModel(keywords);
//
//
//		System.out.println("keywords--------------------------------");
//		System.out.println(keywords);
//
//
//		//SESSA results
//
//		MapperInterface mappings = new Mapper();
//
//		mappings.BuildMappings(sessainit.esnode, ngram.getNGramMod());
//
//		InitializerInterface init = new Initializer();
//		init.initiate(mappings.getMappings(), ngram.getNGramMod());
//		PropagatorInterface getFinalResults = new Propagator();
//		getFinalResults.PropagateInit(sessainit.graphdb.getgdbservice(), init.getResultsList());
//
//		ListFunctions.sortresults(init.getResultsList());
//
//
//		int i;
//		for (i = init.getResultsList().size() - 1; i >= 0; i--) {
//
//			//JSONArray json = JSONArray.put(init.getResultsList().get(i));
//			ResultDataStruct rds = init.getResultsList().get(i);
//
//
//			if(answer.answerStr.contains(rds.getURI()) != true){
//				answer.answerStr.add(rds.getURI());
//			}
//
//		}
//			answer.question_id = Integer.parseInt(q.getId());
//			answer.question = q.getLanguageToQuestion().get("en");
//		return answer;
//	}
//
//
//	@Test
//	public void test() throws Exception {
//		sessainit.init();
////		double average = 0;
//		double count = 0;
//		double countNULLAnswer = 0;
//		Dataset data = Dataset.QALD7_Train_Multilingual;
//		List<Answer> resultsList = new ArrayList<Answer>();
//
//		List<IQuestion> questions = LoaderController.load(data);
//			if (questions == null) {
//				System.out.println("Dataset null" + data.toString());
//			} else if (questions.size() == 0) {
//				System.out.println("Dataset Empty" + data.toString());
//			} else {
//			for (IQuestion q : questions) {
//			    try {
//					Answer a = getSessaResults(q);
//						resultsList.add(a);
//							if (a.answerStr.isEmpty()) {
//								log.warn("Question#" + q.getId() + " returned no answers! (Q: " + q.getLanguageToQuestion().get("en") + ")");
//								++countNULLAnswer;
//								continue;
//							}
//
//						++count;
//
//
//			    	} catch (Exception e) { // catch any exception
//			    		++countNULLAnswer;
//			    		System.out.println("keywords--------------------------------");
//			    		System.out.println(q.getLanguageToKeywords().get("en"));
//			    		e.printStackTrace();
//			    		log.warn("Question#" + q.getId() + " returned no answers! (Q: " + q.getLanguageToQuestion().get("en") + ")" + "Exception thrown  :" + e);
//			    		continue; // will just skip this iteration and jump to the next
//			    	}
//			}
//			}
//			log.info("Number of totally questionsTotally: " + questions.size());
//			log.info("Number of questions with answer: " + count + ", number of questions without answer: " + countNULLAnswer);
//		}
//}
