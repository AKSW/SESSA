package org.aksw.sessa.main;

import java.util.Set;
import org.junit.Test;
import org.junit.Assert;

public class SESSATest {

  public static final String TSV_FILE = "src/test/resources/en_surface_forms_small.tsv";
  SESSA sessa = new SESSA(TSV_FILE);
  String question;
  Set<String> answer;
	@Test
	public void testAnswer_onEmpty() {
    question = "";
	  answer = sessa.answer(question);
    Assert.assertNull(answer);
  }

  @Test
  public void testAnswer_onRunningExample() {
    question = "birthplace bill gates wife";
    answer = sessa.answer(question);
    Assert.assertTrue(answer.contains("http://dbpedia.org/resource/Dallas"));
  }

  @Test
  public void testAnswer_onObamaExample() {
    question = "birthplace barack obama wife";
    answer = sessa.answer(question);
    Assert.assertTrue(answer.contains("http://dbpedia.org/resource/Chicago"));
  }

  // TODO: create tests for other questions

  // TODO: create tests for accessibility to QueryProcessing & Co.
}
