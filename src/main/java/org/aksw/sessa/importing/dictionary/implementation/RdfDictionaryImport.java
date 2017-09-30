package org.aksw.sessa.importing.dictionary.implementation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.openrdf.model.vocabulary.RDFS;

public class RdfDictionaryImport implements DictionaryImportInterface {

	@Override
	public Map<String, Set<String>> getDictionary(String fileName) {

		Map<String, Set<String>> dictionary = new HashMap<>();

		StreamRDF destination = new StreamRDF() {

			@Override
			public void triple(Triple statement) {

				String uri = statement.getPredicate().getURI();
				String rdfslabeluri = RDFS.LABEL.stringValue();
				if (uri.equals(rdfslabeluri)) {
					Node object = statement.getObject();
					String surfaceform = object.getLiteral().getLexicalForm();

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

			@Override
			public void base(String arg0) {
			}

			@Override
			public void finish() {
			}

			@Override
			public void prefix(String arg0, String arg1) {
			}

			@Override
			public void quad(Quad arg0) {
			}

			@Override
			public void start() {
			}

		};
		RDFDataMgr.parse(destination, fileName);

		return dictionary;
	}

}