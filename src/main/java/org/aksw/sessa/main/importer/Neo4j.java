package org.aksw.sessa.main.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileDeleteStrategy;
import org.aksw.sessa.main.serverproperties.pathvariables;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
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
public class neo4j {
	private static Logger log = LoggerFactory.getLogger(Neo4j.class);

	private GraphDatabaseService db = null;
	private String graphpath = null;

	// variables just for writing to DB
	private boolean databaseExistsDoNotWrite = false;
	private int numberOfTriplesAdded = 0;
	private LoadingCache<Label, Long> cache = null;
	private BatchInserter batchInserter = null;

	//TODO transform to unit test with destruction of DB in the end
	public static void main(String[] args) {
		log.info("Creating DataBase");

		pathvariables Instance = new pathvariables();
		String getgraph = Instance.getgraph();
		neo4j graphdb = new neo4j(getgraph);

		graphdb.graphdbform("resources/test_mapping_object_100k.ttl");

		log.info("Creating DataBase finished");

	}

	/**
	 * Starting a grahdatabase service at apecified path
	 * 
	 * @param graphpath
	 */
	public neo4j(String graphpath) {
		try {
			this.graphpath = graphpath;
			File file = new File(graphpath);
			if (file.exists()) {
				log.info("Database exists already at " + file.getAbsolutePath());
				databaseExistsDoNotWrite = true;
				this.db = new GraphDatabaseFactory().newEmbeddedDatabase(graphpath);
				registerShutdownHook(this.db);
			} else {
				log.info("Opening a new database at " + file.getAbsolutePath());

				// this cache builds a new node for the batchinserter
				this.batchInserter = BatchInserters.inserter(graphpath);

				this.cache = CacheBuilder.newBuilder().maximumSize(100000).build(new CacheLoader<Label, Long>() {
					public Long load(Label key) {
						Map<String, Object> properties = new HashMap<String, Object>();
						properties.put("URI", key.name());
						Long id = batchInserter.createNode(properties, key);
						return id;
					}
				});
			}
		} catch (Exception e) {
			log.error("Error while creating neo4j", e);
		}
	}

	public void graphdbform(String rdfpath) {

		if (databaseExistsDoNotWrite) {
			log.info("Database exists already thus we are not adding " + (new File(rdfpath)).getAbsolutePath());
			databaseExistsDoNotWrite = true;
		} else {
			log.info("Start parsing: " + rdfpath);

			RDFParser parser = new TurtleParser();
			OnlineStatementHandler osh = new OnlineStatementHandler();
			parser.setRDFHandler(osh);
			parser.getParserConfig().setNonFatalErrors(new HashSet<RioSetting<?>>(Arrays.asList(BasicParserSettings.VERIFY_DATATYPE_VALUES, BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES)));
			try {
				parser.parse(new FileReader(rdfpath), "http://dbpedia.org");
			} catch (RDFParseException | RDFHandlerException | IOException e) {
				log.error("Error while parsing " + rdfpath, e);
			}

			batchInserter.shutdown();
			log.info("Finished parsing: " + rdfpath + " with " + numberOfTriplesAdded + "#triples");
		}
	}

	private void addStatementToGraphDBAsBatch(Statement statement) throws ExecutionException {
		/* Initialization of triple nodes */

		/* Extracting RDF triple nodes from the specified file */
		Resource s = statement.getSubject();
		Resource p = statement.getPredicate();
		Value o = statement.getObject();
		/* The formation of graph and label nodes */
		Label subjectlabel = DynamicLabel.label(s.stringValue());
		Label predicatelabel = DynamicLabel.label(p.stringValue());
		Label objectlabel = DynamicLabel.label(o.stringValue());

		/* Checking whether the node exists before or not */
		Long subjectnode = this.cache.get(subjectlabel);
		Long predicatenode = this.cache.get(predicatelabel);
		Long objectnode = this.cache.get(objectlabel);

		/*
		 * Creating a new fact node connecting all parts of a triple without any
		 * label or property
		 */
		Long factnode = batchInserter.createNode(new HashMap<String, Object>(), DynamicLabel.label(new String()));

		/* Establishing relationships of each triple with its fact node */
		batchInserter.createRelationship(factnode, subjectnode, Reltypes.Subject_of, null);
		batchInserter.createRelationship(factnode, predicatenode, Reltypes.Predicate_of, null);
		batchInserter.createRelationship(factnode, objectnode, Reltypes.Object_of, null);

	}

	private class OnlineStatementHandler extends AbstractRDFHandler {
		@Override
		public void handleStatement(Statement st) {
			try {
				addStatementToGraphDBAsBatch(st);
				numberOfTriplesAdded++;
				if (numberOfTriplesAdded % 1000 == 0) {
					log.info("Number of triples added so far " + numberOfTriplesAdded);
				}
			} catch (ExecutionException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
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


	public void shutDown() {
		log.info("Shutting down database ...");
		db.shutdown();
	}

	/* Clearing data created on graphdatabase path */
	public void clearDb(String graphpath) throws IOException {
		File files = new File(graphpath);
		for (File file : files.listFiles()) {
			FileDeleteStrategy.FORCE.delete(file);
		}
		log.info("Finished clearing database ...");

	}

	/* Returning the started graph database service */
	public GraphDatabaseService getgdbservice() {
		if (this.db == null) {
			log.info("Database exists already at " + this.graphpath);
			databaseExistsDoNotWrite = true;
			this.db = new GraphDatabaseFactory().newEmbeddedDatabase(this.graphpath);
			registerShutdownHook(this.db);
		}
		return this.db;
	}

	/*
	 * This method creates the types of properties that must be present in the
	 * graph
	 */
	private enum Reltypes implements RelationshipType {
		Predicate_of, Subject_of, Object_of;
	}

}
