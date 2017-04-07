package org.aksw.sessa.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.aksw.sessa.main.Initializer.Initializer;
import org.aksw.sessa.main.datastructures.ListFunctions;
import org.aksw.sessa.main.datastructures.ResultDataStruct;
import org.aksw.sessa.main.importer.Neo4j;
import org.aksw.sessa.main.indexer.ESNode;
import org.aksw.sessa.main.ngramgenerator.NGramModel;
import org.aksw.sessa.main.propagator.Propagator;
import org.aksw.sessa.main.serverproperties.Pathvariables;
import org.aksw.sessa.main.urimapper.Mapper;
import org.elasticsearch.common.base.Joiner;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SESSAMiniExample {
	static Logger logger = LoggerFactory.getLogger(SESSAMiniExample.class);

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

		ESNode esnode = new ESNode();
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

		Pathvariables Instance = new Pathvariables();
		Neo4j graphdb = new Neo4j(Instance.getgraph());

		graphdb.graphdbform("resources/test_mappings.ttl");

		System.out.println("Creating DataBase finished");
		
		String keywords = "car doors";

		NGramModel ngram = new NGramModel();

		ngram.createNGramModel(keywords);

		System.out.println("keywords: " + keywords);

		// SESSA results
		Mapper mapper = new Mapper();

		mapper.BuildMappings(esnode, ngram.getNGramMod());

		//TODO here is the pruning step missing!!!
		Initializer init = new Initializer();
		init.initiate(mapper.getMappings(), ngram.getNGramMod());
		System.out.println("Before propagating: "+ Joiner.on("\n\t").join(init.getResultsList()));

		Propagator getFinalResults = new Propagator();
		getFinalResults.PropagateInit(graphdb.getgdbservice(), init.getResultsList());
		System.out.println("After propagating: "+ Joiner.on("\n\t").join(init.getResultsList()));
		ListFunctions.sortresults(init.getResultsList());

		for (ResultDataStruct rds : init.getResultsList()) {
			System.out.println("results " +rds.getURI() +" -> " +rds.getEnergyScore() + ", " + rds.getExplainationScore());
		}
		
		esnode.closeClient();
	}

}
