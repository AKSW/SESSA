package org.aksw.sessa.importing.dictionary.implementation;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.candidate.Candidate;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.config.ConfigurationInitializer;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.importing.dictionary.FileBasedDictionary;
import org.aksw.sessa.importing.dictionary.util.DictionaryEntrySimilarity;
import org.apache.commons.configuration2.Configuration;
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
import org.apache.lucene.search.BooleanClause.Occur;
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
   * Defines how many documents will be retrieved from the index for each query.
   */
  public static final int NUMBER_OF_DOCS_RECEIVED_FROM_INDEX = 100;
  /**
   * Contains the stop words, for which the search will be omitted.
   */
  public static final List<String> STOP_WORDS = ImmutableList
      .of("the", "of", "on", "in", "for", "at", "to");
  private static final String LUCENE_LOCATION_KEY = "dictionary.lucene.location";
  /**
   * Contains the field name for the keys in Lucene.
   */
  private static final String FIELD_NAME_KEY = "N-GRAM";
  /**
   * Contains the field name for the values in Lucene.
   */
  private static final String FIELD_NAME_VALUE = "URI";
  private static final Version LUCENE_VERSION = Version.LUCENE_46;
  /**
   * Contais the buffer size, i.e. the number of entries in the bufferSize-hashmap before the
   * changes are committed to the Lucene dictionary. Smaller numbers will lead to performance loss
   * due to the committing cost. Larger numbers will lead to more memory consumption.
   */
  private int bufferSize = 1000000;
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
    this(null, null);
  }

  /**
   * Calls {@link #LuceneDictionary(FileHandlerInterface, String) LuceneDictionary(null,
   * indexLocation)}. This means that the given index will remain as is and additional dictionary
   * entries have to be added via {@link #putAll(FileHandlerInterface)}. (
   */
  public LuceneDictionary(String indexLocation) {
    this(null, indexLocation);
  }

  /**
   * Calls {@link #LuceneDictionary(FileHandlerInterface, String) LuceneDictionary(FileHandlerInterface,
   * DEFAULT_PATH_TO_INDEX)}. This means that the index will be written in the in default location
   * (
   *
   * @param handler file handler, which contains file and is capable of parsing said file
   */
  public LuceneDictionary(FileHandlerInterface handler) {
    this(handler, null);
  }

  /**
   * Constructs a LuceneDictionary with given handler and location of index.
   *
   * @param handler file handler that contains file name
   * @param indexLocation indicates where the index should be saved
   */
  public LuceneDictionary(FileHandlerInterface handler, String indexLocation) {
    if (indexLocation == null) {
      Configuration configuration = ConfigurationInitializer.getConfiguration();
      indexLocation = configuration.getString(LUCENE_LOCATION_KEY);
    }

    try {
      maxResultSize = NUMBER_OF_DOCS_RECEIVED_FROM_INDEX;
      SimpleAnalyzer analyzer = new SimpleAnalyzer(LUCENE_VERSION);
      Path path = FileSystems.getDefault().getPath(indexLocation);
      directory = MMapDirectory.open(path.toFile());
      //directory = new RAMDirectory(); // alternative to file based Lucene
      IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, analyzer);
      similarity = new DictionaryEntrySimilarity();
      config.setSimilarity(similarity);
      iWriter = new IndexWriter(directory, config);
      commitAndUpdate(); // ones to set the iReader
      if (handler != null) {
        putAll(handler);
      }
      log.debug("Loaded LuceneDictionary. Total number of entries in dictionary: {}",
          iReader.numDocs());
    } catch (Exception e) {
      log.error(e.getLocalizedMessage());
    }
  }

  /**
   * Contains the buffer size, i.e. the number of entries in the buffer-hashmap before the changes
   * are committed to the Lucene dictionary. Smaller numbers will lead to performance loss due to
   * the committing cost. Larger numbers will lead to more memory consumption.
   */
  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  /**
   * Given a n-gram, returns a set of URIs related to it or null if this map contains no mapping for
   * the key.
   *
   * @param nGram n-gram whose associated value is to be returned
   * @return mapping of n-grams to set of URIs
   */
  @Override
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
    foundCandidateSet = this.filter(nGram, foundCandidateSet);
    foundCandidateSet = this.calculateEnergy(foundCandidateSet, nGram);
    return foundCandidateSet;
  }

  /**
   * Returns the size of the dictionary, i.e. how many pairs of keys and values.
   */
  @Override
  public int size() {
    return iReader.numDocs();
  }

  /**
   * Closes all files handled, i.e. the index-files.
   */
  @Override
  public void close() {
    try {
      iReader.close();
      iWriter.close();
      directory.close();
    } catch (IOException e) {
      log.error(e.getLocalizedMessage());
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
      log.error(ioE.getLocalizedMessage());
    }
  }

  /**
   * Adds the entries in the give handler to the dictionary.
   *
   * @param handler handler with file information
   */
  @Override
  public void putAll(FileHandlerInterface handler) {
    try {
      log.debug("Starting indexing for file '{}'", handler.getFileName());
      int count = 0;
      Map<String, Set<String>> candidateEntries = new HashMap<>();
      for (Entry<String, String> entry; (entry = handler.nextEntry()) != null; ) {
        String key = entry.getKey().toLowerCase();
        String value = entry.getValue();
        log.trace("Checking entry {}", entry);
        if (!inDictionary(entry)) {
          log.trace("\tEntry not in Lucene");
          if (candidateEntries.containsKey(key)) {
            log.trace("\tEntry in buffer");
            if (!candidateEntries.get(key).contains(value.toLowerCase())) {
              log.trace("\t\tEntry value not in buffer");
              Set<String> tmp = candidateEntries.get(key);
              tmp.add(value.toLowerCase());
              candidateEntries.put(key, tmp);
              addDocumentToIndex(key, value);
              count++;
            }
          } else {
            log.trace("\tEntry not in buffer");
            Set<String> tmp = new HashSet<>();
            tmp.add(value.toLowerCase());
            candidateEntries.put(key, tmp);
            addDocumentToIndex(key, value);
            count++;
          }
        }
        if (count % bufferSize == 0) {
          candidateEntries.clear();
          commitAndUpdate();
        }
      }
      commitAndUpdate();
      log.debug("Number of entries added: {}", count);
      log.debug("Total number of entries in index: {}", iReader.numDocs());
    } catch (IOException e) {
      log.error(e.getLocalizedMessage());
    }
  }

  private void addDocumentToIndex(String key, String value) throws IOException {
    Document doc = new Document();
    doc.add(new TextField(FIELD_NAME_KEY, key, Store.YES));
    doc.add(new StringField(FIELD_NAME_VALUE, value, Store.YES));
    iWriter.addDocument(doc);
  }

  private boolean inDictionary(Entry<String, String> entry) {
    BooleanQuery queryTerms = new BooleanQuery();
    Query queryKey = new TermQuery(new Term(FIELD_NAME_KEY, entry.getKey().toLowerCase()));
    queryTerms.add(queryKey, Occur.MUST);
    TopScoreDocCollector collector = TopScoreDocCollector
        .create(5, true);
    Map<String, String> foundEntries = new HashMap<>();
    try {
      iSearcher.search(queryTerms, collector);
      ScoreDoc[] hits = collector.topDocs().scoreDocs;
      for (ScoreDoc hit : hits) {
        Document hitDoc = iSearcher.doc(hit.doc);
        String key = hitDoc.get(FIELD_NAME_KEY);
        String uri = hitDoc.get(FIELD_NAME_VALUE);
        foundEntries.put(key.toLowerCase(), uri.toLowerCase());
      }
    } catch (IOException ioE) {
      log.error(ioE.getLocalizedMessage());
    }
    String key = entry.getKey().toLowerCase();
    if (foundEntries.containsKey(key)) {
      String value = foundEntries.get(key);
      if (value != null && value.equals(entry.getValue())) {
        return true;
      }
    }
    return false;
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

