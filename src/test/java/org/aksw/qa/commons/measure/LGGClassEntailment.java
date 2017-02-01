package org.aksw.qa.commons.measure;

import java.io.ByteArrayInputStream;

import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.vocabulary.ReasonerVocabulary;
import org.dllearner.algorithms.qtl.QueryTreeUtils;
import org.dllearner.algorithms.qtl.datastructures.impl.RDFResourceTree;
import org.dllearner.algorithms.qtl.impl.QueryTreeFactory;
import org.dllearner.algorithms.qtl.impl.QueryTreeFactoryBase;
import org.dllearner.algorithms.qtl.operations.lgg.LGGGenerator;
import org.dllearner.algorithms.qtl.operations.lgg.LGGGeneratorSimple;
import org.dllearner.core.ComponentInitException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Lorenz Buehmann, Ricardo Usbeck
 *
 */
public class LGGClassEntailment {

	private static QueryTreeFactory treeFactory;
	private static Model model;

	private static LGGGenerator lggGenSimple;

	@BeforeClass
	public static void init() throws ComponentInitException {
		String kb = "@prefix : <http://test.org/> . " +
	"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ." +
				":x1 a :KFZ . " 
				+ ":x2 a :PKW . :x2 :tueren \"4\" ."
				+ ":x3 a :LKW .  " //anhängerkupplung und rdfs:label zu instanzen
		        + ":LKW rdfs:subClassOf :KFZ ."
		        + ":PKW rdfs:subClassOf :KFZ ."
		        + ":PKW rdfs:label \"Personenkraftwagen\" ."
		        + ":LKW rdfs:label \"Lastkraftwagen\" ."
		        + ":KFZ rdfs:label \"Kraftfahrzeug\" .";
		
		
//		<http://test.org/x2> <http://test.org/tueren> "4" .
//		<http://test.org/PKW> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://test.org/KFZ> .
//		<http://test.org/KFZ> <http://www.w3.org/2000/01/rdf-schema#label> "Kraftfahrzeug" .
//			<http://test.org/PKW> <http://www.w3.org/2000/01/rdf-schema#label> "Personenkraftwagen" .
//			<http://test.org/x2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://test.org/PKW> .
//			<http://test.org/x3> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://test.org/LKW> .
//			<http://test.org/LKW> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://test.org/KFZ> .
//			<http://test.org/LKW> <http://www.w3.org/2000/01/rdf-schema#label> "Lastkraftwagen" .
//			<http://test.org/x1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://test.org/KFZ> .

		//create model with RDFS inference
		model = ModelFactory.createDefaultModel();
		model.read(new ByteArrayInputStream(kb.getBytes()), null, "TURTLE");
		// creates triples: :x2 a :KFZ. :x3 a :KFZ
		org.apache.jena.reasoner.Reasoner jenareas = ReasonerRegistry.getRDFSReasoner();
		jenareas.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
		model = ModelFactory.createInfModel(jenareas, model);
		
		// create a treefactory to get minimal descriptive tree
		treeFactory = new QueryTreeFactoryBase();

		lggGenSimple = new LGGGeneratorSimple();
	}

	@Test
	//TODO improve it to test LGG works
	public void testClassEntailment() {
		RDFResourceTree tree1 = treeFactory.getQueryTree("http://test.org/x2", model);
		RDFResourceTree tree2 = treeFactory.getQueryTree("http://test.org/x3", model);

		System.out.println("Tree 1\n" + tree1.getStringRepresentation());
		System.out.println("Tree 2\n" + tree2.getStringRepresentation());

		RDFResourceTree lggSimple = lggGenSimple.getLGG(tree1, tree2);
		System.out.println("LGG_simple(T1,T2)\n" + lggSimple.getStringRepresentation());


		// SPARQL query
		org.apache.jena.query.Query query = QueryTreeUtils.toSPARQLQuery(lggSimple);
		System.out.println(query);

		//run query against
		org.apache.jena.query.Query q = QueryFactory.create(query);
		org.aksw.jena_sparql_api.core.QueryExecutionFactory qef = new  QueryExecutionFactoryModel (model);

		try (QueryExecution qexec = qef.createQueryExecution(q)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				System.out.println(soln);
			}
		}

	}

}
