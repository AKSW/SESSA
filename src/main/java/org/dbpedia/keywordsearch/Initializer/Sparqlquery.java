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
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.WebContent;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.util.iterator.Filter;
import org.apache.jena.vocabulary.RDFS;
import org.dbpedia.keywordsearch.Constructs.BasicQueryTemplate;
import org.dllearner.algorithms.qtl.QueryTreeUtils;
import org.dllearner.algorithms.qtl.datastructures.impl.RDFResourceTree;
import org.dllearner.algorithms.qtl.impl.QueryTreeFactory;
import org.dllearner.algorithms.qtl.impl.QueryTreeFactoryBase;
import org.dllearner.algorithms.qtl.operations.lgg.LGGGenerator;
import org.dllearner.algorithms.qtl.operations.lgg.LGGGeneratorSimple;
import org.dllearner.algorithms.qtl.util.StopURIsDBpedia;
import org.dllearner.algorithms.qtl.util.StopURIsOWL;
import org.dllearner.algorithms.qtl.util.StopURIsRDFS;
import org.dllearner.algorithms.qtl.util.StopURIsSKOS;
import org.dllearner.algorithms.qtl.util.filters.NamespaceDropStatementFilter;
import org.dllearner.algorithms.qtl.util.filters.ObjectDropStatementFilter;
import org.dllearner.algorithms.qtl.util.filters.PredicateDropStatementFilter;
import org.dllearner.kb.sparql.ConciseBoundedDescriptionGenerator;
import org.dllearner.kb.sparql.ConciseBoundedDescriptionGeneratorImpl;
import org.dllearner.kb.sparql.SymmetricConciseBoundedDescriptionGeneratorImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
				ConciseBoundedDescriptionGenerator cbdGen;
				
				
				cbdGen = new SymmetricConciseBoundedDescriptionGeneratorImpl(qef);
				
				
				
				
				
				
				
				// filters
				
				ArrayList<Filter<Statement>> treeFilters = Lists.newArrayList(new PredicateDropStatementFilter(StopURIsDBpedia.get()), new ObjectDropStatementFilter(StopURIsDBpedia.get()),
				        new PredicateDropStatementFilter(Sets.union(StopURIsRDFS.get(), Sets.newHashSet(RDFS.seeAlso.getURI()))), new PredicateDropStatementFilter(StopURIsOWL.get()),
				        new ObjectDropStatementFilter(StopURIsOWL.get()), new PredicateDropStatementFilter(StopURIsSKOS.get()), new ObjectDropStatementFilter(StopURIsSKOS.get()),
				        new NamespaceDropStatementFilter(Sets.newHashSet("http://dbpedia.org/property/", "http://purl.org/dc/terms/", "http://dbpedia.org/class/yago/", FOAF.getURI())),
				        new PredicateDropStatementFilter(Sets.newHashSet("http://www.w3.org/2002/07/owl#equivalentClass", "http://www.w3.org/2002/07/owl#disjointWith")));
				qtf.addDropFilters(treeFilters.toArray(new Filter[treeFilters.size()]));
				
				
				
				
				
				
				
				int minNrOfExamples = 2;
				System.out.println(" ");
				System.out.println("---------------------{>_<}------------------------");
				System.out.println("disambiguatedAnswers.size(): " + disambiguatedAnswers.size());
				System.out.println(" ");
				RDFResourceTree lggempty = new RDFResourceTree();
				
				if (disambiguatedAnswers.size() >= minNrOfExamples) {
					List<RDFResourceTree> trees = new ArrayList<>(disambiguatedAnswers.size());
					Model cbd;
					for (String uri : disambiguatedAnswers) {
						// generate CBD
						
						cbd = cbdGen.getConciseBoundedDescription(uri);
						
						System.out.println("|cbd(" + uri + ")|=" + cbd.size() + " triples");
						cbdGen = new SymmetricConciseBoundedDescriptionGeneratorImpl(qef);
						// generate query tree
						RDFResourceTree tree = qtf.getQueryTree(uri, cbd);
						trees.add(tree);
						//System.out.println(tree.getStringRepresentation(true));
						//query = QueryTreeUtils.toSPARQLQuery(tree);
					}
					//System.out.println(trees);
					// compute LGG
					RDFResourceTree lgg = lggGen.getLGG(trees,true);
					//System.out.println(lgg.getStringRepresentation(true).equals(lggempty.getStringRepresentation(true)));
					//if (lgg.getStringRepresentation(true).equals(lggempty.getStringRepresentation(true)) != true){
						System.out.println(lgg.getStringRepresentation(true));
						System.out.println(lggempty.getStringRepresentation(true));
						// SPARQL query
						BasicQueryTemplate qe = new BasicQueryTemplate();
						query = qe.toSPARQLQuery(lgg);
						//query = QueryTreeUtils.toSPARQLQuery(lgg);
						//}
					

					System.out.println(" ");
					System.out.println("---------------------{>_<}------------------------");
					System.out.println("This is Sparqlquery: ");
					System.out.println(" ");
					
					// f-measure/accuracy to answer
					System.out.println(query);
				}
			
	}
	
	public List<RDFNode> getLggUrilist(Set<String> uriset){
	  	String sparqlEndpoint = "http://dbpedia.org/sparql";		  
	  	query(uriset);
	  	if (query != null){
	    QueryEngineHTTP httpQuery = new QueryEngineHTTP(sparqlEndpoint,query);
	    	    
	    // execute a Select query
	    ResultSet results = httpQuery.execSelect();
	   
	    while (results.hasNext()) {
	      QuerySolution solution = results.next();
	      // get the value of the variables in the select clause
	      RDFNode ontUri = solution.get("s");
	      
	      lggUrilist.add(ontUri);
	      //System.out.println(ontUri);
	    }
		System.out.println(" ");
		System.out.println("---------------------{>_<}------------------------");
		System.out.println("lggUrilist.size(): " + lggUrilist.size());
		System.out.println(" ");
	    
	    httpQuery.close();
	  	}
		return lggUrilist;
	}
	
}


