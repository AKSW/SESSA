package org.aksw.sessa.query.models;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 16.06.17.
 */
public class NGramEntryPositionTest {


  @Test
  public void TestEquals_Equal() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 1);
    Assert.assertThat(pos1, equalTo(pos1));
  }

  @Test
  public void TestEquals_Different() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 1);
    NGramEntryPosition pos2 = new NGramEntryPosition(3, 2);
    Assert.assertThat(pos1, not(equalTo(pos2)));
  }


  @Test
  public void TestGetAllDescendants_ValidInnerPosition() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 1);
    Set<NGramEntryPosition> actualDescendants = new HashSet<>();
    actualDescendants.add(new NGramEntryPosition(2, 1));
    actualDescendants.add(new NGramEntryPosition(2, 2));
    actualDescendants.add(new NGramEntryPosition(1, 1));
    actualDescendants.add(new NGramEntryPosition(1, 2));
    actualDescendants.add(new NGramEntryPosition(1, 3));
    Set<NGramEntryPosition> descendantsPos1 = pos1.getAllDescendants();
    Assert.assertEquals(actualDescendants, descendantsPos1);
  }

  @Test
  public void TestGetAllDecendants_ValidOuterRightPosition() {
    NGramEntryPosition pos2 = new NGramEntryPosition(3, 2);
    Set<NGramEntryPosition> actualDescendants = new HashSet<>();
    actualDescendants.add(new NGramEntryPosition(2, 2));
    actualDescendants.add(new NGramEntryPosition(2, 3));
    actualDescendants.add(new NGramEntryPosition(1, 2));
    actualDescendants.add(new NGramEntryPosition(1, 3));
    actualDescendants.add(new NGramEntryPosition(1, 4));
    Set<NGramEntryPosition> descendantsPos2 = pos2.getAllDescendants();
    Assert.assertEquals(actualDescendants, descendantsPos2);
  }


  @Test
  public void TestIsAncestorOf_Length2OtherPos() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 3);
    Assert.assertThat(pos1.isAncestorOf(pos2), is(true));
  }

  @Test
  public void TestIsAncestorOf_Length1SamePos() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(1, 2);
    Assert.assertThat(pos1.isAncestorOf(pos2), is(true));
  }

  @Test
  public void TestIsAncestorOf_FalseForSame() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(3, 2);
    Assert.assertThat(pos1.isAncestorOf(pos2), is(false));
  }

  @Test
  public void TestIsAncestorOf_TestCaseFalse1() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(3, 4);
    Assert.assertThat(pos1.isAncestorOf(pos2), is(false));
  }

  @Test
  public void TestIsAncestorOf_TestCaseFalse2() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 4);
    Assert.assertThat(pos1.isAncestorOf(pos2), is(false));
  }

  @Test
  public void TestIsRelatedTo_Length2OtherPos_1() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 3);
    Assert.assertThat(pos1.isRelatedTo(pos2), is(true));
    Assert.assertThat(pos2.isRelatedTo(pos1), is(true));
  }

  @Test
  public void TestIsRelatedTo_Same() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(3, 2);
    Assert.assertThat(pos1.isRelatedTo(pos2), is(true));
  }

  @Test
  public void TestIsRelatedTo_TestCaseFalse1() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 4);
    Assert.assertThat(pos2.isRelatedTo(pos1), is(false));
    Assert.assertThat(pos1.isRelatedTo(pos2), is(false));
  }

  @Test
  public void TestIsOverlappingWith_isAlsoRelated() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 3);
    Assert.assertThat(pos1.isOverlappingWith(pos2), is(true));
    Assert.assertThat(pos2.isOverlappingWith(pos1), is(true));
  }

  @Test
  public void TestIsOverlappingWith_Same() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(3, 2);
    Assert.assertThat(pos1.isOverlappingWith(pos2), is(true));
  }

  @Test
  public void TestIsOverlappingWith_TestCaseOnlyOverlap1() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 4);
    Assert.assertThat(pos2.isOverlappingWith(pos1), is(true));
    Assert.assertThat(pos1.isOverlappingWith(pos2), is(true));
  }

  @Test
  public void TestIsOverlappingWith_TestCaseFalse1() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 1);
    NGramEntryPosition pos2 = new NGramEntryPosition(1, 0);
    Assert.assertThat(pos2.isOverlappingWith(pos1), is(false));
    Assert.assertThat(pos1.isOverlappingWith(pos2), is(false));
  }

  @Test
  public void TestIsOverlappingWith_TestCaseFalse2() {
    NGramEntryPosition pos1 = new NGramEntryPosition(2, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 0);
    Assert.assertThat(pos2.isOverlappingWith(pos1), is(false));
    Assert.assertThat(pos1.isOverlappingWith(pos2), is(false));
  }

  @Test
  public void TestIsOverlappingWith_TestCaseFalse3() {
    NGramEntryPosition pos1 = new NGramEntryPosition(1, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 0);
    Assert.assertThat(pos2.isOverlappingWith(pos1), is(false));
    Assert.assertThat(pos1.isOverlappingWith(pos2), is(false));
  }

  @Test
  public void TestIsOverlappingWith_TestCaseFalse4() {
    NGramEntryPosition pos1 = new NGramEntryPosition(1, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(1, 4);
    Assert.assertThat(pos2.isOverlappingWith(pos1), is(false));
    Assert.assertThat(pos1.isOverlappingWith(pos2), is(false));
  }

  @Test
  public void TestIsMergeable_True1() {
    NGramEntryPosition pos1 = new NGramEntryPosition(1, 2);
    NGramEntryPosition pos2 = new NGramEntryPosition(1, 4);
    Assert.assertThat(pos2.isMergeable(pos1), is(true));
  }

  @Test
  public void TestIsMergeable_True2() {
    NGramEntryPosition pos1 = new NGramEntryPosition(2, 1);
    NGramEntryPosition pos2 = new NGramEntryPosition(1, 2);
    Assert.assertThat(pos2.isMergeable(pos1), is(true));
  }

  @Test
  public void TestIsMergeable_False1() {
    NGramEntryPosition pos1 = new NGramEntryPosition(2, 1);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 2);
    Assert.assertThat(pos2.isMergeable(pos1), is(false));
  }

  @Test
  public void TestIsMergeable_False3() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 1);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 3);
    Assert.assertThat(pos2.isMergeable(pos1), is(false));
  }

  @Test
  public void TestIsMergeable_ForSet_True1() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 0);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 3);
    NGramEntryPosition pos3 = new NGramEntryPosition(2, 5);
    HashSet<NGramEntryPosition> set = new HashSet<>();
    set.add(pos2);
    set.add(pos3);
    Assert.assertThat(pos1.isMergeable(set), is(true));
  }

  @Test
  public void TestIsMergeable_ForSet_False1() {
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 0);
    NGramEntryPosition pos2 = new NGramEntryPosition(2, 2);
    NGramEntryPosition pos3 = new NGramEntryPosition(2, 5);
    HashSet<NGramEntryPosition> set = new HashSet<>();
    set.add(pos2);
    set.add(pos3);
    Assert.assertThat(pos1.isMergeable(set), is(false));
  }

}
