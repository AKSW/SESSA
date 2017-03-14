package org.aksw.sessa.main.ngramgenerator.interfaces;

import java.util.List;

import org.aksw.sessa.main.datastructures.NGramStruct;

public interface NGramInterface {
    public void CreateNGramModel(String keyword);
    public List<NGramStruct> getNGramMod();
}
