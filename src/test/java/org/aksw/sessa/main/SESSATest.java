package org.aksw.sessa.main;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SESSATest {

	public static final String TSV_FILE = "src/test/resources/en_surface_forms_small.tsv";
	SESSA sessa = new SESSA();
	String question;
	Set<String> answer;

	@Before
	public void initialize() {
		sessa.loadFileToDictionaryTSV(TSV_FILE);
	}

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
		System.out.println(answer);
		Assert.assertTrue(answer.contains("http://dbpedia.org/resource/Dallas"));
	}

	@Test
	public void testAnswer_onObamaExample() {
		String TSV_FILE = "src/test/resources/en_surface_forms_small.tsv";
		SESSA sessa = new SESSA();
		sessa.loadFileToDictionaryTSV(TSV_FILE);

		question = "birthplace barack obama wife";
		answer = sessa.answer(question);
		System.out.println(answer);
		Assert.assertTrue(answer.contains("http://dbpedia.org/resource/Chicago"));
	}

	// TODO: create tests for other questions

	// TODO: create tests for accessibility to QueryProcessing & Co.
}
