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
		Set<String> set = dict.get("AfghanistanHistory");
		String o = "http://dbpedia.org/resource/AfghanistanHistory";

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
		Set<String> surfaceformset = dict.get("lunar crater");
		String target_surfaceform = "http://dbpedia.org/ontology/LunarCrater";

		Assert.assertTrue(surfaceformset.contains(target_surfaceform));

		// checking surface forms from the labels file
		surfaceformset = dict.get("ActionFilm");
		target_surfaceform = "http://dbpedia.org/resource/ActionFilm";

		Assert.assertTrue(surfaceformset.contains(target_surfaceform));
	}
}
