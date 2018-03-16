package org.aksw.sessa.query.processing.post;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.colorspreading.ColorSpreader;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.query.models.Candidate;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.aksw.sessa.query.models.NGramHierarchy;
import org.aksw.sessa.query.models.QAModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostProcessing {

  private static final Logger log = LoggerFactory.getLogger(PostProcessing.class);

  private final String RDF_TYPE = "type";
  private final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

  private final String MESSAGE_FOUND = "Found post processing case";



  public QAModel process(QAModel qAModel){
    log.debug("Starting post processing...");
    QAModel newQAModel = qAModel;
    Set<Node> results = qAModel.getResults();
    if(results.size() == 1) {
      Node node = results.iterator().next();
      if(node.getContent().toString().equals(RDF_TYPE_URI)){
        log.debug("{}: rdf:type is the only answer.", MESSAGE_FOUND);
        newQAModel = handleRdfTypeAnswer(qAModel);
      }
    }
    return newQAModel;
  }


  //TODO: Only consider the candidates that lead to the rdf:type answer
  private QAModel handleRdfTypeAnswer(QAModel qaModel) {
    log.debug("\tAdding rdf:type to the question and ask SESSA again.");
    NGramHierarchy hierarchy = qaModel.getNGramHierarchy();

    // add necessary information for rdf:type
    String question = qaModel.getQuestion() + " type";
    qaModel.setQuestion(question);
    hierarchy.extendHierarchy(RDF_TYPE);
    qaModel.setNGramHierarchy(hierarchy);
    Map<NGramEntryPosition, Set<Candidate>> canMap =  qaModel.getCandidateMap();
    Set<Candidate> candidates = new HashSet<>();
    candidates.add(new Candidate(RDF_TYPE_URI, RDF_TYPE));
    NGramEntryPosition pos = hierarchy.getPosition(RDF_TYPE);
    canMap.put(pos,candidates);

    // Spread colors again
    ColorSpreader colorSpreader = new ColorSpreader(canMap);
    colorSpreader.spreadColors();
    log.debug("{}", colorSpreader.getGraph());
    qaModel.setGraph(colorSpreader.getGraph());
    qaModel.setResults(colorSpreader.getResult());
    return qaModel;
  }

}
