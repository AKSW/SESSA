package org.aksw.sessa.main;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.sessa.candidate.CandidateGenerator;
import org.aksw.sessa.colorspreading.ColorSpreader;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.aksw.sessa.importing.dictionary.implementation.RdfDictionaryImport;
import org.aksw.sessa.importing.dictionary.implementation.TsvDictionaryImport;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.aksw.sessa.query.models.NGramHierarchy;
import org.aksw.sessa.query.processing.QueryProcessingInterface;
import org.aksw.sessa.query.processing.implementation.SimpleQueryProcessing;

/**
 * Main class of project SESSA, which returns answers to asked questions.
 */
public class SESSA {

	private Map<String, Set<String>> dictionary;
	private QueryProcessingInterface queryProcess;

	/**
	 * 
	 */

	public SESSA() {
		queryProcess = new SimpleQueryProcessing();
	}

	/**
	 * loading of files to a dictionary there by reading all rdfs:labels from a
	 * file
	 * 
	 * @param file
	 */
	public void loadFileToDictionaryRDF(String file) {
		DictionaryImportInterface dictImporter = new RdfDictionaryImport();
		loadDictionaryDataset(file, dictImporter);
	}

	/**
	 * Loads a dictionary from a tsv file which contains a dictionary with URIs
	 * as key and keywords to the URI as values (tab separated). This is needed
	 * for the candidate mapping.
	 *
	 * @param fileName
	 *            tsv file which contains mapping.
	 */

	public void loadFileToDictionaryTSV(String file) {
		DictionaryImportInterface dictImporter = new TsvDictionaryImport();
		loadDictionaryDataset(file, dictImporter);

	}

	private void loadDictionaryDataset(String file, DictionaryImportInterface dictImporter) {
		if (dictionary == null) {
			dictionary = dictImporter.getDictionary(file);
		} else {
			dictionary.putAll(dictImporter.getDictionary(file));
		}
	}

	/**
	 * This method tries to answer the given question using the method
	 * descripted in the <a href=
	 * "https://docs.google.com/viewer?a=v&pid=sites&srcid=ZGVmYXVsdGRvbWFpbnxubGl3b2QyMDE0fGd4Ojc5NjU1YjhhMzNhMDczNWI"
	 * >corresponding paper</a>. The answer is a set of strings containg the
	 * URIs with the highest likelihood to be the answer (i.e. with the highest
	 * explanation score).
	 *
	 * @param question
	 *            the question that should be answered (for now keyword based,
	 *            i.e. 'birthplace bill gates wife' instead of
	 *            "Where was Bill Gates' wife born")
	 * @return set of URIs with the highest explanation score
	 */
	public Set<String> answer(String question) {
		if (question.equals("")) {
			return null;
		} else {
			NGramHierarchy nGramHierarchy = queryProcess.processQuery(question);
			CandidateGenerator canGen = new CandidateGenerator(dictionary);
			Map<NGramEntryPosition, Set<String>> canMap = canGen.getCandidateMapping(nGramHierarchy);
			ColorSpreader colorSpreader = new ColorSpreader(canMap);
			colorSpreader.spreadColors();
			Set<Node> results = colorSpreader.getResult();
			Set<String> stringResults = new HashSet<>();
			for (Node result : results) {
				stringResults.add(result.getContent().toString());
			}
			return stringResults;
		}
	}
}
