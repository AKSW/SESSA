package org.dbpedia.keywordsearch.indexer;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.dbpedia.keywordsearch.indexer.Interface.IndexerInterface;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.AbstractRDFHandler;
import org.openrdf.rio.turtle.TurtleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESNode implements IndexerInterface {
	private static Logger log = LoggerFactory.getLogger(ESNode.class);
	private Node node;
	private Client client;
	// this may lead to corruption in multi thread environment
	private String indexname;
	private String baseURI;
	private BulkProcessor bulkProcessor;

	public void startCluster(String clustername) {
		/* Initialization of cluster */
		this.node = nodeBuilder().clusterName(clustername)
				.node();
		/* Starting the central server */
		this.client = this.node.client();

		/* Base URI for Parsing */
		this.baseURI = "http://dbpedia.org";

		/* Prepare Bulk Load */
		this.bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				log.debug("Before Bulk");
			}

			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				log.debug("After Bulk. Bulk took: " + response.took());
			}

			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				log.error("After Bulk", failure);
			}
		})
				.setBulkActions(100000)
				.setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
				.setFlushInterval(TimeValue.timeValueSeconds(60))
				.setConcurrentRequests(0)
				.build();
	}

	public void rdfcluster(String labelspath, String indexname) throws FileNotFoundException, IOException {

		if (!client.admin()
				.indices()
				.exists(new IndicesExistsRequest(indexname))
				.actionGet()
				.exists()) {
			/* Index name */
			this.indexname = indexname;

			/* Parsing turtlefile */
			log.info("Start parsing: " + labelspath);
			RDFParser parser = new TurtleParser();
			OnlineStatementHandler osh = new OnlineStatementHandler();
			parser.setRDFHandler(osh);
			parser.parse(new FileReader(labelspath), baseURI);
			log.info("Finished parsing: " + labelspath);
		}
	}

	private void addToIndex(String subject, String objectString) throws IOException {
		/* Indexing the data in the central server */
		bulkProcessor.add(new IndexRequest(indexname, "mappings").source(jsonBuilder().startObject()
				.field("uri", subject)
				.field("label", objectString)
				.endObject()));
	}

	private class OnlineStatementHandler extends AbstractRDFHandler {
		@SuppressWarnings("unused")
		public void handleStatement(Statement st) {
			String subject = st.getSubject()
					.stringValue();
			String predicate = st.getPredicate()
					.stringValue();
			String object = st.getObject()
					.stringValue();
			try {
				addToIndex(subject, object);
			} catch (IOException e) {
				log.error("Could not parse triple: " + st.toString(), e);
			}
		}

	}

	public void datatypeindex(String filepath, String indexname) throws FileNotFoundException, IOException {
		int i = 0;
		System.out.println("Building the " + indexname + " data...........");
		BufferedReader in = new BufferedReader(new FileReader(filepath));
		String line;
		String value = "";
		String standardunitdatatype;
		String factorunitdatatype;
		while (!((line = in.readLine()) == null)) {
			if (line.contains("$")) {

				line = in.readLine();
				if (line.contains("#")) {
					StringTokenizer standardst = new StringTokenizer(line, "#");
					if (!standardst.hasMoreTokens()) {
						System.err.print("Invalid standarddataunit format: err");
					}
					standardunitdatatype = standardst.nextToken();
					while (!("".equals((line = in.readLine())))) {
						if (line.contains("//") || line.contains("missing conversion factor"))
							continue;
						if (line.equals(""))
							break;
						StringTokenizer factorst = new StringTokenizer(line, "-");
						factorunitdatatype = factorst.nextToken();
						String labels = factorst.nextToken();
						String[] labelarr = labels.split(", ");
						value = "";
						while (factorst.hasMoreTokens())
							value = value + factorst.nextToken();
						for (String label : labelarr) {
							i = i + 1;
							client.prepareIndex(indexname, "mappings", String.valueOf(i))
									.setSource(jsonBuilder().startObject()
											.field("standardunit", standardunitdatatype)
											.field("factorunit", factorunitdatatype)
											.field("label", label)
											.field("value", value)
											.endObject())
									.execute()
									.actionGet();
						}
					}
				} else {
					System.err.print("Invalid standardunit format format in datatype file: Standardunit not present");
				}
			} else {
				System.err.print("Invalid dimension format in datatype file");
			}
		}
		in.close();
		System.out.println("Data Entry complete");
	}

	@Override
	public SearchHit[] transportclient(String query, String path) {
		/* Connecting the remote client with the central cluster */
		Client clientremote = this.node.client();

		/* Building the Query */
		MatchQueryBuilder qb = QueryBuilders.matchQuery("label", query);
		SearchRequestBuilder srb = clientremote.prepareSearch(path)
				.setTypes("mappings");
		SearchResponse retrieved = srb.setQuery(qb)
				.execute()
				.actionGet();

		/* Retrieving the results from the query */
		SearchHit[] results = retrieved.getHits()
				.getHits();
		return results;
	}

	@Override
	public void closeBulkLoader() {
		bulkProcessor.close();
	}

}
