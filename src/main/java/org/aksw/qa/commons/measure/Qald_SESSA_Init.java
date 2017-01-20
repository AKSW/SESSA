package org.aksw.qa.commons.measure;

import java.io.IOException;

import org.dbpedia.keywordsearch.importer.neo4j;
import org.dbpedia.keywordsearch.indexer.ESNode;
import org.dbpedia.keywordsearch.indexer.Interface.IndexerInterface;
import org.dbpedia.keywordsearch.serverproperties.pathvariables;
import org.neo4j.graphdb.GraphDatabaseService;

public class Qald_SESSA_Init {

	IndexerInterface esnode;
	private pathvariables Instance;
	neo4j graphdb;
	
	Qald_SESSA_Init(){
		
	}

	public void init() throws Exception {

		esnode = new ESNode();
		esnode.startCluster("DBpediaCluster");
		try {
			/* Indexing of classes */
			esnode.rdfcluster("resources/dbpedia_3Eng_class.ttl", "classes");

			/* Indexing of Properties */
			esnode.rdfcluster("resources/dbpedia_3Eng_property.ttl", "properties");

			/* Enriching them with surfaceforms */
//			esnode.rdfcluster("resources/en_surface_forms.ttl", "surfaceforms");

			/* Indexing DBpedia labels */
			esnode.rdfcluster("resources/labels_en.ttl", "dbpedialabels");

			esnode.datatypeindex("resources/datatypes", "datatypes");
			
			esnode.closeBulkLoader();
		} catch (IOException e) {
			// TODO generate log message
		}
		System.out.println("Creating DataBase");

		this.Instance = new pathvariables();
		graphdb = new neo4j(this.Instance.getgraph());
	
		//TODO only load the data once
		GraphDatabaseService gdb = graphdb.getgdbservice();
//		graphdb.graphdbform(gdb, "resources/mappingbased_literals_en.ttl");
		graphdb.graphdbform(gdb, "resources/mappingbased_objects_en.ttl");
		
		
		System.out.println("Creating DataBase finished");

	}
}
