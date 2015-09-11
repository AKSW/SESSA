/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dbpedia.keywordsearch.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.neo4j.graphdb.GraphDatabaseService;

import com.google.gson.Gson;

/**
 *
 * @author enigmatus
 */
public class ServletServer extends HttpServlet {

	IndexerInterface esnode;
	private pathvariables Instance;
	neo4j graphdb;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		NGramInterface ngram = new NGramModel();
		ngram.CreateNGramModel(request.getParameter("Query")
				.replace("whose", "")
				.replace("is", ""));
		MapperInterface mappings = new Mapper();
		mappings.BuildMappings(this.esnode, ngram.getNGramMod());
		InitializerInterface init = new initializer();
		init.initiate(mappings.getMappings(), ngram.getNGramMod());
		PropagatorInterface getFinalResults = new propagator();
		getFinalResults.PropagateInit(graphdb.getgdbservice(), init.getResultsList());
		ListFunctions.sortresults(init.getResultsList());
		PrintWriter pw = response.getWriter();// get the stream to write the data
		Map map = new HashMap();
		pw.write("[");
		int i;
		for (i = init.getResultsList()
				.size() - 1; i >= 0; i--) {
			ResultDataStruct rds = init.getResultsList()
					.get(i);
			System.out.println(rds.getURI() + " : " + rds.getImage() + " : " + rds.getEnergyScore());
			map.put("URI", rds.getURI());
			map.put("ExpScore", rds.getExplainationScore());
			map.put("EngScore", rds.getEnergyScore());
			map.put("image", rds.getImage());
			pw.write(new Gson().toJson(map));
			map.clear();
			if (i > 0)
				pw.write(",");

		}
		pw.write("]");
		System.out.print("Done");

	}

	private String graphpath() {

		return this.Instance.getgraph();
	}

	@Override
	public void init() throws ServletException {

		esnode = new ESNode();
		esnode.startCluster("DBpediaCluster");
		try {
			/* Indexing of classes */
			esnode.rdfcluster("resources/dbpedia_3Eng_class.ttl", "classes");

			/* Indexing of Properties */
			esnode.rdfcluster("resources/dbpedia_3Eng_property.ttl", "properties");

			/* Enriching them with surfaceforms */
			esnode.rdfcluster("resources/en_surface_forms.ttl", "surfaceforms");

			/* Indexing DBpedia labels */
			esnode.rdfcluster("resources/dbpedia_labels.ttl", "dbpedialabels");

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
		graphdb.graphdbform(gdb, "resources/mappingbased_properties_en.ttl");
		
		
		System.out.print("Creating DataBase finished");

	}
}
