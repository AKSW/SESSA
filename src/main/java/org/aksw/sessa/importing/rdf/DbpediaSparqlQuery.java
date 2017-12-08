package org.aksw.sessa.importing.rdf;


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

  public final String DBPEDIA_URI = "http://dbpedia.org/sparql";

  // one day for now
  private final long TIME_TO_LIVE = 24L * 60L * 60L * 1000L;
  private static final Logger log = LoggerFactory.getLogger(DbpediaSparqlQuery.class);


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
      while (rs.hasNext()) {
        QuerySolution qs = rs.next();
        finalSet.add(qs.get("?o").toString());
        //  System.out.println("Before result set4");
      }

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.trace("Query: '{}'. Found: {}", queryString, finalSet);
    return finalSet;
  }

}
