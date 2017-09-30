package org.aksw.sessa.importing.dictionary.implementation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.openrdf.model.vocabulary.RDFS;

public class RdfDictionaryImport implements DictionaryImportInterface {

	@Override
	public Map<String, Set<String>> getDictionary(String fileName) {

		Map<String, Set<String>> dictionary = new HashMap<>();
		Model model = ModelFactory.createDefaultModel();
		try {
			model.read(new FileInputStream(fileName), null, "NT");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StmtIterator iter = model.listStatements();
		while (iter.hasNext()) {
			Statement statement = iter.nextStatement(); // get next statement

			String uri = statement.getPredicate().asResource().getURI();
			String rdfslabeluri = RDFS.LABEL.stringValue();
			if (uri.equals(rdfslabeluri)) {
				RDFNode object = statement.getObject();
				String surfaceform = object.asLiteral().getString();

				if (dictionary.containsKey(statement.getSubject().toString())) {
					// we see the uri a second time, and thus add the
					// surfaceform to the set
					Set<String> tmpset = dictionary.get(statement.getSubject().toString());
					tmpset.add(surfaceform);
					dictionary.put(statement.getSubject().toString(), tmpset);
				} else {
					// we see the (uri,surfaceform) pair for the first time
					Set<String> surfaceformset = new HashSet<String>();
					surfaceformset.add(surfaceform);
					dictionary.put(statement.getSubject().toString(), surfaceformset);
				}
			}
		}
		return dictionary;
	}
}