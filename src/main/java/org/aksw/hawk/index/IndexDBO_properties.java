package org.aksw.hawk.index;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDFS;
import org.lucene46.analysis.core.SimpleAnalyzer;
import org.lucene46.document.Document;
import org.lucene46.document.Field.Store;
import org.lucene46.document.StringField;
import org.lucene46.document.TextField;
import org.lucene46.index.DirectoryReader;
import org.lucene46.index.IndexWriter;
import org.lucene46.index.IndexWriterConfig;
import org.lucene46.index.Term;
import org.lucene46.search.FuzzyQuery;
import org.lucene46.search.IndexSearcher;
import org.lucene46.search.Query;
import org.lucene46.search.ScoreDoc;
import org.lucene46.search.TopScoreDocCollector;
import org.lucene46.store.Directory;
import org.lucene46.store.MMapDirectory;
import org.lucene46.util.Version;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class IndexDBO_properties {

	private static final Version LUCENE_VERSION = Version.LUCENE_46;
	private org.slf4j.Logger log = LoggerFactory.getLogger(IndexDBO_properties.class);
	public String FIELD_NAME_SUBJECT = "subject";
	public String FIELD_NAME_PREDICATE = "predicate";
	public String FIELD_NAME_OBJECT = "object";
	private int numberOfDocsRetrievedFromIndex = 100;

	private Directory directory;
	private IndexSearcher isearcher;
	private DirectoryReader ireader;
	private IndexWriter iwriter;
	private SimpleAnalyzer analyzer;

	public IndexDBO_properties() {
		try {
			File index = new File("resources/ontologyProperties");
			analyzer = new SimpleAnalyzer(LUCENE_VERSION);
			if (!index.exists()) {
				index.mkdir();
				IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION,analyzer);
				directory = new MMapDirectory(index);
				iwriter = new IndexWriter(directory, config);
				index();
			} else {
				directory = new MMapDirectory(index);
			}
			ireader = DirectoryReader.open(directory);
			isearcher = new IndexSearcher(ireader);
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

	public ArrayList<String> search(final String object) {
		ArrayList<String> uris = Lists.newArrayList();
		try {
			log.debug("\t start asking index...");

			Query q = new FuzzyQuery(new Term(FIELD_NAME_OBJECT, object), 0);
			TopScoreDocCollector collector = TopScoreDocCollector.create(numberOfDocsRetrievedFromIndex,true);

			isearcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			for (ScoreDoc hit : hits) {
				Document hitDoc = isearcher.doc(hit.doc);
				log.debug(object + "->" + hitDoc.get(FIELD_NAME_SUBJECT) + ", " + hitDoc.get(FIELD_NAME_OBJECT));
				uris.add(hitDoc.get(FIELD_NAME_SUBJECT));
			}
			log.debug("\t finished asking index...");
		} catch (Exception e) {
			log.error(e.getLocalizedMessage() + " -> " + object, e);
		}
		return uris;
	}

	public void close() {
		try {
			ireader.close();
			directory.close();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

	private void index() {
		try {
			Model model = RDFDataMgr.loadModel("resources/dbpedia_3Eng_property.ttl");
			StmtIterator stmts = model.listStatements(null, RDFS.label, (RDFNode) null);
			while (stmts.hasNext()) {
				final Statement stmt = stmts.next();
				RDFNode label = stmt.getObject();
				addDocumentToIndex(stmt.getSubject(), "rdfs:label", label.asLiteral());
			}

			iwriter.commit();
			iwriter.close();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

	private void addDocumentToIndex(final Resource resource, final String predicate, final RDFNode next) throws IOException {
		Document doc = new Document();
		doc.add(new StringField(FIELD_NAME_SUBJECT, resource.getURI(), Store.YES));
		doc.add(new StringField(FIELD_NAME_PREDICATE, predicate, Store.YES));
		doc.add(new TextField(FIELD_NAME_OBJECT, next.asLiteral().getString(), Store.YES));
		iwriter.addDocument(doc);
	}

}
