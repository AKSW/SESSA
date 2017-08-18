package org.aksw.sessa.query.models;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Simon Bordewisch on 16.06.17.
 */
public class NGramEntryPositionTest {


  @Test
  public void TestGetAllDecendants_ValidInnerPosition(){
    NGramEntryPosition pos1 = new NGramEntryPosition(3, 1);
    Set<NGramEntryPosition> actualDescandants = new HashSet<>();
    actualDescandants.add(new NGramEntryPosition(2,1));
    actualDescandants.add(new NGramEntryPosition(2,2));
    actualDescandants.add(new NGramEntryPosition(1,1));
    actualDescandants.add(new NGramEntryPosition(1,2));
    actualDescandants.add(new NGramEntryPosition(1,3));
    Set<NGramEntryPosition> descandantsPos1 = pos1.getAllDescendants();
    Assert.assertEquals(actualDescandants, descandantsPos1);
  }

  @Test
  public void TestGetAllDecendants_ValidOuterRightPosition(){
    NGramEntryPosition pos2 = new NGramEntryPosition(3, 2);
    Set<NGramEntryPosition> actualDescandants = new HashSet<>();
    actualDescandants.add(new NGramEntryPosition(2,2));
    actualDescandants.add(new NGramEntryPosition(2,3));
    actualDescandants.add(new NGramEntryPosition(1,2));
    actualDescandants.add(new NGramEntryPosition(1,3));
    actualDescandants.add(new NGramEntryPosition(1,4));
    Set<NGramEntryPosition> descandantsPos2 = pos2.getAllDescendants();
    Assert.assertEquals(actualDescandants, descandantsPos2);
  }

}
