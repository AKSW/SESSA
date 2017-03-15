package org.aksw.hawk.controller;

import java.util.List;

import org.aksw.hawk.datastructures.Answer;
import org.aksw.hawk.datastructures.HAWKQuestion;
import org.aksw.hawk.nlp.MutableTreePruner;
import org.aksw.hawk.nouncombination.NounCombinationChain;
import org.aksw.hawk.nouncombination.NounCombiners;
import org.aksw.hawk.number.UnitController;
import org.aksw.hawk.querybuilding.Annotater;
import org.aksw.hawk.querybuilding.SPARQL;
import org.aksw.hawk.querybuilding.SPARQLQuery;
import org.aksw.hawk.querybuilding.SPARQLQueryBuilder;
import org.aksw.hawk.spotter.ASpotter;
import org.aksw.hawk.spotter.Spotlight;
import org.aksw.qa.commons.datastructure.Entity;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineStanford_1 extends AbstractPipeline {
	static Logger log = LoggerFactory.getLogger(PipelineStanford_1.class);
	private ASpotter nerdModule;
	private MutableTreePruner pruner;
	private Annotater annotater;
	private SPARQLQueryBuilder queryBuilder;
	private Cardinality cardinality;
	private QueryTypeClassifier queryTypeClassifier;
	private StanfordNLPConnector stanfordConnector;
	private UnitController numberToDigit;
	private NounCombinationChain nounCombination;

	public PipelineStanford_1() {
		queryTypeClassifier = new QueryTypeClassifier();

		nerdModule = new Spotlight();


		this.stanfordConnector = new StanfordNLPConnector();
		System.out.println("connector------------------init");
		this.numberToDigit = new UnitController();
		System.out.println("UnitController-------------------init");
		numberToDigit.instantiateEnglish(stanfordConnector);
		System.out.println("numberToDigit-------------------init");
		
		nounCombination = new NounCombinationChain(NounCombiners.HawkRules, NounCombiners.StanfordDependecy);
		System.out.println("nounCombination-------------------init");
		
		
		cardinality = new Cardinality();
		System.out.println("cardinality-------------------init");
		pruner = new MutableTreePruner();
		System.out.println("pruner-------------------init");
		SPARQL sparql = new SPARQL();
		System.out.println("sparql-------------------init");
		annotater = new Annotater(sparql);
		System.out.println("annotater-------------------init");
		queryBuilder = new SPARQLQueryBuilder(sparql);
		
	

	}

	
	public void setInitialQuery(Query lggQuery){
		queryBuilder.setInitialQuery(lggQuery);
	}
	
	
	@Override
	public List<Answer> getAnswersToQuestion(final HAWKQuestion q) {
		System.out.println("-------------------------------------");
		log.info("Question: " + q.getLanguageToQuestion().get("en"));

		log.info("Classify question type.");
		q.setIsClassifiedAsASKQuery(queryTypeClassifier.isASKQuery(q.getLanguageToQuestion().get("en")));
		System.out.println(q.getIsClassifiedAsASKQuery());
		
		
		// Disambiguate parts of the query
		log.info("Named entity recognition.");
		q.setLanguageToNamedEntites(nerdModule.getEntities(q.getLanguageToQuestion().get("en")));
	
		
		System.out.println(nerdModule.getEntities(q.getLanguageToQuestion().get("en")));
		// Noun combiner, decrease #nodes in the DEPTree
		log.info("Noun phrase combination / Dependency Parsing");
		// TODO make this method return the combine sequence and work on this,
		// i.e., q.sequence = sentenceToSequence.combineSequences(q);

		// @Ricardo this will calculate cardinality of reduced(combinedNN) tree.
		// is this right?
		q.setTree(stanfordConnector.combineSequences(q, this.numberToDigit));	
		
		
		q.setTree(stanfordConnector.parseTree(q, this.numberToDigit));
		nounCombination.runChain(q);
		
		// Cardinality identifies the integer i used for LIMIT i
		log.info("Cardinality calculation.");
		q.setCardinality(cardinality.cardinality(q));

		// Apply pruning rules
		log.info("Pruning tree.");
		q.setTree(pruner.prune(q));

		// Annotate tree
		log.info("Semantically annotating the tree.");
		annotater.annotateTree(q);

		// Calculating all possible SPARQL BGPs with given semantic annotations
		log.info("Calculating SPARQL representations.");
		List<Answer> answers = queryBuilder.build(q);
		
		return answers;
	}

	public static void main(final String[] args) {
		PipelineStanford_1 p = new PipelineStanford_1();
		HAWKQuestion q = new HAWKQuestion();
		//q.getLanguageToQuestion().put("en", "Which anti-apartheid artist was born in Newyork?");
		q.getLanguageToQuestion().put("en", " Who was vice president under the president?");
		p.getAnswersToQuestion(q);

	}

}
