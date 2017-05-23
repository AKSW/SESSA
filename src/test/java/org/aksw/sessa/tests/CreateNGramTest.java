package org.aksw.sessa.tests;

import org.aksw.sessa.main.datastructures.NGramStruct;
import org.aksw.sessa.main.ngramgenerator.NGramModel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by gekko on 20.03.17.
 */
public class CreateNGramTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void createNGramModel() throws Exception {
        String keywords = "birthplace bill gates wife";

        NGramModel nGramModel = new NGramModel();
        nGramModel.createNGramModel(keywords);

        List<NGramStruct> ngram;

        ngram = nGramModel.getNGramMod();

        List<String> result = new ArrayList<>();
        for(NGramStruct n: ngram) {
            result.add(n.getLabel());
        }
        String[] expectedStringArray = {"birthplace bill gates wife", "birthplace bill gates", "bill gates wife", "birthplace bill", "bill gates", "gates wife", "birthplace", "bill", "gates", "wife"};
        assertTrue(Arrays.equals(expectedStringArray, result.toArray()));
    }

    @Test
    public void getNGramMod() throws Exception {

    }

}