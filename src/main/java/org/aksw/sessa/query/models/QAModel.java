package org.aksw.sessa.query.models;

import java.util.Map;
import java.util.Set;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;

public class QAModel {

  private String question;
  private String preProcessedQuestion;
  private GraphInterface graph;
  private Set<Node> results;
  private NGramHierarchy nGramHierarchy;
  private Map<NGramEntryPosition, Set<Candidate>> candidateMap;

  public QAModel(){
    question = null;
    preProcessedQuestion = null;
    graph = null;
    results = null;
    nGramHierarchy = null;
    candidateMap = null;
  }

  public QAModel(QAModel other){
    question = other.getQuestion();
    preProcessedQuestion = other.getPreProcessedQuestion();
    graph = other.getGraph();
    results = other.getResults();
    nGramHierarchy = other.getNGramHierarchy();
    candidateMap = other.getCandidateMap();
  }


  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getPreProcessedQuestion() {
    return preProcessedQuestion;
  }

  public void setPreProcessedQuestion(String preProcessedQuestion) {
    this.preProcessedQuestion = preProcessedQuestion;
  }

  public GraphInterface getGraph() {
    return graph;
  }

  public void setGraph(GraphInterface graph) {
    this.graph = graph;
  }

  public Set<Node> getResults() {
    return results;
  }

  public void setResults(Set<Node> results) {
    this.results = results;
  }

  public NGramHierarchy getNGramHierarchy() {
    return nGramHierarchy;
  }

  public void setNGramHierarchy(NGramHierarchy nGramHierarchy) {
    this.nGramHierarchy = nGramHierarchy;
  }

  public Map<NGramEntryPosition, Set<Candidate>> getCandidateMap() {
    return candidateMap;
  }

  public void setCandidateMap(
      Map<NGramEntryPosition, Set<Candidate>> candidateMap) {
    this.candidateMap = candidateMap;
  }

  @Override
  public String toString() {
    return "QAModel{" +
        "question='" + question + '\'' +
        ", preProcessedQuestion='" + preProcessedQuestion + '\'' +
        ", graph=" + graph +
        ", results=" + results +
        ", nGramHierarchy=" + nGramHierarchy +
        ", candidateMap=" + candidateMap +
        '}';
  }
}
