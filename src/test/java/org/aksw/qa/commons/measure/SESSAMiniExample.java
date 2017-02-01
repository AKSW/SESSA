package org.aksw.qa.commons.measure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import org.aksw.hawk.datastructures.Answer;
import org.dbpedia.keywordsearch.Initializer.initializer;
import org.dbpedia.keywordsearch.Initializer.interfaces.InitializerInterface;
import org.dbpedia.keywordsearch.datastructures.ListFunctions;
import org.dbpedia.keywordsearch.datastructures.ResultDataStruct;
import org.dbpedia.keywordsearch.importer.neo4j;
import org.dbpedia.keywordsearch.indexer.ESNode;
import org.dbpedia.keywordsearch.indexer.Interface.IndexerInterface;
import org.dbpedia.keywordsearch.ngramgenerator.NGramModel;
import org.dbpedia.keywordsearch.ngramgenerator.interfaces.NGramInterface;
import org.dbpedia.keywordsearch.propagator.propagator;
import org.dbpedia.keywordsearch.propagator.interfaces.PropagatorInterface;
import org.dbpedia.keywordsearch.serverproperties.pathvariables;
import org.dbpedia.keywordsearch.urimapper.Mapper;
import org.dbpedia.keywordsearch.urimapper.interfaces.MapperInterface;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SESSAMiniExample {
	static Logger log = LoggerFactory.getLogger(QALD7_Train_SESSA.class);

	@Test
	// TODO write unit test that checks that partial matches are found, e.g.
	// wagen in Personenkraftwagen
	public void getSessaResults() throws FileNotFoundException, IOException {

		IndexerInterface esnode = new ESNode();
		esnode.startCluster("data/testcluster");
		/* Indexing of classes */
		esnode.rdfcluster("resources/test.ttl", "classes");

		/* Indexing of Properties */
		esnode.rdfcluster("resources/test.ttl", "properties");

		/* Indexing DBpedia labels */
		esnode.rdfcluster("resources/test.ttl", "dbpedialabels");

		/* Enriching them with surfaceforms */
		esnode.rdfcluster("resources/test.ttl", "surfaceforms");

		esnode.closeBulkLoader();

		for (Map<String, Object> i : ((ESNode) esnode).getAllDocs()) {
			for (String j : i.keySet()) {
				System.out.println(j + " " + i.get(j));
			}
		}
		System.out.println("Creating DataBase");

		pathvariables Instance = new pathvariables();
		neo4j graphdb = new neo4j(Instance.getgraph());

		graphdb.graphdbform("resources/test.ttl");

		System.out.println("Creating DataBase finished");

		Answer answer = new Answer();
		answer.answerStr = new HashSet<String>();
		String keywords = "Personenkraftwagen";

		NGramInterface ngram = new NGramModel();

		ngram.CreateNGramModel(keywords);

		System.out.println("keywords: " + keywords);

		// SESSA results
		MapperInterface mappings = new Mapper();

		mappings.BuildMappings(esnode, ngram.getNGramMod());

		InitializerInterface init = new initializer();
		init.initiate(mappings.getMappings(), ngram.getNGramMod());
		PropagatorInterface getFinalResults = new propagator();
		getFinalResults.PropagateInit(graphdb.getgdbservice(), init.getResultsList());

		ListFunctions.sortresults(init.getResultsList());

		for (ResultDataStruct rds : init.getResultsList()) {
			System.out.println("results " +rds.getURI() +" -> " +rds.getEnergyScore() + ", " + rds.getExplainationScore());
		}
		
		esnode.closeClient();
	}

}
