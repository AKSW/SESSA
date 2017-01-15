package org.aksw.qa.commons.measure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.aksw.hawk.controller.EvalObj;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QALD6_Test_SESSA {
	static Logger log = LoggerFactory.getLogger(QALD6_Test_SESSA.class);
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
				//System.out.println(rds.getURI());
				answer.answerStr.add(rds.getURI());
			}
				
		}
			answer.question_id = Integer.parseInt(q.getId());
			answer.question = q.getLanguageToQuestion().get("en");
		return answer;
	}
	
	public static void QALD6_Pipeline() throws Exception{
	
		sessainit.init();
		double averagef = 0;
		double averagep = 0;
		double averager = 0;
		
		double count = 0;
		double countNULLAnswer = 0;
		double countNOTRT = 0;
		
		Dataset data = Dataset.QALD6_Test_Multilingual;
		List<Answer> resultsList = new ArrayList<Answer>();
		List<EvalObj> evallist = new ArrayList<EvalObj>();

		
		List<IQuestion> questions = LoaderController.load(data);
			if (questions == null) {
				System.out.println("Dataset null" + data.toString());
			} else if (questions.size() == 0) {
				System.out.println("Dataset Empty" + data.toString());
			} else {
			for (IQuestion q : questions) {	
			    try {
			    	System.out.println(q.getId());
					System.out.println(q.getLanguageToQuestion().get("en"));
						
						Answer a = getSessaResults(q);	
						
						resultsList.add(a);
							if (a.answerStr.isEmpty()) {
								log.warn("Question#" + q.getId() + " returned no answers! (Q: " + q.getLanguageToQuestion().get("en") + ")");
								++countNULLAnswer;
								continue;
							}
							
						++count;
						
						log.info("Measure");
						if(q.getAnswerType().equals("resource")){
							EvalObj eval = Measures.measureIQuestion(a, q);
							evallist.add(eval);
						}else{
							log.info("Not Resource based");
							++countNOTRT;
						}		
			        //do something with 'source'
			    	} catch (Exception e) { // catch any exception
			    		++countNULLAnswer;
			    		System.out.println("keywords--------------------------------");
			    		System.out.println(q.getLanguageToKeywords().get("en"));	
			    		log.warn("Question#" + q.getId() + " returned no answers! (Q: " + q.getLanguageToQuestion().get("en") + ")" + "because of Exception");
			    		continue; // will just skip this iteration and jump to the next
			    	}	
			    }
			
			for (EvalObj e : evallist) {
				averagep += e.getPmax();
				averager += e.getRmax();
				averagef += e.getFmax();
			}

		log.info("Number of questions with answer: " + count + ", number of questions without answer: " + countNULLAnswer);
		log.info("Number of questions with answer but not Resourcetype: " + countNOTRT);
		double sum = questions.size()-countNOTRT;
		log.info("Average Precision: " + (averagep / sum));
		log.info("Average Recall: " + (averager / sum));
		log.info("Average F-measure: " + (averagef / sum));		
		}

}
	
	public static void main(final String[] args) throws Exception{
		QALD6_Pipeline();

	}
}
