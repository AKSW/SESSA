package org.aksw.sessa.importing.dictionary.implementation;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.LoggerFactory;

//import org.apache.lucene.store.RAMDirectory;

public class LuceneDictionary implements DictionaryImportInterface {

  private static final Version LUCENE_VERSION = Version.LUCENE_46;
  private org.slf4j.Logger log = LoggerFactory.getLogger(DictionaryImportInterface.class);

  public final String FIELD_NAME_KEY = "N-GRAM";
  public final String FIELD_NAME_VALUE = "URI";
  public final int NUMBER_OF_DOCS_RECEIVED_FROM_INDEX = 100;
  public static List<String> STOP_WORDS = ImmutableList
      .of("the", "of", "on", "in", "for", "at", "to");


  private Directory directory;
  private IndexSearcher iSearcher;
  private DirectoryReader iReader;
  private IndexWriter iWriter;

  public LuceneDictionary(FileHandlerInterface handler) {
    try{
      SimpleAnalyzer analyzer = new SimpleAnalyzer(LUCENE_VERSION);
      Path path = FileSystems.getDefault().getPath("resources", "index");
      directory = MMapDirectory.open(path.toFile());
      //directory = new RAMDirectory();
      IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, analyzer);
      iWriter = new IndexWriter(directory, config);
      index(handler);
      Document doc = new Document();
      iReader = DirectoryReader.open(directory);
      iSearcher = new IndexSearcher(iReader);
    } catch (Exception e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }

  public Set<String> get(final String object) {
    if (STOP_WORDS.contains(object.toLowerCase())) {
      return new HashSet<>();
    }
    Set<String> uris = new HashSet<>();
    try {
      PhraseQuery q = new PhraseQuery();
      for(String obj : object.split(" ")) {
        q.add(new Term(FIELD_NAME_KEY, obj));
      }
      TopScoreDocCollector collector = TopScoreDocCollector
          .create(NUMBER_OF_DOCS_RECEIVED_FROM_INDEX, true);

      iSearcher.search(q, collector);
      ScoreDoc[] hits = collector.topDocs().scoreDocs;

      for (ScoreDoc hit : hits) {
        Document hitDoc = iSearcher.doc(hit.doc);
        uris.add(hitDoc.get(FIELD_NAME_VALUE));
      }
    } catch (Exception e) {
      log.error(e.getLocalizedMessage() + " -> " + object, e);
    }
    return uris;
  }

  public void close() {
    try {
      iReader.close();
      directory.close();
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }

  private void index(FileHandlerInterface handler) {
    try {
      log.debug("Starting indexing for  file '{}'", handler.getFileName());
      int count = 0;
      for (Entry<String, String> entry; (entry = handler.nextEntry()) != null; ){
        addDocumentToIndex(entry.getKey(), entry.getValue());
        count++;
      }
      log.debug("Number of entries added: {}", count);
      iWriter.commit();
      iWriter.close();
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }

  private void addDocumentToIndex(String key, String value) throws IOException {
    Document doc = new Document();
    doc.add(new TextField(FIELD_NAME_KEY, key, Store.YES));
    doc.add(new StringField(FIELD_NAME_VALUE, value, Store.YES));
    iWriter.addDocument(doc);
  }
}

