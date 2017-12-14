package org.aksw.sessa.main;

import java.io.IOException;
import java.util.Set;

import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SESSATest {

	public static final String TSV_FILE = "src/test/resources/en_surface_forms_small.tsv";
	private SESSA sessa = new SESSA();
	private String question;
	private Set<String> answer;

	@Before
	public void initialize() throws IOException{
		FileHandlerInterface handler = new TsvFileHandler(TSV_FILE);
		sessa.loadFileToHashMapDictionary(handler);
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
		question = "birthplace barack obama wife";
		answer = sessa.answer(question);
		System.out.println(answer);
		Assert.assertTrue(answer.contains("http://dbpedia.org/resource/Chicago"));
	}

	@Test
	@Ignore
	// This test is for testing interlinking between nodes. As of now, this is not possible
	// Therefore, this will be ignored
	public void testAnswer_typeQuestion() {
		question = "music by elton john type musical";
		answer = sessa.answer(question);
		System.out.println(answer);
		Assert.assertTrue(answer.contains("http://dbpedia.org/resource/The_Lion_King_(musical)"));
	}

	// TODO: create tests for other questions

	// TODO: create tests for accessibility to QueryProcessing & Co.
}
