//package org.aksw.sessa.importing.dictionary.implementation;
//
//import java.util.Set;
//import org.aksw.sessa.importing.dictionary.DictionaryInterface;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//public class ReverseTsvDictionaryImportTest {
//
//  private DictionaryInterface rtd;
//  private final String fileName = "src/test/resources/small_reverse_dictionary.tsv";
//  @Before
//  public void setUp(){
//    rtd = new ReverseTsvDictionaryImport(fileName);
//  }
//
//  @Test
//  public void get_TestLastElement(){
//    String uri = "http://dbpedia.org/resource/Andriy_Hitchenko";
//    Assert.assertTrue(rtd.get("hitchenko").contains(uri));
//  }
//
//  @Test
//  public void get_TestFirstElement(){
//    String uri = "http://dbpedia.org/resource/List_of_food_companies";
//    Assert.assertTrue(rtd.get("list of food companies").contains(uri));
//
//  }
//
//  @Test
//  public void get_TestSomeMiddleElement(){
//    String uri = "http://dbpedia.org/resource/Rutland_Elementary_School";
//    Assert.assertTrue(rtd.get("rutland elementary school").contains(uri));
//  }
//}
