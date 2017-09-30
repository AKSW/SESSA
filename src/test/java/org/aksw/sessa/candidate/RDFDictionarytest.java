package org.aksw.sessa.candidate;
import java.util.Map;
import java.util.Set;

import org.aksw.sessa.importing.dictionary.implementation.RdfDictionaryImport;
import org.junit.Test;

import junit.framework.Assert;

@SuppressWarnings("deprecation")
public class RDFDictionarytest {

	@Test
	public void rdfdictionaryOneLine() {
		RdfDictionaryImport rdfdictimporter = new RdfDictionaryImport();

		String NT_FILE = "src/test/resources/test_twoURI_fourSF.nt";
		Map<String, Set<String>> dict = rdfdictimporter.getDictionary(NT_FILE);

		int size = dict.size();
		Set<String> set = dict.get("http://dbpedia.org/resource/AfghanistanHistory");
		String o = "AfghanistanHistory";

		System.out.println("Size of the dictionary " + size);
		System.out.println("Size of the surface form set for dbr:AfghanistanHistory " + set.size());
		System.out.println("Target surface form AfghanistanHistory" + o);

		Assert.assertTrue(set.contains(o));
	}

	@Test
	public void rdfdictionaryLabelsAndOntology() {
		RdfDictionaryImport rdfdictimporter = new RdfDictionaryImport();

		String file_sample_dbpedia_ontology = "src/test/resources/file_sample_dbpedia_ontology.nt";
		String file_sample_dbpedia_labels = "src/test/resources/file_sample_dbpedia_labels.nt";

		Map<String, Set<String>> dict = rdfdictimporter.getDictionary(file_sample_dbpedia_ontology);
		dict.putAll(rdfdictimporter.getDictionary(file_sample_dbpedia_labels));

		int size = dict.size();
		System.out.println("Size of the dictionary " + size);

		// checking surface forms from the ontology file
		Set<String> surfaceformset = dict.get("http://dbpedia.org/ontology/LunarCrater");
		String target_surfaceform = "lunar crater";

		Assert.assertTrue(surfaceformset.contains(target_surfaceform));

		// checking surface forms from the labels file
		surfaceformset = dict.get("http://dbpedia.org/resource/ActionFilm");
		target_surfaceform = "ActionFilm";

		Assert.assertTrue(surfaceformset.contains(target_surfaceform));
	}
}
