package org.aksw.sessa.importing.dictionary.implementation;

import java.util.Set;
import java.util.Map;
import org.aksw.sessa.importing.dictionary.DictionaryImportInterface;
import org.aksw.sessa.importing.dictionary.FileBasedDictionaryImport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 21.06.17.
 */
public class TsvDictionaryImportTest {

  private DictionaryImportInterface dictionary = null;
  private final String fileName = "src/test/resources/en_surface_forms_small.tsv";

  @Before
  public void init(){
    if(dictionary == null) {
      dictionary = new TsvDictionaryImport(fileName);
    }
  }

    @Test
    public void getDictionary_TestBillGates(){
      String uri = "http://dbpedia.org/resource/Bill_Gates";
      Set<String> set = dictionary.get("bill gates");
      System.out.println(set);
      Assert.assertTrue(set.contains(uri));

      uri = "http://dbpedia.org/ontology/birthPlace";
      set = dictionary.get("birthplace");
      System.out.println(set);
      Assert.assertTrue(set.contains(uri));

      uri = "http://dbpedia.org/ontology/spouse";
      set = dictionary.get("wife");
      System.out.println(set);
      Assert.assertTrue(set.contains(uri));
  }
}
