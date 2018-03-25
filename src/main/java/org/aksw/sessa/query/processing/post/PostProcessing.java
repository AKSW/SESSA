package org.aksw.sessa.query.processing.post;

import java.util.HashSet;
import java.util.Set;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.importing.rdf.DbpediaSparqlQuery;
import org.aksw.sessa.query.models.QAModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostProcessing {

  private static final Logger log = LoggerFactory.getLogger(PostProcessing.class);

  private final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

  private final String MESSAGE_FOUND = "Found post processing case";
  private final int MAX_RESULT_SIZE = 20;


  public QAModel process(QAModel qAModel) {
    log.debug("Starting post processing...");
    QAModel newQAModel = new QAModel(qAModel);
    Set<Node> results = qAModel.getResults();

    //handling low information answers (too many results)
    if(results.size() > MAX_RESULT_SIZE) {
      log.debug("Too many answers(>{}). Returning empty set.", MAX_RESULT_SIZE);
      newQAModel.setResults(new HashSet<>());
    }
    //handling low information answers (explanation score too low)
    if(qAModel.getExplanationScore() < qAModel.getMaxPossibleExplanationScore()) {
      log.debug("Explanation score too low (is:{}, maxPossible:{}. Returning empty set.",
          qAModel.getExplanationScore(),
          qAModel.getMaxPossibleExplanationScore());
      newQAModel.setResults(new HashSet<>());
    }

    // handling specific answer
    if (results.size() == 1) {
      Node node = results.iterator().next();

      // handling rdf:type-answers
      if (node.getContent().toString().equals(RDF_TYPE_URI)) {
        log.debug("{}: rdf:type is the only answer.", MESSAGE_FOUND);
        newQAModel = handleRdfTypeAnswer(qAModel);
      }
    }
    return newQAModel;
  }


  private QAModel handleRdfTypeAnswer(QAModel qaModel) {
    QAModel postProcessModel = new QAModel(qaModel);
    GraphInterface originalGraph = qaModel.getGraph();
    Node rdfType = qaModel.getResults().iterator().next();
    log.debug("Extracting minimal graph that leads to rdf:type");
    GraphInterface path = originalGraph.findPathsToNode(rdfType);
    postProcessModel.setGraph(path);
    Set<Node> results = new HashSet<>();
    log.debug("Searching for nodes that are instance of another");
    for (Node factNode : path.getNeighborsLeadingTo(rdfType)) {
      for (Node neighbor1 : path.getNeighborsLeadingTo(factNode)) {
        for (Node neighbor2 : path.getNeighborsLeadingTo(factNode)) {
          if (neighbor1 != neighbor2 && isRdfTypeOf(neighbor1, neighbor2)) {
            log.debug("Found node that is instance of another: {}", neighbor2);
            results.add(neighbor2);
          }
        }
      }
    }
    postProcessModel.setResults(results);
    return postProcessModel;
  }

  private boolean isRdfTypeOf(Node classNode, Node instanceNode) {
    DbpediaSparqlQuery dbpQuery = new DbpediaSparqlQuery();
    return dbpQuery.askQuery(
        instanceNode.getContent().toString(),
        RDF_TYPE_URI,
        classNode.getContent().toString());
  }

}
