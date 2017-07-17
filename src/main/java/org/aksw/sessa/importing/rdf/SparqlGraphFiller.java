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
import org.apache.jena.sparql.engine.http.QueryEngineHTTP ;

/**
 * Created by Simon Bordewisch on 04.07.17.
 */
public class SparqlGraphFiller {

  private final String QUERY_STRING =
      "SELECT DISTINCT ?o WHERE {" +
      "{ <%1$s> <%2$s> ?o. } UNION" +
      "{ <%1$s> ?o <%2$s>. } UNION" +
      "{ ?o <%1$s> <%2$s>. } UNION" +
      "{ <%2$s> <%1$s> ?o. } UNION" +
      "{ <%2$s> ?o <%1$s>. } UNION" +
      "{ ?o <%2$s> <%1$s>. }" +
      //"FILTER ( strstarts(str(?o), \"http://dbpedia.org\") " +
      //"&& ?o != <http://dbpedia.org/ontology/wikiPageWikiLink> )" +
      "} LIMIT 100";


  public String buildQuery(String uri1, String uri2){
    return new Formatter().format( QUERY_STRING, uri1, uri2).toString();
  }



  public Set<String> findMissingTripleElement(String uri1, String uri2)
  {
    String queryStr = buildQuery(uri1, uri2);
    Query query = QueryFactory.create(queryStr);

    ResultSet rs = null;
    Set<String> test = new HashSet<>();
    // Remote execution.
    try ( QueryExecution qexec = QueryExecutionFactory
        .sparqlService("http://dbpedia.org/sparql", query) ) {
      // Set the DBpedia specific timeout.
      ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

      // Execute.
      rs = qexec.execSelect();
      while(rs.hasNext()){
        QuerySolution qs = rs.next();
        test.add(qs.get("?o").toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return test;
  }

  public static void main(String[] args){
    SparqlGraphFiller filler = new SparqlGraphFiller();
    Set<String> qsList = filler.findMissingTripleElement("dbo:birthPlace", "dbr:Bill_Gates");
    for (String qs : qsList){
      System.out.println(qs);
    }
  }


}
