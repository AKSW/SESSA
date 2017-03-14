/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.sessa.main.controller;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.aksw.sessa.main.datastructures.ListFunctions;
import org.aksw.sessa.main.indexer.Interface.IndexerInterface;
import org.aksw.sessa.main.Initializer.initializer;
import org.aksw.sessa.main.Initializer.interfaces.InitializerInterface;
import org.aksw.sessa.main.datastructures.ResultDataStruct;
import org.aksw.sessa.main.importer.neo4j;
import org.aksw.sessa.main.indexer.ESNode;
import org.aksw.sessa.main.ngramgenerator.NGramModel;
import org.aksw.sessa.main.ngramgenerator.interfaces.NGramInterface;
import org.aksw.sessa.main.propagator.propagator;
import org.aksw.sessa.main.propagator.interfaces.PropagatorInterface;
import org.aksw.sessa.main.serverproperties.pathvariables;
import org.aksw.sessa.main.urimapper.Mapper;
import org.aksw.sessa.main.urimapper.interfaces.MapperInterface;

import com.google.gson.Gson;

import org.aksw.hawk.controller.AbstractPipeline;
import org.aksw.hawk.controller.PipelineStanford_1;
import org.aksw.hawk.datastructures.Answer;
import org.aksw.hawk.datastructures.HAWKQuestion;
import org.aksw.hawk.querybuilding.SPARQLQuery;

/**
 *
 * @author enigmatus
 */
public class ServletServer extends HttpServlet {

	IndexerInterface esnode;
	private pathvariables Instance;
	neo4j graphdb;
	//private Fox recognizer;
	


	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		
		NGramInterface ngram = new NGramModel();
		ngram.CreateNGramModel(request.getParameter("Query")
				.replace("whose", "")
				.replace("is", ""));
		
		System.out.println("");
		System.out.println("Question-------------------------");
		System.out.println(request.getParameter("Query"));
		System.out.println("-------------------------------"); 
		String question = request.getParameter("Query");
				
		
		
		//1. SESSA
		MapperInterface mappings = new Mapper();
		mappings.BuildMappings(this.esnode, ngram.getNGramMod());
		InitializerInterface init = new initializer();
        for(int i=0;i<ngram.getNGramMod().size();i++){
        	System.out.println(ngram.getNGramMod().get(i).getBegin());
        	System.out.println(ngram.getNGramMod().get(i).getEnd());
        	System.out.println(ngram.getNGramMod().get(i).getIndex());
        	System.out.println(ngram.getNGramMod().get(i).getLabel());
          
            System.out.println("Index: " + ngram.getNGramMod().get(i).getIndex()+" NGram: "+ ngram.getNGramMod().get(i).getLabel());
        }
		init.initiate(mappings.getMappings(), ngram.getNGramMod());
		PropagatorInterface propagator = new propagator();
		propagator.PropagateInit(graphdb.getgdbservice(), init.getResultsList());
		
		propagator.getFinalResults();
		ListFunctions.sortresults(propagator.getFinalResults());
//		//2. Lgg Query
//		init.setLggQuery();
//		//Lgg Results
////		init.addLggresult();
//		
//		
//		//3. HAWK Prozess
//		HAWKQuestion q = new HAWKQuestion();
////		// q.getLanguageToQuestion().put("en",
////		// "Which anti-apartheid activist was born in Mvezo?");feature
//	
//		
//		q.getLanguageToQuestion().put("en", question);
//		AbstractPipeline pipeline = new PipelineStanford_1();
//	
//		//System.out.println(init.getLggQuery().getQueryPattern());	
//	
//		//Add Prefix and QueryPattern from Lgg
//		if(init.getLggQuery() != null){
//			pipeline.setInitialQuery(init.getLggQuery());		
//			List<Answer> answerlist = pipeline.getAnswersToQuestion(q);
//			init.addLggHawkresult(answerlist);
//		}
		
		//ListFunctions.sortresults(init.getResultsList());
				
		PrintWriter pw = response.getWriter();// get the stream to write the data
		Map map = new HashMap();
		pw.write("[");
		int i;
		System.out.println(" ");
		System.out.println("---------------------{>_<}------------------------");
		System.out.println(" ");
		for (i = propagator.getFinalResults().size() - 1; i >= 0; i--) {
			
			//JSONArray json = JSONArray.put(init.getResultsList().get(i));			
			ResultDataStruct rds = propagator.getFinalResults().get(i);
			//System.out.println(rds.getURI() + " : " + rds.getImage() + " : " + rds.getEnergyScore());
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
		System.out.println(this.Instance.getgraph());
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
		String getgraph = this.Instance.getgraph();
		graphdb = new neo4j(getgraph);
		
//		graphdb.graphdbform(gdb, "resources/mappingbased_literals_en.ttl");
		graphdb.graphdbform( "resources/mappingbased_objects_en.ttl");
		
		System.out.print("Creating DataBase finished");

	}
}
