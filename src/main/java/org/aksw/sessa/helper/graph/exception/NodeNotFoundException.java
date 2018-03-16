package org.aksw.sessa.helper.graph.exception;

import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;

public class NodeNotFoundException extends Exception {

  private Node node;
  private GraphInterface graph;

  public NodeNotFoundException(){
    this(null);
  }

  public NodeNotFoundException(String message){
    this(message, null);
  }

  public NodeNotFoundException(String message, Node node){
    this(message, node, null);
  }

  public NodeNotFoundException(String message, Node node, GraphInterface graph){
    super(message);
    this.node = node;
    this.graph = graph;
  }

  public Node getNode() {
    return node;
  }

  public GraphInterface getGraph() {
    return graph;
  }
}
