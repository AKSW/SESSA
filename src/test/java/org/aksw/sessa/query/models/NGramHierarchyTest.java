package org.aksw.sessa.query.models;

import org.aksw.sessa.query.models.NGramHierarchy;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 02.06.17.
 */
public class NGramHierarchyTest {

  NGramHierarchy hierarchy;
  String ngramBill = "birthplace bill gates wife";;


  @Test
  public void testRootParent_null() {
    hierarchy = new NGramHierarchy(ngramBill);
    Assert.assertNull(hierarchy.getParents(0,4));
  }
  @Test
  public void testGetNgram_first1Gram() {
    hierarchy = new NGramHierarchy(ngramBill);
    Assert.assertEquals("birthplace", hierarchy.getNGram(0,1));
  }

  @Test
  public void testGetNgram_last1Gram() {
    hierarchy = new NGramHierarchy(ngramBill);
    Assert.assertEquals("wife", hierarchy.getNGram(3,1));
  }

  @Test
  public void testGetNgram_first3Gram() {
    hierarchy = new NGramHierarchy(ngramBill);
    Assert.assertEquals("birthplace bill gates", hierarchy.getNGram(0,3));
  }

  @Test
  public void testGetNgram_last3Gram() {
    hierarchy = new NGramHierarchy(ngramBill);
    Assert.assertEquals("bill gates wife", hierarchy.getNGram(1,3));
  }

  @Test
  public void testGetParents_ofFirst2Gram() {
    hierarchy = new NGramHierarchy(ngramBill);
    String[] parents = {"birthplace bill gates"};
    Assert.assertArrayEquals(parents, hierarchy.getParents(0,2));
  }

  @Test
  public void testGetParents_ofMiddle2Gram() {
    hierarchy = new NGramHierarchy(ngramBill);
    String[] parents = {"birthplace bill gates", "bill gates wife"};
    Assert.assertArrayEquals(parents, hierarchy.getParents(1,2));
  }

  @Test
  public void testGetParents_ofLast2Gram() {
    hierarchy = new NGramHierarchy(ngramBill);
    String[] parents = {"bill gates wife"};
    Assert.assertArrayEquals(parents, hierarchy.getParents(2,2));
  }

  @Test
  public void testGetParents_last1Gram() {
    hierarchy = new NGramHierarchy(ngramBill);
    String[] parents = {"gates wife"};
    Assert.assertArrayEquals(parents, hierarchy.getParents(3,1));
  }

  @Test
  public void testGetDirectChildren_ofRoot() {
    hierarchy = new NGramHierarchy(ngramBill);
    String[] parents = null;
    //Assert.assertArrayEquals(parents, hierarchy.getParents(1,1));
    Assert.assertNull(hierarchy.getDirectChildren(1,1));
  }

  @Test
  public void testGetDirectChildren_ofFirst2Ngram() {
    hierarchy = new NGramHierarchy(ngramBill);
    String[] parents = {"birthplace", "bill"};
    Assert.assertArrayEquals(parents, hierarchy.getDirectChildren(0,2));
  }

  @Test
  public void testToStringArray() {
    hierarchy = new NGramHierarchy(ngramBill);
    String[] hierarchyArray = {"birthplace bill gates wife",
                               "birthplace bill gates", "bill gates wife",
                               "birthplace bill", "bill gates", "gates wife",
                               "birthplace", "bill", "gates", "wife"};
    Assert.assertArrayEquals(hierarchyArray, hierarchy.toStringArray());
  }
}
