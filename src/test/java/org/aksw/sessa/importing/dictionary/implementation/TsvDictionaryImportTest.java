package org.aksw.sessa.importing.dictionary.implementation;

import java.util.Set;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 21.06.17.
 */
public class TsvDictionaryImportTest {

  private Map<String, Set<String>> dictionary = null;
  private final String fileName = "src/test/resources/en_surface_forms.tsv";

  @Before
  public void init(){
    if(dictionary == null) {
      TsvDictionaryImport tdi = new TsvDictionaryImport();
      dictionary = tdi.getDictionary(fileName);
    }
  }
    @Test
    public void testgetDictionary_TestBillGates(){
      String uri = "http://dbpedia.org/resource/Bill_Gates";
      Set<String> list = dictionary.get("bill gates");
      System.out.println(list);
      Assert.assertTrue(list.contains(uri));

      uri = "http://dbpedia.org/ontology/birthplace";
      list = dictionary.get("birthplace");
      System.out.println(list);
      Assert.assertTrue(list.contains(uri));

      uri = "http://dbpedia.org/ontology/spouse";
      list = dictionary.get("wife");
      System.out.println(list);
      Assert.assertTrue(list.contains(uri));
  }
}
