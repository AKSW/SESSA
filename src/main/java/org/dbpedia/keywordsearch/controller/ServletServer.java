/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dbpedia.keywordsearch.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aksw.jena_sparql_api.core.FluentQueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionHttpWrapper;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.WebContent;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
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
import org.dllearner.algorithms.qtl.QueryTreeUtils;
import org.dllearner.algorithms.qtl.datastructures.impl.RDFResourceTree;
import org.dllearner.algorithms.qtl.impl.QueryTreeFactory;
import org.dllearner.algorithms.qtl.impl.QueryTreeFactoryBase;
import org.dllearner.algorithms.qtl.operations.lgg.LGGGenerator;
import org.dllearner.algorithms.qtl.operations.lgg.LGGGeneratorSimple;
import org.dllearner.kb.sparql.ConciseBoundedDescriptionGenerator;
import org.dllearner.kb.sparql.ConciseBoundedDescriptionGeneratorImpl;
import org.dllearner.kb.sparql.SymmetricConciseBoundedDescriptionGeneratorImpl;
import org.neo4j.graphdb.GraphDatabaseService;

import com.google.common.collect.Lists;
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
		
		//------------------------------------------------------------------Lgg 
		Set<String> disambiguatedAnswers = init.totalLabellist();
		LGGGenerator lggGen = new LGGGeneratorSimple();
		// DBpedia as SPARQL endpoint
		QueryExecutionFactory qef = FluentQueryExecutionFactory.http("http://dbpedia.org/sparql", Lists.newArrayList("http://dbpedia.org")).config().withPostProcessor(qe -> ((QueryEngineHTTP) ((QueryExecutionHttpWrapper) qe).getDecoratee()).setModelContentType(WebContent.contentTypeRDFXML)).end().create();
		// CBD generator
		ConciseBoundedDescriptionGenerator cbdGen = new ConciseBoundedDescriptionGeneratorImpl(qef);
		cbdGen = new SymmetricConciseBoundedDescriptionGeneratorImpl(qef);
		QueryTreeFactory qtf = new QueryTreeFactoryBase();
		
		
		
		int minNrOfExamples = 2;
		if (disambiguatedAnswers.size() >= minNrOfExamples) {
			List<RDFResourceTree> trees = new ArrayList<>(disambiguatedAnswers.size());
			for (String uri : disambiguatedAnswers) {
				// generate CBD
				Model cbd = cbdGen.getConciseBoundedDescription(uri);
				System.out.println("|cbd(" + uri + ")|=" + cbd.size() + " triples");
				// generate query tree
				RDFResourceTree tree = qtf.getQueryTree(uri, cbd);
				trees.add(tree);
				// System.out.println(tree.getStringRepresentation(true));
			}

			// compute LGG
			RDFResourceTree lgg = lggGen.getLGG(trees);

			// SPARQL query
			Query query = QueryTreeUtils.toSPARQLQuery(lgg);

			// f-measure/accuracy to answer
			System.out.println(query);
		//------------------------------------------------------------------Lgg 	
		}
		
		
		
		PrintWriter pw = response.getWriter();// get the stream to write the data
		Map map = new HashMap();
		pw.write("[");
		int i;
		System.out.println(" ");
		System.out.println(" 11111111111111111111111111111111");
	   	
    	//System.out.println(json.toString()); 
		for (i = init.getResultsList().size() - 1; i >= 0; i--) {
			
			//JSONArray json = JSONArray.put(init.getResultsList().get(i));
			
			ResultDataStruct rds = init.getResultsList().get(i);
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
		graphdb.graphdbform(gdb, "resources/mappingbased_literals_en.ttl");
		graphdb.graphdbform(gdb, "resources/mappingbased_objects_en.ttl");
		
		
		System.out.print("Creating DataBase finished");

	}
}
