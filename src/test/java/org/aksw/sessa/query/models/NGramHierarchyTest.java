package org.aksw.sessa.query.models;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 02.06.17.
 */
public class NGramHierarchyTest {

  private NGramHierarchy hierarchy;
  private String nGramBill = "birthplace bill gates wife";


  @Before
  public void initHierarchy() {
    hierarchy = new NGramHierarchy(nGramBill);
  }


  @Test
  public void testRootParent_null() {
    Assert.assertNull(hierarchy.getParents(4, 0));
  }

  @Test
  public void testGetNgram_first1Gram() {
    Assert.assertEquals("birthplace", hierarchy.getNGram(1, 0));
  }

  @Test
  public void testGetNgram_last1Gram() {
    Assert.assertEquals("wife", hierarchy.getNGram(1, 3));
  }

  @Test
  public void testGetNgram_first3Gram() {
    Assert.assertEquals("birthplace bill gates", hierarchy.getNGram(3, 0));
  }

  @Test
  public void testGetNgram_last3Gram() {
    Assert.assertEquals("bill gates wife", hierarchy.getNGram(3, 1));
  }

  @Test
  public void testGetParents_ofFirst2Gram() {
    String[] parents = {"birthplace bill gates"};
    Assert.assertArrayEquals(parents, hierarchy.getParents(2, 0));
  }

  @Test
  public void testGetParents_ofMiddle2Gram() {
    String[] parents = {"birthplace bill gates", "bill gates wife"};
    Assert.assertArrayEquals(parents, hierarchy.getParents(2, 1));
  }

  @Test
  public void testGetParents_ofLast2Gram() {
    String[] parents = {"bill gates wife"};
    Assert.assertArrayEquals(parents, hierarchy.getParents(2, 2));
  }

  @Test
  public void testGetParents_last1Gram() {
    String[] parents = {"gates wife"};
    Assert.assertArrayEquals(parents, hierarchy.getParents(1, 3));
  }

  @Test
  public void testGetDirectChildren_ofRoot() {
    String[] parents = null;
    //Assert.assertArrayEquals(parents, hierarchy.getParents(1,1));
    Assert.assertNull(hierarchy.getDirectChildren(1, 1));
  }

  @Test
  public void testGetDirectChildren_ofFirst2Ngram() {
    String[] parents = {"birthplace", "bill"};
    Assert.assertArrayEquals(parents, hierarchy.getDirectChildren(2, 0));
  }

  @Test
  public void testToStringArray() {
    String[] hierarchyArray = {"birthplace bill gates wife",
        "birthplace bill gates", "bill gates wife",
        "birthplace bill", "bill gates", "gates wife",
        "birthplace", "bill", "gates", "wife"};
    Assert.assertArrayEquals(hierarchyArray, hierarchy.toStringArray());
  }

  @Test
  public void testGetAllPositions() {
    Set<NGramEntryPosition> hierarchyPositions = new HashSet<>();
    hierarchyPositions.add(new NGramEntryPosition(4, 0));

    hierarchyPositions.add(new NGramEntryPosition(3, 0));
    hierarchyPositions.add(new NGramEntryPosition(3, 1));

    hierarchyPositions.add(new NGramEntryPosition(2, 0));
    hierarchyPositions.add(new NGramEntryPosition(2, 1));
    hierarchyPositions.add(new NGramEntryPosition(2, 2));

    hierarchyPositions.add(new NGramEntryPosition(1, 0));
    hierarchyPositions.add(new NGramEntryPosition(1, 1));
    hierarchyPositions.add(new NGramEntryPosition(1, 2));
    hierarchyPositions.add(new NGramEntryPosition(1, 3));

    Assert.assertEquals(hierarchyPositions, hierarchy.getAllPositions());
  }
}
