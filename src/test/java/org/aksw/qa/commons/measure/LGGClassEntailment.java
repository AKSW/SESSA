package org.aksw.qa.commons.measure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
	public static void init() throws ComponentInitException, IOException {
		String kb = new String(Files.readAllBytes(Paths.get("resources/test.ttl")));

		//create model with RDFS inference
		model = ModelFactory.createDefaultModel();
		model.read(new ByteArrayInputStream(kb.getBytes()), null, "N3");
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
