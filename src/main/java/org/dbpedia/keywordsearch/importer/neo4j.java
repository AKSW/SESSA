package org.dbpedia.keywordsearch.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileDeleteStrategy;
import org.dbpedia.keywordsearch.importer.interfaces.GDBInterface;
import org.dbpedia.keywordsearch.serverproperties.pathvariables;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.helpers.AbstractRDFHandler;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.turtle.TurtleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/* This is the class for starting the graph database server and creating the graph in it */
public class neo4j implements GDBInterface {
	private static Logger log = LoggerFactory.getLogger(neo4j.class);
	private GraphDatabaseService db;
	private boolean databaseExistsDoNotWrite = false;
	private int numberOfTriplesAdded = 0;
	LoadingCache<Label, Node> cache;

	public static void main(String[] args) {
		System.out.println("Creating DataBase");

		pathvariables Instance = new pathvariables();
		String getgraph = Instance.getgraph();
		neo4j graphdb = new neo4j(getgraph);
	
		
		GraphDatabaseService gdb = graphdb.getgdbservice();
		graphdb.graphdbform(gdb, "resources/test_mapping_object_100k.ttl");
		
		System.out.print("Creating DataBase finished");

	}
	/**
	 * Starting a grahdatabase service at apecified path
	 * 
	 * @param graphpath
	 */
	public neo4j(String graphpath) {
		try {
			File file = new File(graphpath);
			if (file.exists()) {
				log.info("Database exists already at " + file.getAbsolutePath());
				databaseExistsDoNotWrite = true;
			}
			this.db = new GraphDatabaseFactory().newEmbeddedDatabase(graphpath);
			registerShutdownHook(this.db);
		} catch (Exception e) {
			log.error("Error while creating neo4j", e);
		}

		this.cache = CacheBuilder.newBuilder().maximumSize(100000)
				.build(new CacheLoader<Label, Node>() {
					public Node load(Label key) {
						Node tmpNode = db.createNode(key);
						tmpNode.setProperty("URI", key.name());
						return tmpNode;
					}
				});
	}

	/* Graphdatabase service closed */
	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	@Override
	public void shutDown() {
		log.info("Shutting down database ...");
		db.shutdown();
	}

	/* Clearing data created on graphdatabase path */
	@Override
	public void clearDb(String graphpath) throws IOException {
		File files = new File(graphpath);
		for (File file : files.listFiles()) {
			FileDeleteStrategy.FORCE.delete(file);
		}
		log.info("Finished clearing database ...");

	}

	/* Returning the started graph database service */
	@Override
	public GraphDatabaseService getgdbservice() {
		return this.db;
	}

	/*
	 * This method creates the types of properties that must be present in the
	 * graph
	 */
	private enum Reltypes implements RelationshipType {
		Predicate_of, Subject_of, Object_of;
	}

	@Override
	public void graphdbform(GraphDatabaseService graphdb, String rdfpath) {
		if (databaseExistsDoNotWrite) {
			log.info("Database exists already thus we are not adding "
					+ (new File(rdfpath)).getAbsolutePath());
			databaseExistsDoNotWrite = true;
		} else {
			log.info("Start parsing: " + rdfpath);

			RDFParser parser = new TurtleParser();
			OnlineStatementHandler osh = new OnlineStatementHandler();
			parser.setRDFHandler(osh);
			parser.getParserConfig().setNonFatalErrors(
					new HashSet<RioSetting<?>>(Arrays.asList(
							BasicParserSettings.VERIFY_DATATYPE_VALUES,
							BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES)));
			try {
				parser.parse(new FileReader(rdfpath), "http://dbpedia.org");
			} catch (RDFParseException | RDFHandlerException | IOException e) {
				log.error("Error while parsing " + rdfpath, e);
			}
			log.info("Finished parsing: " + rdfpath + " with "
					+ numberOfTriplesAdded + "#triples");
		}
	}

	private void addStatementToGraphDB(Statement statement) {
		try (Transaction tx = this.db.beginTx();) {

			/* Creates an iterator on the rdf triples from the specified file */
			/* Beginning the transaction of creating nodes. Allocating resources */
			GraphDatabaseService graphdb = this.db;
			/* Initialization of triple nodes */
			Node subjectnode = null;
			Node predicatenode = null;
			Node objectnode = null;
			ResourceIterator<Node> nodeindex;

			/* Extracting RDF triple nodes from the specified file */
			Resource s = statement.getSubject();
			Resource p = statement.getPredicate();
			Value o = statement.getObject();
			/* The formation of graph and label nodes */
			Label subjectlabel = DynamicLabel.label(s.stringValue());
			Label predicatelabel = DynamicLabel.label(p.stringValue());
			Label objectlabel = DynamicLabel.label(o.stringValue());

			/* Checking whether the node exists before or not */
			subjectnode = this.cache.get(subjectlabel);
			predicatenode = this.cache.get(predicatelabel);
			objectnode = this.cache.get(objectlabel);
			// nodeindex = graphdb.findNodes(subjectlabel);
			// if (!nodeindex.hasNext()) {
			// subjectnode = graphdb.createNode(subjectlabel);
			// subjectnode.setProperty("URI", s.toString());
			// } /* other wise create a new node */
			// else {
			// subjectnode = nodeindex.next();
			// }
			// nodeindex.close();
//			nodeindex = graphdb.findNodes(predicatelabel);
//			if (!nodeindex.hasNext()) {
//				predicatenode = graphdb.createNode(predicatelabel);
//				predicatenode.setProperty("URI", p.toString());
//			} else {
//				predicatenode = nodeindex.next();
//			}
//			nodeindex.close();
//			nodeindex = graphdb.findNodes(objectlabel);
//			if (!nodeindex.hasNext()) {
//				objectnode = graphdb.createNode(objectlabel);
//				objectnode.setProperty("URI", o.toString());
//			} else {
//				objectnode = nodeindex.next();
//			}
//			nodeindex.close();

			/* Creating a fact node for each triple */
			Node factnode = graphdb.createNode();

			/* Establishing relationships of each triple with its fact node */
			Relationship relationships = factnode.createRelationshipTo(
					subjectnode, Reltypes.Subject_of);
			Relationship relationshipp = factnode.createRelationshipTo(
					predicatenode, Reltypes.Predicate_of);
			Relationship relationshipo = factnode.createRelationshipTo(
					objectnode, Reltypes.Object_of);
			tx.success();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class OnlineStatementHandler extends AbstractRDFHandler {
		@Override
		public void handleStatement(Statement st) {
			addStatementToGraphDB(st);
			numberOfTriplesAdded++;
			if (numberOfTriplesAdded % 1000 == 0) {
				log.info("Number of triples added so far "
						+ numberOfTriplesAdded);
			}
		}
	}
}
