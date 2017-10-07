package org.aksw.sessa.importing.rdf;


import java.util.Formatter;
import java.util.HashSet;
import java.util.Set;
import org.aksw.jena_sparql_api.cache.h2.CacheCoreH2;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.aksw.jena_sparql_api.retry.core.QueryExecutionFactoryRetry;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.cache.extra.CacheBackend;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontend;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontendImpl;
import org.aksw.jena_sparql_api.cache.core.QueryExecutionFactoryCacheEx;


/**
 * This class uses the DBPedia-SPARQL interface to provide
 * information about the missing triple elements.
 */
//FIXME hard coded DBpedia
public class SparqlGraphFiller {

  private final String DBPEDIA_URI = "http://dbpedia.org/sparql";
  private final String QUERY_STRING =
      "SELECT DISTINCT ?o WHERE {" +
          "{ <%1$s> <%2$s> ?o. } UNION" +
          "{ <%1$s> ?o <%2$s>. } UNION" +
          "{ ?o <%1$s> <%2$s>. } UNION" +
          "{ <%2$s> <%1$s> ?o. } UNION" +
          "{ <%2$s> ?o <%1$s>. } UNION" +
          "{ ?o <%2$s> <%1$s>. }" +
          "} LIMIT 100";

  // one day for now
  private final long TIME_TO_LIVE = 24L * 60L * 60L * 1000L;


  /**
   * Builds query with given URIs to find the missing triple.
   *
   * @param uri1 first URI to be used for the SPARQL-query
   * @param uri2 second URI to be used for the SPARQL-query
   * @return SPARQL query which can be used to find the missing triple
   */
  public String buildQuery(String uri1, String uri2) {
    return new Formatter().format(QUERY_STRING, uri1, uri2).toString();
  }

  /**
   * Given two URIs, it tries to find the missing triple element.
   * Example (URIs shortened):
   * Given dbr:Bill_Gates and dbo:birthPlace this method should at least
   * provide dbr:Seattle, because 'dbr:Bill_Gates dbo:birthPlace dbr:Seattle.'
   * is a valid triple in the DBpedia-database.
   *
   * @param uri1 first URI to be used for the SPARQL-query
   * @param uri2 second URI to be used for the SPARQL-query
   * @return set of triple elements which ca be used to complement the two given URIs
   */
  public Set<String> findMissingTripleElement(String uri1, String uri2) {
    String queryStr = buildQuery(uri1, uri2);

    QueryExecutionFactory qef = new QueryExecutionFactoryHttp(DBPEDIA_URI, "http://dbpedia.org");
    qef = new QueryExecutionFactoryRetry(qef, 5, 5000);
    
    ResultSet rs;
    Set<String> finalSet = new HashSet<>();


    try{
      //CacheBackend cacheBackend = CacheCoreH2.create("./baseDir", TIME_TO_LIVE, true);
      //CacheFrontend cacheFrontend = new CacheFrontendImpl(cacheBackend);
      //qef = new QueryExecutionFactoryCacheEx(qef, cacheFrontend);

      // Add pagination
      qef = new QueryExecutionFactoryPaginated(qef, 900);
      QueryExecution qe = qef.createQueryExecution(queryStr);
      rs = qe.execSelect();
      while (rs.hasNext()) {
        QuerySolution qs = rs.next();
        finalSet.add(qs.get("?o").toString());
      //  System.out.println("Before result set4");
      }

    } catch(Exception e){
      e.printStackTrace();
    }
    return finalSet;
  }

}
