package org.aksw.sessa.main.urimapper.interfaces;

import java.util.List;
import java.util.Map;

import org.aksw.sessa.main.datastructures.MapperDataStruct;
import org.aksw.sessa.main.indexer.Interface.IndexerInterface;
import org.aksw.sessa.main.datastructures.NGramStruct;

public interface MapperInterface {
    public void BuildMappings(IndexerInterface node, List<NGramStruct> ngramlist);
    public Map<Integer,MapperDataStruct> getMappings();
}
