package org.aksw.sessa.main;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.sessa.candidate.CandidateGenerator;
import org.aksw.sessa.colorspreading.ColorSpreader;
import org.aksw.sessa.helper.graph.Node;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.aksw.sessa.importing.dictionary.FileBasedDictionaryImport;
import org.aksw.sessa.importing.dictionary.implementation.ReverseTsvDictionaryImport;
import org.aksw.sessa.importing.dictionary.implementation.RdfDictionaryImport;
import org.aksw.sessa.importing.dictionary.implementation.TsvDictionaryImport;
import org.aksw.sessa.query.models.NGramEntryPosition;
import org.aksw.sessa.query.models.NGramHierarchy;
import org.aksw.sessa.query.processing.QueryProcessingInterface;
import org.aksw.sessa.query.processing.implementation.SimpleQueryProcessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of project SESSA, which returns answers to asked questions.
 */
public class SESSA {

	private DictionaryImportInterface dictionary;
	private QueryProcessingInterface queryProcess;
	private static final Logger log = LoggerFactory.getLogger(SESSA.class);

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
	private void loadFileToDictionaryRDF(String file) {
		log.info("Loading RDF file '{}' to dictionary.", file);
		if(dictionary == null){
			dictionary = new RdfDictionaryImport(file);
		} else if(dictionary instanceof RdfDictionaryImport){
			((FileBasedDictionaryImport)dictionary).putAll(file);
		}
	}


	/**
	 * Loads a dictionary from a tsv file which contains a dictionary with URIs
	 * as key and keywords to the URI as values (tab separated). This is needed
	 * for the candidate mapping.
	 *
	 * @param file
	 *            tsv file which contains mapping.
	 */

	public void loadFileToDictionaryTSV(String file) {
		log.info("Loading TSV file '{}' to dictionary", file);
		if(dictionary == null){
			dictionary = new TsvDictionaryImport(file);
		} else if(dictionary instanceof TsvDictionaryImport){
			((FileBasedDictionaryImport)dictionary).putAll(file);
		}
	}


	public void loadFileToDictionaryReverseTSV(String file) {
		log.info("Loading reverse TSV file '{}' to dictionary", file);
		if(dictionary == null){
			dictionary = new ReverseTsvDictionaryImport(file);
		} else if(dictionary instanceof ReverseTsvDictionaryImport){
			((FileBasedDictionaryImport)dictionary).putAll(file);
		}
	}

	/**
	 * This method tries to answer the given question using the method
	 * described in the <a href=
	 * "https://docs.google.com/viewer?a=v&pid=sites&srcid=ZGVmYXVsdGRvbWFpbnxubGl3b2QyMDE0fGd4Ojc5NjU1YjhhMzNhMDczNWI"
	 * >corresponding paper</a>. The answer is a set of strings containing the
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
			log.debug("Candidate map content: {}", canMap.toString());
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
