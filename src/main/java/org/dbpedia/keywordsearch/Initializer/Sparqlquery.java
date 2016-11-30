package org.dbpedia.keywordsearch.Initializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.jena_sparql_api.core.FluentQueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionHttpWrapper;
import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.WebContent;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.dllearner.algorithms.qtl.QueryTreeUtils;
import org.dllearner.algorithms.qtl.datastructures.impl.RDFResourceTree;
import org.dllearner.algorithms.qtl.impl.QueryTreeFactory;
import org.dllearner.algorithms.qtl.impl.QueryTreeFactoryBase;
import org.dllearner.algorithms.qtl.operations.lgg.LGGGenerator;
import org.dllearner.algorithms.qtl.operations.lgg.LGGGeneratorSimple;
import org.dllearner.kb.sparql.ConciseBoundedDescriptionGenerator;
import org.dllearner.kb.sparql.ConciseBoundedDescriptionGeneratorImpl;

import com.google.common.collect.Lists;

public class Sparqlquery {

	private List<RDFNode> lggUrilist = new ArrayList<RDFNode>();
	private Query query = null;
	
	public void query(Set<String> uriset){
		
				Set<String> disambiguatedAnswers = uriset;
				LGGGenerator lggGen = new LGGGeneratorSimple();
				// DBpedia as SPARQL endpoint
				QueryExecutionFactory qef = FluentQueryExecutionFactory.http("http://dbpedia.org/sparql", Lists.newArrayList("http://dbpedia.org")).config().withPostProcessor(qe -> ((QueryEngineHTTP) ((QueryExecutionHttpWrapper) qe).getDecoratee()).setModelContentType(WebContent.contentTypeRDFXML)).end().create();
				// CBD generator

				QueryTreeFactory qtf = new QueryTreeFactoryBase();
				ConciseBoundedDescriptionGenerator cbdGen = new ConciseBoundedDescriptionGeneratorImpl(qef);
				//cbdGen = new SymmetricConciseBoundedDescriptionGeneratorImpl(qef);
							
				
				int minNrOfExamples = 2;
				System.out.println(disambiguatedAnswers.size());
				if (disambiguatedAnswers.size() >= minNrOfExamples) {
					List<RDFResourceTree> trees = new ArrayList<>(disambiguatedAnswers.size());
					for (String uri : disambiguatedAnswers) {
						// generate CBD
						Model cbd = cbdGen.getConciseBoundedDescription(uri);
						System.out.println("|cbd(" + uri + ")|=" + cbd.size() + " triples");
						//System.out.println(cbd);
						// generate query tree
						RDFResourceTree tree = qtf.getQueryTree(uri, cbd);
						trees.add(tree);
						// System.out.println(tree.getStringRepresentation(true));
					}
					System.out.println(trees);
					// compute LGG
					RDFResourceTree lgg = lggGen.getLGG(trees);
					
					// SPARQL query
					query = QueryTreeUtils.toSPARQLQuery(lgg);

					System.out.println(" ");
					System.out.println("---------------------{>_<}------------------------");
					System.out.println(" ");
					System.out.println("This is Sparqlquery: ");
					// f-measure/accuracy to answer
					System.out.println(query);
				}
	}
	
	public List<RDFNode> getLggUrilist(Set<String> uriset){
	  	String sparqlEndpoint = "http://dbpedia.org/sparql";		  
	  	query(uriset);
	  	//QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(this.service, query);
	  	
	    QueryEngineHTTP httpQuery = new QueryEngineHTTP(sparqlEndpoint,query);
	    
	    
	    // execute a Select query
	    ResultSet results = httpQuery.execSelect();
	   
	    while (results.hasNext()) {
	      QuerySolution solution = results.next();
	      // get the value of the variables in the select clause
	      RDFNode ontUri = solution.get("s");
	      
	      // print the output to stdout
	      lggUrilist.add(ontUri);
	      //System.out.println(ontUri);
	    }
	    httpQuery.close();
		return lggUrilist;
	}
}
