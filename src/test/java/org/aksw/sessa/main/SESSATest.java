package org.aksw.sessa.main;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.ReverseTsvFileHandler;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.importing.dictionary.implementation.HashMapDictionary;
import org.aksw.sessa.query.models.QAModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SESSATest {

  public static final String TSV_FILE = "src/test/resources/en_surface_forms_small.tsv";
  public static final String REVERSE_TSV_FILE = "src/test/resources/small_reverse_dictionary.tsv";
  private SESSA sessa = new SESSA();
  private String question;
  private Set<String> answer;
  HashMapDictionary dict;

  @Before
  public void initialize() throws IOException {
    FileHandlerInterface handler = new TsvFileHandler(TSV_FILE);
    FileHandlerInterface handler2 = new ReverseTsvFileHandler(REVERSE_TSV_FILE);
    sessa.loadFileToHashMapDictionary(handler);
    sessa.loadFileToHashMapDictionary(handler2);
  }

  @Test
  public void testAnswer_onEmpty() {
    question = "";
    answer = sessa.answer(question);
    Assert.assertThat(answer, is(nullValue()));
  }

  @Test
  public void testAnswer_onRunningExample() {
    question = "birthplace bill gates wife";
    answer = sessa.answer(question);
    Assert.assertThat(answer, hasItem("http://dbpedia.org/resource/Dallas"));
  }

  @Test
  public void testAnswer_onObamaExample() {
    question = "birthplace barack obama wife";
    answer = sessa.answer(question);
    Assert.assertThat(answer, hasItem("http://dbpedia.org/resource/Chicago"));
  }

  @Test
  @Ignore
  public void testAnswer_SelfReferencing() {
    // spouse of elton john's spouse
    question = "elton john spouse spouse";
    answer = sessa.answer(question);
    Assert.assertThat(answer, hasItem("http://dbpedia.org/resource/Elton_John"));
  }

  /**
   * This tests on issue #11.
   */
  @Test
  public void testAnswer_onInterlinkingProblem() {
    question = "music by elton john current production minskoff theatre";
    answer = sessa.answer(question);
    Assert.assertThat(answer, hasItem("http://dbpedia.org/resource/The_Lion_King_(musical)"));
  }

  @Test
  /**
   * This test on issue #18.
   */
  public void testAnswer_onColorUpdateProblem() {
    question = "offical language suriname";
    answer = sessa.answer(question);
    HashSet<String> answerSet = new HashSet<>();
    answerSet.add("http://dbpedia.org/resource/Dutch_language");
    Assert.assertThat(answer, equalTo(answerSet));
  }

//  @Test
//  public void testAnswer_ManyRdfs_Label() {
//    question = "company, aerospace industry, nuclear reactor technology";
//    answer = sessa.answer(question);
//  }

  @Test
  public void testAnswer_WhichShouldGiveRdfType_BeforePreProcessing(){
    question = "musical music by elton john";
    QAModel[] qaModels = sessa.getQAModels(question);
    Node<String> node = new Node<>("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    Assert.assertThat(qaModels[0].getResults(), hasItem(node));
    node = new Node<>("http://dbpedia.org/resource/The_Lion_King_(musical)");
    Assert.assertThat(qaModels[1].getResults(), hasItem(node));
  }

  @Test
  public void testGetGraphFor_TestColors() {
    question = "music by elton john current production minskoff theatre";
    GraphInterface graph = sessa.getGraphFor(question);
    HashMap<String, Node> nodes = new HashMap<>();
    for (Node node : graph.getNodes()) {
      nodes.put(node.getContent().toString(), node);
    }
    String answer = "http://dbpedia.org/resource/The_Lion_King_(musical)";
    Node answerNode = nodes.get(answer);
    System.out.println(graph);
    Assert.assertThat(answerNode.getExplanation(), equalTo(question.split(" ").length));
  }

  // TODO: create tests for other questions

  // TODO: create tests for accessibility to QueryProcessing & Co.
}