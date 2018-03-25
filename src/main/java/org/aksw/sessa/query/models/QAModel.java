package org.aksw.sessa.query.models;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.sessa.helper.graph.Graph;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;

public class QAModel {

  private String question;
  private String preProcessedQuestion;
  private GraphInterface graph;
  private Set<Node> results;
  private NGramHierarchy nGramHierarchy;
  private Map<NGramEntryPosition, Set<Candidate>> candidateMap;
  private int explanationScore;
  private int maxPossibleExplanationScore;

  public QAModel(){
    question = "";
    preProcessedQuestion = "";
    graph = new Graph();
    results = new HashSet<>();
    nGramHierarchy = null;
    candidateMap = null;
    explanationScore = 0;
    maxPossibleExplanationScore = 0;
  }

  public QAModel(QAModel other){
    question = other.getQuestion();
    preProcessedQuestion = other.getPreProcessedQuestion();
    graph = other.getGraph();
    results = other.getResults();
    nGramHierarchy = other.getNGramHierarchy();
    candidateMap = other.getCandidateMap();
    explanationScore = other.getExplanationScore();
    maxPossibleExplanationScore = other.getMaxPossibleExplanationScore();
  }


  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
    maxPossibleExplanationScore = question.split(" ").length;
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
    if(results != null && !results.isEmpty()) {
      explanationScore = results.iterator().next().getExplanation();
    }
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

  public int getExplanationScore() {
    return explanationScore;
  }


  public int getMaxPossibleExplanationScore() {
    return maxPossibleExplanationScore;
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
