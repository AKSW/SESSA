
package org.aksw.sessa.main.urimapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.sessa.main.datastructures.MapperDataStruct;
import org.aksw.sessa.main.datastructures.NGramStruct;
import org.aksw.sessa.main.indexer.ESNode;


public class Mapper {
    private final Map<Integer,MapperDataStruct> mappings=new HashMap<Integer,MapperDataStruct>();
    

    public void BuildMappings(ESNode node, List<NGramStruct> ngramlist){
        List<String> tempURIs;
        List<String> tempLabels;
        
        for (NGramStruct ngram : ngramlist) {
            tempURIs=new ArrayList<String>();
            tempLabels=new ArrayList<String>();
            QuerySearch buildquery=new QuerySearch(node, ngram);
            List<String> uriList = buildquery.getURIList();
			List<String> labelList = buildquery.getLabelList();
			List<Double> energyScoreList = buildquery.getEnergyScoreList();
			MapperDataStruct mapping = new MapperDataStruct(uriList,labelList,energyScoreList);
            mappings.put(ngram.getIndex(), mapping);
//            System.out.println(ngram.getIndex() + ngram.getLabel());
//            System.out.println(uriList);
            
        }
    }

    public Map<Integer,MapperDataStruct> getMappings(){ return this.mappings; }
}
