package org.aksw.sessa.importing.dictionary.energy;

import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.slf4j.LoggerFactory;

/**
 * Provides function to calculate the energy score based on wikipedia page rank of given URI. This
 * class is an implementation of the interface {@link EnergyFunctionInterface}.
 */
public class PagerRankFunction implements EnergyFunctionInterface {

  private org.slf4j.Logger log = LoggerFactory.getLogger(EnergyFunctionInterface.class);

  /**
   * Returns the wikipedia page rank of given URI.
   *
   * @param nGram original n-gram with which the uri was found
   * @param foundURI found URI for which the energy score should be calculated
   * @param foundKey key of the dictionary for which the URI is the value
   * @return the energy score of an URI with the given data
   */
  @Override
  public float calculateEnergyScore(String nGram, String foundURI, String foundKey) {
    String rankQuery = constructQuery(foundURI);
    Set<Float> rankQueryResults = executeQuery(rankQuery);
    Iterator<Float> it = rankQueryResults.iterator();
    float rank;
    if (it.hasNext()) {
      rank = rankQueryResults.iterator().next();
    } else {
      rank = 0;
    }
    return rank;
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
