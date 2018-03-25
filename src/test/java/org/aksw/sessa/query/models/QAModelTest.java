package org.aksw.sessa.query.models;

import static org.hamcrest.Matchers.is;

import java.util.HashSet;
import java.util.Set;
import org.aksw.sessa.helper.graph.Node;
import org.junit.Assert;
import org.junit.Test;

public class QAModelTest {

  @Test
  public void testGetMaxPossibleExplanationScore(){
    QAModel qaModel = new QAModel();
    qaModel.setQuestion("1 2 3");
    Assert.assertThat(qaModel.getMaxPossibleExplanationScore(), is(3));
  }

  @Test
  public void testGetExplanationScore(){
    QAModel qaModel = new QAModel();
    Set<Node> results = new HashSet<>();
    NGramEntryPosition threeExplanationColor = new NGramEntryPosition(3, 1);
    Node<Integer> node = new Node<Integer>(1);
    node.addColor(threeExplanationColor);
    results.add(node);
    qaModel.setResults(results);
    Assert.assertThat(qaModel.getExplanationScore(), is(3));
  }

}
