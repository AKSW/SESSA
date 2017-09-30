package org.aksw.sessa.importing.rdf;


import java.util.Formatter;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

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
    Query query = QueryFactory.create(queryStr);
    
    
    //System.out.println("Before result set");
    
    
    ResultSet rs = null;
    Set<String> test = new HashSet<>();
    // Remote execution.
    try (QueryExecution qexec = QueryExecutionFactory
        .sparqlService(DBPEDIA_URI, query)) {
      // Set the DBpedia specific timeout.
      ((QueryEngineHTTP) qexec).addParam("timeout", "10000");

      
    //  System.out.println("Before result set1");
      // Execute.
      rs = qexec.execSelect();
      
      //System.out.println("Before result set3");
      while (rs.hasNext()) {
        QuerySolution qs = rs.next();
        test.add(qs.get("?o").toString());
      //  System.out.println("Before result set4");
      }
    } catch (Exception e) {
      e.printStackTrace();
      
     // System.out.println("Before result set2");
    }
    return test;
  }

}
