package org.aksw.sessa.importing.dictionary.implementation;

import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.slf4j.LoggerFactory;

/**
 * @author Simon Bordewisch
 */
public class PageRankDictionary extends LuceneDictionary implements DictionaryInterface {

  private org.slf4j.Logger log = LoggerFactory.getLogger(DictionaryInterface.class);
  private int maxResultSize;

  public PageRankDictionary(FileHandlerInterface handler, String indexLocation) {
    super(handler, indexLocation);
    super.setMaxResultSize(1000);
    maxResultSize = LuceneDictionary.NUMBER_OF_DOCS_RECEIVED_FROM_INDEX;
  }

  public PageRankDictionary(FileHandlerInterface handler) {
    this(handler, DEFAULT_PATH_TO_INDEX);
  }

  public PageRankDictionary() {
    this(null);
  }

  /**
   * Given a n-gram, returns a set of URIs related to it or null if this map contains no mapping for
   * the key.
   *
   * @param nGram n-gram whose associated value is to be returned
   * @return mapping of n-grams to set of URIs
   */
  public Set<String> get(final String nGram) {
    Set<String> queryResults = super.get(nGram);
    PriorityQueue<Entry<String, Float>> sortedResults =
        new PriorityQueue<>(1000, Comparator.comparing(Entry::getValue));
    for (String result : queryResults) {
      String rankQuery = constructQuery(result);
      Set<Float> rankQueryResults = executeQuery(rankQuery);
      Iterator<Float> it = rankQueryResults.iterator();
      float rank;
      if (it.hasNext()) {
        rank = rankQueryResults.iterator().next();
      } else {
        rank = 0;
      }
      sortedResults.add(new SimpleEntry<>(result, rank));
    }
    Set<String> finalResultSet = new HashSet<>();
    for (int resultSize = 0;
        resultSize < maxResultSize && !sortedResults.isEmpty();
        resultSize++) {
      finalResultSet.add(sortedResults.poll().getKey());
    }
    return finalResultSet;
  }

  @Override
  public void setMaxResultSize(int maxResultSize) {
    this.maxResultSize = maxResultSize;
  }

  private String constructQuery(String uri) {
    final String QUERY_STRING =
        "SELECT DISTINCT ?rank " +
            "FROM <http://dbpedia.org> " +
            "FROM <http://people.aifb.kit.edu/ath/#DBpedia_PageRank> " +
            "WHERE { <%1$s> <http://purl.org/voc/vrank#hasRank>/<http://purl.org/voc/vrank#rankValue> ?rank. }";
    return new Formatter().format(QUERY_STRING, uri).toString();
  }

  private Set<Float> executeQuery(String queryString) {
    Query query = QueryFactory.create(queryString);

    ResultSet rs;
    Set<Float> finalSet = new HashSet<>();

    try (QueryExecution qexec = QueryExecutionFactory
        .sparqlService("http://dbpedia.org/sparql", query)) {
      // Set the DBpedia specific timeout.
      ((QueryEngineHTTP) qexec).addParam("timeout", "10000");
      // Execute.
      rs = qexec.execSelect();
      String resultVar = rs.getResultVars().get(0);
      while (rs.hasNext()) {
        QuerySolution qs = rs.next();
        finalSet.add(qs.getLiteral(resultVar).getFloat());
      }
    } catch (Exception e) {
      log.error("Error with query {}", queryString);
      log.error("Answer set until this point is: {}", finalSet);
      log.error(e.getLocalizedMessage(), e);
    }
    log.trace("Query: '{}'. Found: {}", queryString, finalSet);
    return finalSet;
  }

}
