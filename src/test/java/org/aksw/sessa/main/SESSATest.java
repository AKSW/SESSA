package org.aksw.sessa.main;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
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
import org.aksw.sessa.query.models.QAModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SESSATest {

  public static final String TSV_FILE = "src/test/resources/en_surface_forms_small.tsv";
  public static final String REVERSE_TSV_FILE = "src/test/resources/small_reverse_dictionary.tsv";
  private static final Logger log = LoggerFactory.getLogger(SESSATest.class);
  private SESSA sessa = new SESSA();
  private String question;
  private Set<String> answer;

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

  /**
   * This test on issue #18.
   */
  @Test
  public void testAnswer_onColorUpdateProblem() {
    question = "offical language suriname";
    answer = sessa.answer(question);
    HashSet<String> answerSet = new HashSet<>();
    answerSet.add("http://dbpedia.org/resource/Dutch_language");
    Assert.assertThat(answer, equalTo(answerSet));
  }


  /**
   * This tests on issue #29.
   */
  @Test
  public void testAnswer_onNoConnectionProblem() {
    question = "juan carlos I wife parents";
    answer = sessa.answer(question);
    Assert.assertThat(answer, hasItem("http://dbpedia.org/resource/Paul_of_Greece"));
    Assert.assertThat(answer, hasItem("http://dbpedia.org/resource/Frederika_of_Hanover"));
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
    GraphInterface graph = qaModels[0].getGraph();
    GraphInterface path = graph.findPathsToNodes(qaModels[0].getResults());
    log.debug("\n{}", path.asDOTFormat());
    Assert.assertThat(qaModels[0].getResults(), hasItem(node));
    node = new Node<>("http://dbpedia.org/resource/The_Lion_King_(musical)");
    path = qaModels[1].getGraph().findPathsToNodes(qaModels[1].getResults());
    log.debug("\n{}", path.asDOTFormat());
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


  // Some test for popular questions

  @Test
  @Ignore // cant be answered, as there is the URI http://dbpedia.org/resource/President_of_the_USA which gets associated with the whole question
  public void testAnswer_PresidentOfUSA() {
    question = "current president of usa";
    answer = sessa.answer(question);
    Assert.assertThat(answer, hasItem("http://dbpedia.org/resource/Donald_Trump"));
  }

  @Test
  @Ignore // cant be answered, as capital only points to dbo:Capital (the class, not the property)
  public void testAnswer_OnCapitalOfGermany() {
    question = "capital germany";
    answer = sessa.answer(question);
    Assert.assertThat(answer, hasItem("http://dbpedia.org/resource/Berlin"));
    Assert.assertThat(answer, hasSize(1));
  }

}