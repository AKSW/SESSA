package org.aksw.sessa.main;

import org.junit.Test;
import org.junit.Assert;

public class SESSATest {

  SESSA sessa = new SESSA();
  String question;
  String a;
	@Test
	public void testEmpty() {
    question = "";
	  a = sessa.answer(question);
    Assert.assertEquals("", a);
  }

  @Test
  public void test() {
    question = "birthplace bill gates wife";
    a = sessa.answer(question);
    Assert.assertEquals("Dallas", a);
  }

  // TODO: create tests for other questions

  // TODO: create tests for accesibility to QueryProcessing & Co.
}
