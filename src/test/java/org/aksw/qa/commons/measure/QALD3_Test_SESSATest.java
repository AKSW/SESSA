package org.aksw.qa.commons.measure;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.hawk.datastructures.Answer;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.dbpedia.keywordsearch.Initializer.initializer;
import org.dbpedia.keywordsearch.Initializer.interfaces.InitializerInterface;
import org.dbpedia.keywordsearch.datastructures.ListFunctions;
import org.dbpedia.keywordsearch.datastructures.ResultDataStruct;
import org.dbpedia.keywordsearch.ngramgenerator.NGramModel;
import org.dbpedia.keywordsearch.ngramgenerator.interfaces.NGramInterface;
import org.dbpedia.keywordsearch.propagator.propagator;
import org.dbpedia.keywordsearch.propagator.interfaces.PropagatorInterface;
import org.dbpedia.keywordsearch.urimapper.Mapper;
import org.dbpedia.keywordsearch.urimapper.interfaces.MapperInterface;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QALD3_Test_SESSATest {
	static Logger log = LoggerFactory.getLogger(QALD3_Test_SESSA.class);	
	private static Qald_SESSA_Init sessainit = new Qald_SESSA_Init();
	
	
	public static Answer getSessaResults(IQuestion q){
		
		Answer answer = new Answer();
		answer.answerStr = new HashSet<String>();
		String keywords = "";
		for (String str : q.getLanguageToKeywords().get("en")) {
			keywords =  keywords + " " + str;
		}
		
		NGramInterface ngram = new NGramModel();
		
		
		ngram.CreateNGramModel(keywords);
		
		
//		System.out.println("keywords--------------------------------");
//		System.out.println(keywords);	
		
		
		//SESSA results
		
		MapperInterface mappings = new Mapper();
		
		mappings.BuildMappings(sessainit.esnode, ngram.getNGramMod());
		
		InitializerInterface init = new initializer();
		init.initiate(mappings.getMappings(), ngram.getNGramMod());
		PropagatorInterface getFinalResults = new propagator();
		getFinalResults.PropagateInit(sessainit.graphdb.getgdbservice(), init.getResultsList());
		
		ListFunctions.sortresults(init.getResultsList());
		
		
		int i;
		for (i = init.getResultsList().size() - 1; i >= 0; i--) {
			
			//JSONArray json = JSONArray.put(init.getResultsList().get(i));			
			ResultDataStruct rds = init.getResultsList().get(i);
			
			
			if(answer.answerStr.contains(rds.getURI()) != true){	
				answer.answerStr.add(rds.getURI());
			}
				
		}
			answer.question_id = Integer.parseInt(q.getId());
			answer.question = q.getLanguageToQuestion().get("en");
		return answer;
	}
	
	
	@Test
	public void test() throws Exception {
		sessainit.init();
//		double average = 0;
		double count = 0;
		double countNULLAnswer = 0;
		Dataset data = Dataset.QALD3_Test_dbpedia;
		List<Answer> resultsList = new ArrayList<Answer>();
		
		List<IQuestion> questions = LoaderController.load(data);
			if (questions == null) {
				System.out.println("Dataset null" + data.toString());
			} else if (questions.size() == 0) {
				System.out.println("Dataset Empty" + data.toString());
			} else {
			for (IQuestion q : questions) {	
			    try {

					Answer a = getSessaResults(q);
						resultsList.add(a);
							if (a.answerStr.isEmpty()) {
								log.warn("Question#" + q.getId() + " returned no answers! (Q: " + q.getLanguageToQuestion().get("en") + ")");
								++countNULLAnswer;
								continue;
							}
							
							++count;
					
			        //do something with 'source'
			    	} catch (Exception e) { // catch any exception
			    		++countNULLAnswer;
			    		System.out.println("keywords--------------------------------");
			    		System.out.println(q.getLanguageToKeywords().get("en"));	
			    		e.printStackTrace();
			    		log.warn("Question#" + q.getId() + " returned no answers! (Q: " + q.getLanguageToQuestion().get("en") + ")" + "Exception thrown  :" + e);
			    		continue; // will just skip this iteration and jump to the next
			    	}					
			    }

			}
		log.info("Number of totally questionsTotally: " + questions.size());
		log.info("Number of questions with answer: " + count + ", number of questions without answer: " + countNULLAnswer);		
		}
}
