package org.aksw.sessa.importing.rdf;


import java.util.Formatter;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class uses the DBPedia-SPARQL interface to provide information about the missing triple
 * elements.
 */
//FIXME hard coded DBpedia
public class SparqlGraphFiller {

  private static final Logger log = LoggerFactory.getLogger(SparqlGraphFiller.class);
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
  private String buildQuery(String uri1, String uri2) {
    return new Formatter().format(QUERY_STRING, uri1, uri2).toString();
  }

  /**
   * Given two URIs, it tries to find the missing triple element. Example (URIs shortened): Given
   * dbr:Bill_Gates and dbo:birthPlace this method should at least provide dbr:Seattle, because
   * 'dbr:Bill_Gates dbo:birthPlace dbr:Seattle.' is a valid triple in the DBpedia-database.
   *
   * @param uri1 first URI to be used for the SPARQL-query
   * @param uri2 second URI to be used for the SPARQL-query
   * @return set of triple elements which ca be used to complement the two given URIs
   */
  public Set<String> findMissingTripleElement(String uri1, String uri2) {
    String queryString = buildQuery(uri1, uri2);
    DbpediaSparqlQuery dbpQ = new DbpediaSparqlQuery();
    return dbpQ.executeQuery(queryString);
  }
}
