//package org.aksw.sessa.importing.dictionary.implementation;
//import java.util.Map;
//import java.util.Set;
//
//import org.aksw.sessa.importing.dictionary.implementation.RdfDictionaryImport;
//import org.junit.Test;
//
//import junit.framework.Assert;
//
//@SuppressWarnings("deprecation")
//public class RdfDictionaryTest {
//
//	@Test
//	public void rdfDictionaryOneLine() {
//
//		String NT_FILE = "src/test/resources/test_twoURI_fourSF.nt";
//		RdfDictionaryImport dict = new RdfDictionaryImport(NT_FILE);
//		System.out.println(dict.entrySet());
//
//		Set<String> set = dict.get("afghanistanhistory");
//		String o = "http://dbpedia.org/resource/AfghanistanHistory";
//
//		System.out.println("Size of the surface form set for dbr:AfghanistanHistory " + set.size());
//		System.out.println("Target surface form AfghanistanHistory" + o);
//
//		Assert.assertTrue(set.contains(o));
//	}
//
//	@Test
//	public void rdfDictionaryLabelsAndOntology() {
//
//		String file_sample_dbpedia_ontology = "src/test/resources/file_sample_dbpedia_ontology.nt";
//		String file_sample_dbpedia_labels = "src/test/resources/file_sample_dbpedia_labels.nt";
//
//		RdfDictionaryImport dict = new RdfDictionaryImport(file_sample_dbpedia_ontology);
//		dict.putAll(file_sample_dbpedia_labels);
//
//		// checking surface forms from the ontology file
//		Set<String> surfaceFormSet = dict.get("lunar crater");
//		String targetSurfaceForm = "http://dbpedia.org/ontology/LunarCrater";
//
//		Assert.assertTrue(surfaceFormSet!=null && surfaceFormSet.contains(targetSurfaceForm));
//
//		// checking surface forms from the labels file
//		surfaceFormSet = dict.get("actionfilm");
//		targetSurfaceForm = "http://dbpedia.org/resource/ActionFilm";
//
//		Assert.assertTrue(surfaceFormSet.contains(targetSurfaceForm));
//	}
//}
