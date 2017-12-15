package org.aksw.sessa.importing.dictionary.filter;

import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.slf4j.LoggerFactory;

public class PageRankFilter extends AbstractFilter {

  private org.slf4j.Logger log = LoggerFactory.getLogger(AbstractFilter.class);


  public PageRankFilter(int numberOfResults){
    super(numberOfResults);
  }

  @Override
  protected float getRank(String keyword, Entry<String, String> entry) {
    String rankQuery = constructQuery(entry.getValue());
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
