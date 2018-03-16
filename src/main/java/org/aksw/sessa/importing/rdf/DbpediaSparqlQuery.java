package org.aksw.sessa.importing.rdf;

import java.util.Formatter;
import java.util.HashSet;
import java.util.Set;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.aksw.jena_sparql_api.retry.core.QueryExecutionFactoryRetry;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class uses the DBpedia-SPARQL interface to provide a query interface for this project.
 */
public class DbpediaSparqlQuery {

  private static final Logger log = LoggerFactory.getLogger(DbpediaSparqlQuery.class);
  public final String DBPEDIA_URI = "http://dbpedia.org/sparql";
  // one day for now
  private final long TIME_TO_LIVE = 24L * 60L * 60L * 1000L;

  /**
   * Returns a set of results for the given query.
   *
   * @param queryString valid SPARQL query
   * @return set of triple elements
   */
  public Set<String> executeQuery(String queryString) {

    QueryExecutionFactory qef = new QueryExecutionFactoryHttp(DBPEDIA_URI, "http://dbpedia.org");
    qef = new QueryExecutionFactoryRetry(qef, 5, 5000);

    ResultSet rs;
    Set<String> finalSet = new HashSet<>();

    try {
      // Add pagination
      qef = new QueryExecutionFactoryPaginated(qef, 900);
      QueryExecution qe = qef.createQueryExecution(queryString);
      rs = qe.execSelect();
      String varName = rs.getResultVars().get(0);
      while (rs.hasNext()) {
        QuerySolution qs = rs.next();
        finalSet.add(qs.get(varName).toString());
      }

    } catch (Exception e) {
      log.error("Error with query {}", queryString);
      log.error(e.getLocalizedMessage(), e);
    }
    log.trace("Query: '{}'. Found: {}", queryString, finalSet);
    return finalSet;
  }

  /**
   * Queries an ASK-query to DBpedia with given valid SPARQL ask-query
   *
   * @param queryString valid SPARQL ask-query
   * @return true if ASK-query true, false otherwise
   */
  public boolean askQuery(String queryString) {

    QueryExecutionFactory qef = new QueryExecutionFactoryHttp(DBPEDIA_URI, "http://dbpedia.org");
    qef = new QueryExecutionFactoryRetry(qef, 5, 5000);

    boolean answer = false;
    Set<String> finalSet = new HashSet<>();

    try {
      // Add pagination
      qef = new QueryExecutionFactoryPaginated(qef, 900);
      QueryExecution qe = qef.createQueryExecution(queryString);
      answer = qe.execAsk();

    } catch (Exception e) {
      log.error("Error with query {}", queryString);
      log.error(e.getLocalizedMessage(), e);
    }
    log.trace("Query: '{}'. Found: {}", queryString, finalSet);
    return answer;
  }

  /**
   * Queries an ASK-query to DBpedia with given triple.
   *
   * @param subject subject of the triple
   * @param predicate predicate of the triple
   * @param object object of the triple
   * @return true if ASK-query true, false otherwise
   */
  public boolean askQuery(String subject, String predicate, String object) {
    final String QUERY_STRING =
        "ASK{ <%1$s> <%2$s> <%3$s>. }";
    Formatter formatter = new Formatter().format(QUERY_STRING, subject, predicate, object);
    log.debug(formatter.toString());
    return askQuery(formatter.toString());
  }

}
