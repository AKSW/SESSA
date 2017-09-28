
import java.util.Map;
import java.util.Set;

import org.aksw.sessa.importing.dictionary.implementation.RdfDictionaryImport;
import org.junit.Test;

import junit.framework.Assert;

@SuppressWarnings("deprecation")
public class RDFDictionarytest {

	public static final String NT_FILE = "src/test/resources/test_oneline.nt";

	@Test
	public void rdfdictionaryOneLine() {
		RdfDictionaryImport rdfdictimporter = new RdfDictionaryImport();

		Map<String, Set<String>> dict = rdfdictimporter.getDictionary(NT_FILE);

		int size = dict.size();
		Set<String> set = dict.get("http://dbpedia.org/resource/AfghanistanHistory");
		String o = "AfghanistanHistory";

		System.out.println("Size of the dictionary_1 " + size);
		System.out.println("Size of the surface form set " + set.size());
		System.out.println("Target surface form " + o);

		// Assert.assertTrue(size == 1);
		Assert.assertTrue(set.contains(o));
		System.out.println("WHY ?");
		// Assert.assertTrue(dict.get("http://dbpedia.org/resource/AfghanistanMilitary").size()
		// == 3);
	}

	@Test
	public void rdfdictionaryLabelsAndOntology() {
		RdfDictionaryImport rdfdictimporter = new RdfDictionaryImport();

		Map<String, Set<String>> dict = rdfdictimporter
				.getDictionary("/home/abddatascienceadmin/Downloads/dbpedia/test_1.nt");
		// Map<String, Set<String>> dict =
		// rdfdictimporter.getDictionary("/home/abddatascienceadmin/Downloads/dbpedia/test_2.ttl");
		dict.putAll(rdfdictimporter.getDictionary("/home/abddatascienceadmin/Downloads/dbpedia/test_2.ttl"));
		int size = dict.size();
		Set<String> set_1 = dict.get("http://dbpedia.org/ontology/");
		String o_1 = "DBpedia Maintainers";
		System.out.println("Size of the surface form set_1 " + set_1.size());

		System.out.println("Size of the dictionary " + size);
		Assert.assertTrue(set_1.contains(o_1));

	}
}
