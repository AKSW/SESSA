package org.aksw.sessa.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import org.aksw.hawk.datastructures.Answer;
import org.aksw.sessa.main.Initializer.initializer;
import org.aksw.sessa.main.Initializer.interfaces.InitializerInterface;
import org.aksw.sessa.main.datastructures.ListFunctions;
import org.aksw.sessa.main.datastructures.ResultDataStruct;
import org.aksw.sessa.main.importer.neo4j;
import org.aksw.sessa.main.indexer.ESNode;
import org.aksw.sessa.main.indexer.Interface.IndexerInterface;
import org.aksw.sessa.main.ngramgenerator.NGramModel;
import org.aksw.sessa.main.ngramgenerator.interfaces.NGramInterface;
import org.aksw.sessa.main.propagator.propagator;
import org.aksw.sessa.main.propagator.interfaces.PropagatorInterface;
import org.aksw.sessa.main.serverproperties.pathvariables;
import org.aksw.sessa.main.urimapper.Mapper;
import org.aksw.sessa.main.urimapper.interfaces.MapperInterface;
import org.elasticsearch.common.base.Joiner;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SESSAMiniExample {
	Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	// TODO write unit test that checks that partial matches are found, e.g.
	// wagen in Personenkraftwagen
	
	//TODO there are still several times the same node activated
	
	//TODO if run twice the result changes 
	
	//TODO bug if you insert the following triples the literals are identified as the same node
	// <http://test.org/x1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://test.org/Vehicle> .
//	<http://test.org/x1> <http://test.org/doors> "4" .
//		<http://test.org/x2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://test.org/Sportscar> .
//		<http://test.org/x2> <http://test.org/doors> "4" .
	public void getSessaResults() throws FileNotFoundException, IOException {

		IndexerInterface esnode = new ESNode();
		esnode.startCluster("data/testcluster");
		/* Indexing of classes */
		esnode.rdfcluster("resources/test_classes.ttl", "classes");

		/* Indexing of Properties */
		esnode.rdfcluster("resources/test_properties.ttl", "properties");

		/* Indexing DBpedia labels */
		esnode.rdfcluster("resources/test_labels.ttl", "dbpedialabels");

		/* Enriching them with surfaceforms */
		esnode.rdfcluster("resources/test_surfaceforms.ttl", "surfaceforms");

		esnode.closeBulkLoader();

		for (Map<String, Object> i : ((ESNode) esnode).getAllDocs()) {
			for (String j : i.keySet()) {
				System.out.println(j + " " + i.get(j));
			}
		}
		System.out.println("Creating DataBase");

		pathvariables Instance = new pathvariables();
		neo4j graphdb = new neo4j(Instance.getgraph());

		graphdb.graphdbform("resources/test_mappings.ttl");

		System.out.println("Creating DataBase finished");

		Answer answer = new Answer();
		answer.answerStr = new HashSet<String>();
		String keywords = "car doors";

		NGramInterface ngram = new NGramModel();

		ngram.CreateNGramModel(keywords);

		System.out.println("keywords: " + keywords);

		// SESSA results
		MapperInterface mapper = new Mapper();

		mapper.BuildMappings(esnode, ngram.getNGramMod());

		//TODO here is the pruning step missing!!!
		InitializerInterface init = new initializer();
		init.initiate(mapper.getMappings(), ngram.getNGramMod());
		System.out.println("Before propagating: "+ Joiner.on("\n\t").join(init.getResultsList()));

		PropagatorInterface getFinalResults = new propagator();
		getFinalResults.PropagateInit(graphdb.getgdbservice(), init.getResultsList());
		System.out.println("After propagating: "+ Joiner.on("\n\t").join(init.getResultsList()));
		ListFunctions.sortresults(init.getResultsList());

		for (ResultDataStruct rds : init.getResultsList()) {
			System.out.println("results " +rds.getURI() +" -> " +rds.getEnergyScore() + ", " + rds.getExplainationScore());
		}
		
		esnode.closeClient();
	}

}
