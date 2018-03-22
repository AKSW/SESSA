package org.aksw.sessa.importing.dictionary.implementation;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.importing.dictionary.FileBasedDictionary;
import org.aksw.sessa.importing.dictionary.util.DictionaryEntrySimilarity;
import org.aksw.sessa.query.models.Candidate;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;

//import org.apache.lucene.store.RAMDirectory;

/**
 * Provides a Lucene-based dictionary given a file handler. The path to the index is stored in the
 * variable DEFAULT_PATH_TO_INDEX. This class is an implementation of the interface {@link
 * DictionaryInterface}.
 *
 * @author Simon Bordewisch
 */
public class LuceneDictionary extends FileBasedDictionary implements AutoCloseable {

  /**
   * Contains the path to the index.
   */
  public static final String DEFAULT_PATH_TO_INDEX = "resources/index";
  /**
   * Contains the field name for the keys in Lucene.
   */
  public static final String FIELD_NAME_KEY = "N-GRAM";
  /**
   * Contains the field name for the values in Lucene.
   */
  public static final String FIELD_NAME_VALUE = "URI";
  /**
   * Defines how many documents will be retrieved from the index for each query.
   */
  public static final int NUMBER_OF_DOCS_RECEIVED_FROM_INDEX = 100;
  /**
   * Contains the stop words, for which the search will be omitted.
   */
  public static final List<String> STOP_WORDS = ImmutableList
      .of("the", "of", "on", "in", "for", "at", "to");
  private static final Version LUCENE_VERSION = Version.LUCENE_46;
  private Directory directory;
  private Similarity similarity;
  private IndexSearcher iSearcher;
  private DirectoryReader iReader;
  private IndexWriter iWriter;
  private int maxResultSize;

  /**
   * Calls {@link #LuceneDictionary(FileHandlerInterface, String) LuceneDictionary(null,
   * DEFAULT_PATH_TO_INDEX)}. Therefore, no entries will be added to the Lucene index and the
   * location is the default location.
   */
  public LuceneDictionary() {
    this(null, DEFAULT_PATH_TO_INDEX);
  }

  /**
   * Calls {@link #LuceneDictionary(FileHandlerInterface, String) LuceneDictionary(FileHandlerInterface,
   * DEFAULT_PATH_TO_INDEX)}. This means that the index will be written in the in default location
   * (
   *
   * @param handler file handler, which contains file and is capable of parsing said file
   */
  public LuceneDictionary(FileHandlerInterface handler) {
    this(handler, DEFAULT_PATH_TO_INDEX);
  }


  /**
   * Constructs a LuceneDictionary with given handler and location of index.
   *
   * @param handler file handler that contains file name
   * @param indexLocation indicates where the index should be saved
   */
  public LuceneDictionary(FileHandlerInterface handler, String indexLocation) {
    try {
      maxResultSize = NUMBER_OF_DOCS_RECEIVED_FROM_INDEX;
      SimpleAnalyzer analyzer = new SimpleAnalyzer(LUCENE_VERSION);
      Path path = FileSystems.getDefault().getPath(indexLocation);
      boolean filesExists = Files.exists(path);
      directory = MMapDirectory.open(path.toFile());
      //directory = new RAMDirectory(); // alternative to file based Lucene
      IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, analyzer);
      similarity = new DictionaryEntrySimilarity();
      config.setSimilarity(similarity);
      iWriter = new IndexWriter(directory, config);
      if (!filesExists && handler != null) {
        putAll(handler);
      }
      commitAndUpdate();
      log.debug("Loaded LuceneDictionary. Total number of entries in dictionary: {}",
          iReader.numDocs());
    } catch (Exception e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }

  /**
   * Given a n-gram, returns a set of URIs related to it or null if this map contains no mapping for
   * the key.
   *
   * @param nGram n-gram whose associated value is to be returned
   * @return mapping of n-grams to set of URIs
   */
  public Set<Candidate> get(final String nGram) {
    if (STOP_WORDS.contains(nGram.toLowerCase())) {
      return new HashSet<>();
    }
    Set<Candidate> foundCandidateSet = new HashSet<>();
    try {
      String[] uniGrams = nGram.split(" ");
      BooleanQuery queryTerms = new BooleanQuery();
      for (String uniGram : uniGrams) {
        Query query;
        if (uniGram.length() < 4) {
          query = new TermQuery(new Term(FIELD_NAME_KEY, uniGram));
        } else {
          query = new FuzzyQuery(new Term(FIELD_NAME_KEY, uniGram), 1);
        }
        queryTerms.add(query, BooleanClause.Occur.MUST);
      }
      log.trace("{}", queryTerms.toString());

      TopScoreDocCollector collector = TopScoreDocCollector
          .create(maxResultSize, true);
      iSearcher.search(queryTerms, collector);
      ScoreDoc[] hits = collector.topDocs().scoreDocs;
      log.trace("{}", hits);
      for (ScoreDoc hit : hits) {
        Document hitDoc = iSearcher.doc(hit.doc);
        String key = hitDoc.get(FIELD_NAME_KEY);
        String uri = hitDoc.get(FIELD_NAME_VALUE);
        Candidate candidate = new Candidate(uri, key);
        foundCandidateSet.add(candidate);
      }
    } catch (Exception e) {
      log.error(e.getLocalizedMessage() + " -> " + nGram, e);
    }
    return this.filter(nGram, foundCandidateSet);
  }


  /**
   * Closes all files handled, i.e. the index-files.
   */
  public void close() {
    try {
      iReader.close();
      iWriter.close();
      directory.close();
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }

  /**
   * Deletes all entries in the index.
   */
  public void clearIndex() {
    try {
      iWriter.deleteAll();
      commitAndUpdate();
    } catch (IOException ioE) {
      log.error(ioE.getLocalizedMessage(), ioE);
    }
  }

  /**
   * Adds the entries in the give handler to the dictionary.
   *
   * @param handler handler with file information
   */
  public void putAll(FileHandlerInterface handler) {
    try {
      log.debug("Starting indexing for  file '{}'", handler.getFileName());
      int count = 0;
      for (Entry<String, String> entry; (entry = handler.nextEntry()) != null; ) {
        addDocumentToIndex(entry.getKey(), entry.getValue());
        count++;
      }
      commitAndUpdate();
      log.debug("Number of entries added: {}", count);
      log.debug("Total number of entries in index: {}", iReader.numDocs());
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

  /**
   * Commits all pending write operations and updates iReader and iSearcher
   */
  private void commitAndUpdate() throws IOException {
    iWriter.commit();
    iReader = DirectoryReader.open(directory);
    iSearcher = new IndexSearcher(iReader);
    iSearcher.setSimilarity(similarity);
  }
}

