package org.aksw.sessa.main.ngramgenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.aksw.sessa.main.datastructures.ListFunctions;
import org.aksw.sessa.main.datastructures.NGramStruct;
import org.aksw.sessa.main.ngramgenerator.interfaces.NGramInterface;

/* This is the class for creating Ngram hierarchies out of the given query */
public class NGramModel implements NGramInterface {

	private List<NGramStruct> ngrammod = new ArrayList<NGramStruct>();

	/* Initializing n-gram hierarchy with root node */
	@Override
	public void CreateNGramModel(String keyword) {
		ngramhierarchy(0, 1, 0, 0, 0, 0, countchar(keyword), keyword);
	}

	private int countchar(String keyword) {
		int counter = 0;
		for (int i = 0; i < keyword.length(); i++) {
			if (keyword.charAt(i) == ' ') {
				counter++;
			}
		}
		return counter;
	}

	/* Creating the n-gram hierarchy recursively */
	private void ngramhierarchy(int i, int height, int node, int first, int feed, int begin, int end, String keyword) {
		if (height == 4) {
			feed = 1; /*
					 * Initialization of feed number ( to prevent error in
					 * indexing created from presence of common nodes)
					 */
		}
		if (height > 2) {
			first = first + feed;
			feed = feed + 1;
			if (node == 0 || node == 1) {
				i = i - first; /*
								 * Correcting the error in indexing using feeds
								 * that occur because of commong nodes
								 */
			} else {
				i = i - first - node + 1;
			}
		}
		NGramStruct middlenode = new NGramStruct(i, begin, end, keyword);
		if (!ListFunctions.Containselement(middlenode, this.ngrammod)) {
			this.ngrammod.add(middlenode);

		} else {
			return;
		}

		/* Edge Case */
		if (!keyword.contains(" ")) {
			return;
		}

		/* Recusively sending the n-grams */
		String leftstring = keyword.substring(0, keyword.lastIndexOf(" "));
		String rightstring = keyword.substring(keyword.indexOf(" ") + 1);
		ngramhierarchy((2 * i + 1), height + 1, node, first, feed, begin, end - 1, leftstring);
		ngramhierarchy((2 * i + 2), height + 1, node + 1, first, feed, begin + 1, end, rightstring);
	}

	/* Returning the sorted N-gram hierarchy */
	@Override
	public List<NGramStruct> getNGramMod() {
		// List<NGramStruct> modngrams=new
		// ArrayList<NGramStruct>(this.ngrammod.size());
		// for(int i=0;i<this.ngrammod.size();i++){
		// NGramStruct element = this.ngrammod.get(i);
		// int index = element.getIndex();
		// if(index>=modngrams.size())
		// {
		// System.out.println();
		// }
		// modngrams.set(index,element);
		// }
		Collections.sort(ngrammod, new Comparator<NGramStruct>() {
			@Override
			public int compare(NGramStruct o1, NGramStruct o2) {
				return Integer.compare(o1.getIndex(), o2.getIndex());
			}
		});
		return ngrammod;
	}
}
